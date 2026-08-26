package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionDecision;
import io.agentscope.core.tool.ToolBase;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class ShellCommandTool extends ToolBase {

    public ShellCommandTool() {
        super(ToolBase.builder()
                .name("run_command")
                .description("在宿主机上执行一条 shell 命令并返回其输出，例如 echo、hostname、dir 等。")
                .inputSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "command", Map.of(
                                        "type", "string",
                                        "description", "要执行的命令"
                                )
                        ),
                        "required", List.of("command")))
                .externalTool(true) // 关键：标记为外部工具。
        );
    }

    @Override
    public Mono<PermissionDecision> checkPermissions(Map<String, Object> toolInput, PermissionContextState context) {
        return Mono.just(PermissionDecision.allow("external dispatch"));
    }

    // 注意：不实现 callAsync，执行交给外部（即你在 execute 里手动回填结果）。
}
