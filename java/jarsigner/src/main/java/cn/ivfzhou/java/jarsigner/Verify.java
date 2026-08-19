package cn.ivfzhou.java.jarsigner;

import org.apache.maven.shared.jarsigner.DefaultJarSigner;
import org.apache.maven.shared.jarsigner.JarSignerVerifyRequest;
import org.apache.maven.shared.utils.cli.javatool.JavaToolException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Verify {

    static void main(String[] args) throws JavaToolException {
        // 统一按 UTF-8 输出，避免中文 Windows（GBK）下控制台乱码。
        // System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        // System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));

        var outputFile = new File(args[0]);

        // 校验签名（-verify -certs）。
        var verifyRequest = new JarSignerVerifyRequest();
        verifyRequest.setArchive(outputFile);
        verifyRequest.setVerbose(true);
        verifyRequest.setCerts(true);
        // 与签名请求一致，强制子进程按 UTF-8 输出。
        verifyRequest.setArguments("-J-Dfile.encoding=UTF-8", "-J-Dstdout.encoding=UTF-8", "-J-Dstderr.encoding=UTF-8");
        verifyRequest.setSystemOutStreamConsumer(System.out::println);
        verifyRequest.setSystemErrorStreamConsumer(System.err::println);
        var verifyResult = new DefaultJarSigner().execute(verifyRequest);
        System.out.println("校验结果："
                + (verifyResult.getExecutionException() == null && verifyResult.getExitCode() == 0 ? "通过" : "失败"));
    }

}
