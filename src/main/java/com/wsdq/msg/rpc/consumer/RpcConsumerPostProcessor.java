package com.wsdq.msg.rpc.consumer;

import com.wsdq.msg.rpc.consumer.annotation.RpcReference;
import com.wsdq.msg.rpc.core.common.RpcConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * bean 加工处理类， 将本地接口代理为代理类 填充 invoke 方法
 *
 * /---------------------------------------------------------/
 * / ApplicationContextAware  | 获取    ApplicationContext   /
 * /---------------------------------------------------------/
 * / BeanClassLoaderAware     | 获取    ClassLoader          /
 * /---------------------------------------------------------/
 * / BeanFactoryPostProcessor | 获取    bean处理单元          /
 * /---------------------------------------------------------/
 *
 */
@Component
@Slf4j
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {

    private ApplicationContext context;

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new LinkedHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 获取内部bean工厂内部bean
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                // 反射工具类 增加回调方法
                ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
            }
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        // 通过回调方法，将代理好的类放入 spring 容器
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("spring context already has a bean named " + beanName);
            }
            registry.registerBeanDefinition(beanName, rpcRefBeanDefinitions.get(beanName));
            log.info("registered RpcReferenceBean {} success.", beanName);
        });
    }

    /**
     *
     * 反射 回调方法
     *
     * @param field
     */
    private void parseRpcReference(Field field) {

        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);

        //只有添加RPC注解是 内部 RPC 调用类
        if (annotation != null) {

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);

            //设置初始化方法
            builder.setInitMethodName(RpcConstants.INIT_METHOD_NAME);

            //设置 RpcReferenceBean 的 属性值 也是基于反射
            builder.addPropertyValue("interfaceClass", field.getType());

            builder.addPropertyValue("serviceVersion", annotation.serviceVersion());

            builder.addPropertyValue("timeout", annotation.timeout());

            BeanDefinition beanDefinition = builder.getBeanDefinition();
            //放入集合
            rpcRefBeanDefinitions.put(field.getName(), beanDefinition);

        }
    }

}
