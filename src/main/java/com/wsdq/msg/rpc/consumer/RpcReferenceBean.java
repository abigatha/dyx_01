package com.wsdq.msg.rpc.consumer;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;


/**
 *
 * 内部 RPC 调用类 反射生成类
 *
 */
public class RpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private String serviceVersion;

    private long timeout;

    private Object object;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    // BeanDefinitionBuilder 构建 BeanDefinition  并指定初始化对象方法 init()
    public void init() throws Exception {
        // 通过动态代理类 生成代理对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new RpcInvokerCglibProxy(serviceVersion, timeout));
        this.object = enhancer.create();

    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
