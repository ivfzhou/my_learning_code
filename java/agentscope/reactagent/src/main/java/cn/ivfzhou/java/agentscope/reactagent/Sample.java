package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;

public class Sample {

    static void main() {
        var agent = ReActAgent.builder()
                .name("note-taker")
                .sysPrompt("你是一个帮助用户做笔记的助手。")
                .model("dashscope:qwen-plus")
                .build();
        var ctx = RuntimeContext.builder()
                .userId("zhangsan")
                .sessionId("session1")
                .build();
        var msg = agent.call("我叫张三，今天准备一个关于 ReAct 的技术分享。", ctx).block();
        System.out.println("ChatUsage：" + msg.getChatUsage());
        System.out.println("Role：" + msg.getRole());
        System.out.println("ID：" + msg.getId());
        System.out.println("Name：" + msg.getName());
        System.out.println("TextContent：" + msg.getTextContent());
        System.out.println("Timestamp：" + msg.getTimestamp());
        System.out.println("Metadata：");
        msg.getMetadata().forEach((key, value) -> System.out.println("    " + key + ": " + value));
        System.out.println("GenerateReason：" + msg.getGenerateReason());
        System.out.println("Content：");
        msg.getContent().forEach(v -> System.out.println("    " + v));
        agent.close();
    }

}
