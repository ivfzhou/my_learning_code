package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.repository.AgentSkillRepository;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 让 Agent 具备把技能写回技能仓库的能力。
 *
 * <p>framework 只内置了只读的 load_skill_through_path，没有任何工具能调用
 * AgentSkillRepository#save，所以这里包一层暴露给 LLM。
 *
 * <p>save_skill 是「一次性整技能写入」：必须一次性给出该技能的全部资源文件，
 * 已存在的同名技能不会被覆盖，而是报错。
 */
public final class SkillWriterTool {

    /**
     * 框架约定的二进制资源前缀，见 SkillFileSystemHelper / SkillToolFactory。
     */
    private static final String BASE64_PREFIX = "base64:";

    private final AgentSkillRepository repository;

    public SkillWriterTool(AgentSkillRepository repository) {
        this.repository = repository;
    }

    /**
     * 一份随技能保存的资源文件。
     *
     * @param path    技能内的相对路径，可含多级目录，如 scripts/analyze.py
     * @param content 文本内容；binary=true 时传标准 Base64（可带或不带 base64: 前缀）
     * @param binary  是否为二进制资源，true 时按 Base64 解码校验并加上 base64: 前缀后存储
     */
    public record ResourceItem(String path, String content, Boolean binary) {
    }

    @Tool(name = "list_saved_skills", description = "列出技能仓库中已保存的所有技能 ID。")
    public String listSavedSkills() {
        var names = repository.getAllSkillNames();
        if (names == null || names.isEmpty()) {
            return "技能仓库为空。";
        }

        return String.join(System.lineSeparator(), names);
    }

    @Tool(
            name = "save_skill",
            description =
                    "新建并保存一个技能到技能仓库，下一轮对话起该技能会出现在可用技能列表中。"
                            + "必须一次性给全：一次调用要同时提供正文和该技能的全部资源文件，"
                            + "不支持之后再追加资源。"
                            + "已存在同名技能时不会覆盖，会直接报错。"
                            + "入参说明：name/description/content 都是字符串，content 写操作步骤正文；"
                            + "resources 必须是「对象数组」（即使只有一份文件也要写成只含一个元素的数组，"
                            + "不要传单个对象，也不要用对象 map 代替），数组中每个对象形如 "
                            + "{\"path\": \"scripts/analyze.py\", \"content\": \"文件内容\", \"binary\": false}，"
                            + "其中 path 是技能内的相对路径、可含多级目录（scripts/xxx.py、assets/xxx.json），"
                            + "content 是该文件完整内容，binary 为 true 时 content 必须填 Base64 编码；"
                            + "若技能不附带任何资源文件，请省略 resources。"
    )
    public String saveSkill(
            @ToolParam(name = "name", description = "技能名，小写字母、数字、下划线或连字符，不含斜杠")
            String name,
            @ToolParam(name = "description", description = "技能描述，说明该技能解决什么问题、何时使用")
            String description,
            @ToolParam(name = "content", description = "技能正文，即 SKILL.md 的操作步骤说明")
            String content,
            @ToolParam(
                    name = "resources",
                    required = false,
                    description = "可选，必须是「对象数组」，每个元素描述一份资源文件："
                            + "{\"path\": \"scripts/analyze.py\", \"content\": \"文件内容\", \"binary\": false}。"
                            + "即使只有一份文件也要传数组，不要传单个对象或 map。"
                            + "本技能的「全部」资源都要在这次调用里给全，后续无法追加。"
                            + "path 是技能内的相对路径、支持多级目录；content 是文件完整内容；"
                            + "binary 为 true 时 content 必须填该二进制文件的 Base64 编码。"
                            + "无任何资源时省略本参数。")
            List<ResourceItem> resources
    ) {
        if (!repository.isWriteable()) {
            return "写入失败：技能仓库当前为只读。";
        }

        if (repository.skillExists(name)) {
            return "写入失败：技能 '" + name + "' 已存在，本工具不会覆盖已有技能。"
                    + "请先调用 delete_skill 删除旧技能，然后把全部资源文件一次性重新提交。";
        }

        Map<String, String> skillResources = new LinkedHashMap<>();
        if (resources != null && !resources.isEmpty()) {
            for (var item : resources) {
                if (item == null || item.path() == null || item.path().isBlank()) {
                    return "写入失败：资源的 path 不能为空。";
                }
                if (item.content() == null) {
                    return "写入失败：资源 '" + item.path() + "' 的 content 不能为空。";
                }
                if (Boolean.TRUE.equals(item.binary())) {
                    var raw = item.content().startsWith(BASE64_PREFIX)
                            ? item.content().substring(BASE64_PREFIX.length())
                            : item.content();
                    try {
                        Base64.getDecoder().decode(raw);
                    } catch (IllegalArgumentException e) {
                        return "写入失败：资源 '" + item.path() + "' 标记了 binary=true，但 content 不是合法的 Base64：" + e.getMessage();
                    }
                    skillResources.put(item.path(), BASE64_PREFIX + raw);
                } else {
                    skillResources.put(item.path(), item.content());
                }
            }
        }

        var builder = AgentSkill.builder()
                .name(name)
                .description(description)
                .skillContent(content)
                .source("agent-generated");
        if (!skillResources.isEmpty()) {
            builder.resources(skillResources);
        }

        try {
            // force=false：由上面的 skillExists 保证不会误覆盖，这里再兜一道。
            var ok = repository.save(List.of(builder.build()), false);
            if (!ok) {
                return "写入失败：技能仓库返回 false，请检查仓库配置。";
            }
            var msg = "技能 '" + name + "' 已保存到技能仓库，下一轮对话生效。";
            if (!skillResources.isEmpty()) {
                msg += " 附带资源 " + skillResources.size() + " 份：" + String.join("、", skillResources.keySet()) + "。";
            }
            return msg;
        } catch (IllegalStateException e) {
            return "写入失败：" + e.getMessage();
        } catch (RuntimeException e) {
            return "写入失败：" + e;
        }
    }

    @Tool(name = "delete_skill", description = "从技能仓库中删除指定名称的技能及其全部资源文件。")
    public String deleteSkill(
            @ToolParam(name = "name", description = "要删除的技能名")
            String name
    ) {
        if (!repository.isWriteable()) {
            return "删除失败：技能仓库当前为只读。";
        }

        var ok = repository.delete(name);
        return ok ? "技能 '" + name + "' 已删除。" : "删除失败：技能 '" + name + "' 不存在。";
    }
}
