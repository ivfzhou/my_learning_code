package cn.ivfzhou.java.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;

public class ZooKeeperTest {

    private final ZooKeeper zk = ZooKeeperUtil.create();

    private final String path = "/test";

    public ZooKeeperTest() throws IOException {}

    // 创建一个目录节点。
    @Test
    public void testCreate() throws InterruptedException, KeeperException {
        System.out.println(zk.create(
                path, "data你".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
    }

    @Test
    public void testExist() throws InterruptedException, KeeperException {
        System.out.println(zk.exists(path, false).getDataLength());
    }

    @Test
    public void testGetData() throws InterruptedException, KeeperException {
        System.out.println(new String(zk.getData(path, false, null)));
    }

    @Test
    public void testSetData() throws InterruptedException, KeeperException {
        System.out.println(zk.setData(path, "data好".getBytes(), -1).getVersion());
    }

    @Test
    public void testDelete() throws InterruptedException, KeeperException {
        zk.delete(path, -1);
    }

    @Test
    public void testClose() throws InterruptedException {
        zk.close();
    }

}
