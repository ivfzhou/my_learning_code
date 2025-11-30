package cn.ivfzhou.java.zookeeper;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooKeeper;

public final class ZooKeeperUtil {

    public static ZooKeeper create() throws IOException {
        return new ZooKeeper("ivfzhoudockerzookeeper:2181", 1000, null);
    }

    public static CuratorFramework cf() {
        var retryPolicy = new ExponentialBackoffRetry(3000, 2);
        var cf = CuratorFrameworkFactory.builder()
                .connectString("ivfzhoudockerzookeeper_0:2181,ivfzhoudockerzookeeper_1:2182,ivfzhoudockerzookeeper_2:2183")
                .retryPolicy(retryPolicy)
                .build();
        cf.start();
        return cf;
    }


}
