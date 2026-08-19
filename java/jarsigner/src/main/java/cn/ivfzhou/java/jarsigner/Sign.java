package cn.ivfzhou.java.jarsigner;

import org.apache.maven.shared.jarsigner.DefaultJarSigner;
import org.apache.maven.shared.jarsigner.JarSignerSignRequest;
import org.apache.maven.shared.jarsigner.JarSignerUtil;
import org.apache.maven.shared.utils.cli.javatool.JavaToolException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Sign {

    static void main(String[] args) throws JavaToolException, IOException {
        // 统一按 UTF-8 输出，避免中文 Windows（GBK）下控制台乱码。
        // System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        // System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));

        var keystore = new File(args[0]);
        var inputFile = new File(args[1]);

        // 输出文件名：xxx_signed.jar。
        var fileName = inputFile.getName();
        var index = fileName.lastIndexOf(".");
        var resultFileName = index != -1
                ? fileName.substring(0, index) + "_signed" + fileName.substring(index)
                : fileName + "_signed";
        var outputFile = new File(inputFile.getParentFile(), resultFileName);

        // 组装签名请求。
        var request = new JarSignerSignRequest();
        // 待签名的 jar。
        request.setArchive(inputFile);
        // 签名结果输出文件（不设置则原地覆盖签名）。
        request.setSignedjar(outputFile);
        // 密钥库路径、库口令、密钥口令与别名（与 apksigner 模块 GenerateCertificate 生成的密钥库一致）。
        request.setKeystore(keystore.getAbsolutePath());
        request.setStorepass("123456");
        // PKCS12 密钥库不支持库口令与密钥口令不一致，-keypass 必须与 -storepass 相同（不设置则默认取库口令）。
        request.setKeypass("654321");
        request.setAlias("alias");
        // 签名条目 *.SF/*.RSA 的文件名前缀，默认取 alias 的前 8 个字符。
        // request.setSigfile("IVFZHOU");
        // 追加额外的 jarsigner 参数，如指定摘要/签名算法。
        // request.setArguments("-digestalg", "SHA-256", "-sigalg", "SHA256withRSA");
        // 时间戳服务，让签名在证书过期后仍可被验证。
        // request.setTsaLocation("http://timestamp.digicert.com");
        // 强制 jarsigner 子进程同样按 UTF-8 输出，与上面的解码保持一致。
        request.setArguments("-J-Dfile.encoding=UTF-8", "-J-Dstdout.encoding=UTF-8", "-J-Dstderr.encoding=UTF-8");
        request.setVerbose(true);
        // 把 jarsigner 命令的输出转发到控制台。
        request.setSystemOutStreamConsumer(System.out::println);
        request.setSystemErrorStreamConsumer(System.err::println);

        // 执行签名。
        var result = new DefaultJarSigner().execute(request);
        if (result.getExecutionException() != null || result.getExitCode() != 0) {
            throw new IllegalStateException("jar 签名失败。", result.getExecutionException());
        }
        System.out.println("签名完成：" + outputFile.getAbsolutePath());
        System.out.println("是否包含签名条目：" + JarSignerUtil.isArchiveSigned(outputFile));
    }

}
