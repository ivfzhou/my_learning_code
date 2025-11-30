package cn.ivfzhou.java.zookeeper;

import java.nio.charset.StandardCharsets;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.junit.jupiter.api.Test;

public class ZooKeeperPoolTest {

    private final CuratorFramework cf = ZooKeeperUtil.cf();

    private final String path = "/test";

    // 获取子节点。
    @Test
    public void testGetChildren() throws Exception {
        var elems = cf.getChildren().forPath(path);
        for (var v : elems) {
            System.out.println(v);
        }
    }

    // 获取节点数据。
    @Test
    public void testGetData() throws Exception {
        byte[] bytes = cf.getData().forPath(path);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

    // 添加。
    @Test
    public void testCreate() throws Exception {
        System.out.println(cf.create().withMode(CreateMode.PERSISTENT).forPath(path, "data你".getBytes()));
    }

    // 修改。
    @Test
    public void testUpdate() throws Exception {
        System.out.println(cf.setData().forPath(path, "data好".getBytes()));
    }

    // 删除。
    @Test
    public void testDelete() throws Exception {
        System.out.println(cf.delete().deletingChildrenIfNeeded().forPath(path));
    }

    // 查看 znode 的状态。
    @Test
    public void testStat() throws Exception {
        System.out.println(cf.checkExists().forPath(path));
    }

    // 监听通知机制。
    @Test
    public void testListen() throws Exception {
        // 1. 创建 CuratorCache 对象
        var cache = CuratorCache.build(cf, path);

        // 2. 创建并注册监听器
        cache.listenable().addListener(CuratorCacheListener.builder()
                .forNodeCache(() -> {
                    var data = cache.get(path).orElse(null);
                    if (data != null) {
                        System.out.println("监听的节点是：" + data.getPath());
                        System.out.println("节点现在的数据是：" + new String(data.getData(), StandardCharsets.UTF_8));
                        System.out.println("节点状态是：" + data.getStat());
                    } else {
                        System.out.println("节点已删除: " + path);
                    }
                })
                .build());

        // 3. 启动缓存
        cache.start();

        System.out.println("开始监听！！");

        System.in.read(); // 阻塞等待
    }

}
