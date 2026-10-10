package cn.ivfzhou.java.dubbo.spring.protocol;

/**
 * 公共协议接口（protocol 模块）。
 *
 * <p>该模块是服务提供方（provider）与服务消费方（consumer）共同依赖的“契约”，
 * 双方通过此接口约定服务的方法签名，保证远程调用的类型一致性。</p>
 */
public interface IUserService {

    /**
     * 一个简单的 ping 方法，用于验证 Dubbo 远程调用链路是否连通。
     *
     * @return 固定返回 "pong"
     */
    String ping();

}
