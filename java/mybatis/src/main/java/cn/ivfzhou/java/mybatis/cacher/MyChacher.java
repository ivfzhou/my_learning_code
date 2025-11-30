package cn.ivfzhou.java.mybatis.cacher;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.builder.InitializingObject;
import org.apache.ibatis.cache.Cache;

public class MyChacher implements Cache, InitializingObject {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void putObject(Object key, Object value) {}

    @Override
    public Object getObject(Object key) {
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        return null;
    }

    @Override
    public void clear() {}

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return Cache.super.getReadWriteLock();
    }

    /*
     * 初始化，设置完属性后调用。
     * */
    @Override
    public void initialize() throws Exception {}

}
