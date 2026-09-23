package cn.ivfzhou.java.agentscope.a2a.client;

import io.a2a.spec.AgentCard;
import io.agentscope.core.a2a.agent.A2aAgent;
import io.agentscope.core.a2a.agent.card.WellKnownAgentCardResolver;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Sample {

    static void main(String[] args) throws IOException {
        var resolver = WellKnownAgentCardResolver.builder()
                .baseUrl("http://127.0.0.1:8080")
                // .relativeCardPath("/.well-known/agent-card.json")
                // .authHeaders(Map.of("Authorization", "Bearer xxx"))
                .build();

        printCard(resolver.getAgentCard("a2a-demo-agent"));

        // A2aAgent.builder()
        //         .name("a2a-demo-agent")
        //         .agentCard(new AgentCard.Builder()
        //                 .name("a2a-demo-agent")
        //                 .description("")
        //                 .version("1.0.0")
        //                 .url("http://127.0.0.1:8080/a2a")
        //                 .capabilities(new AgentCapabilities.Builder().streaming(true).build())
        //                 .defaultInputModes(List.of("text")).defaultOutputModes(List.of("text"))
        //                 .skills(List.of()).build())
        //         .build();
        var remote = A2aAgent.builder()
                .name("a2a-demo-agent")
                .agentCardResolver(resolver)
                .build();

        chat(remote);
    }

    private static void chat(A2aAgent remote) throws IOException {
        System.out.println("enter message to chat:(quit)");
        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try (reader) {
            var ask = reader.readLine();
            while (null != ask) {
                if (ask.equalsIgnoreCase("quit")) {
                    break;
                }
                try {
                    printMsg(remote.call(new UserMessage(ask)).block());
                } catch (Exception e) {
                    System.out.println("[ERROR] " + e.getMessage());
                }
                ask = reader.readLine();
            }
        }
    }

    private static void printCard(AgentCard card) {
        System.out.println("[CARD] name=" + card.name() + " version=" + card.version());
        System.out.println("[CARD] description=" + card.description());
        System.out.println("[CARD] url=" + card.url() + " transport=" + card.preferredTransport());
        System.out.println("[CARD] protocolVersion=" + card.protocolVersion() + " streaming=" + card.capabilities().streaming());
    }

    private static void printMsg(Msg msg) {
        System.out.println();
        System.out.println("[ID] " + msg.getId());
        System.out.println("[Name] " + msg.getName());
        System.out.println("[Role] " + msg.getRole());
        System.out.println("[Reason] " + msg.getGenerateReason());
        System.out.println("[Content] " + msg.getContent());
        System.out.println("[Text] " + msg.getTextContent());
    }

}
