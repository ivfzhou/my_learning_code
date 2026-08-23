package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteFileTool {

    @Tool(name = "write_file")
    public void writeFile(
            @ToolParam(name = "fileName", description = "文件路径")
            String fileName,
            @ToolParam(name = "content", description = "文件内容")
            String content
    ) throws IOException {
        var file = new File(fileName);
        if (!file.exists()) {
            try (var w = new FileOutputStream(file)) {
                w.write(content.getBytes());
            }
        } else {
            System.err.println("file exists, skip writing");
        }
    }


}
