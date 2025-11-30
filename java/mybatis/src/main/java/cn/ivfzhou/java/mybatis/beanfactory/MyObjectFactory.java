package cn.ivfzhou.java.mybatis.beanfactory;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

/*
 * 创建 JavaBean 时会通过该拦截器。xml配置需要配置该类。
 * */
public class MyObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        System.out.println("MyObjectFactory.create1：" + type);
        return super.create(type);
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        System.out.println("MyObjectFactory.create2：" + type);
        return super.create(type, constructorArgTypes, constructorArgs);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("MyObjectFactory.setProperties：" + properties);
        super.setProperties(properties);
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }

}
