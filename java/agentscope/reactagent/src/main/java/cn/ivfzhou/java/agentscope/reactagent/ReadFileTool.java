package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ReadFileTool {

    @Tool(name = "read_file", description = "读取文件内容")
    public byte[] readFile(
            @ToolParam(name = "fileName", description = "文件路径")
            String fileName
    ) throws IOException {
        var file = new File(fileName);
        if (!file.exists()) throw new RuntimeException("file not exist");
        if (file.isDirectory()) throw new RuntimeException("file is a directory");

        var stream = new FileInputStream(file);
        try (stream) {
            return stream.readAllBytes();
        }
    }

}
