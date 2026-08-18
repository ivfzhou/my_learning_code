package cn.ivfzhou.java.apksigner;

import com.android.apksig.ApkSigner;
import com.android.apksig.KeyConfig;
import com.android.apksig.apk.ApkFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class Sign {

    /**
     *
     * <h3>签名 scheme 与来源戳的兼容情况</h3>
     * <ul>
     *     <li><b>v1</b>（JAR 签名，API 1）：基于 META-INF 下的 {@code *.SF}/{@code *.RSA} 条目。
     *     可与 v2/v3 共存，但一旦启用来源戳时间戳（source stamp timestamp），apksig 会跳过 v1，
     *     因为 v1 的哈希模型无法承载来源戳数据。</li>
     *     <li><b>v2</b>（API 24）：APK Signing Block（ID 0x7109871a），覆盖几乎全部字节，防篡改更强。</li>
     *     <li><b>v3</b>（API 28）：v2 的超集，新增密钥轮换（rotation）与来源戳（source stamp）能力。
     *     开启 v3 通常同时生成 v2 以兼容老设备。</li>
     *     <li><b>v3.1 / v31</b>（API 33）：v3 带 rotation 历史链（多张不同密钥对签出的证书）时自动产生。
     *     必须提供历史证书，且 {@code minSdkVersionForRotation} 足够高（如 28）。</li>
     *     <li><b>v4</b>（API 30）：写入独立 {@code .idsig} 文件，用于增量安装，依赖 APK 内已有 v2/v3。
     *     apksig 9.x 的 {@code V4SchemeSigner} 仅支持单证书，因此 <b>v31（rotation 多证书）与 v4 互斥</b>，
     *     同时开启会抛 {@code CertificateEncodingException: Should only have one certificate}。</li>
     *     <li><b>来源戳（source stamp）</b>：v3 的特性，通过 v3 block 的 attributes 嵌入一张额外证书。
     *     依赖 v3，会跳过 v1，可与 v2 共存；与 v31 不建议同时使用；单证书（无 rotation）时可与 v4 共存。</li>
     * </ul>
     */
    static void main(String[] args) throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableKeyException, ApkFormatException, SignatureException,
            InvalidKeyException {

        // 加载证书。
        var keyStore = KeyStore.getInstance("PKCS12");
        try (var in = new FileInputStream(args[0])) {
            keyStore.load(in, "123456".toCharArray());
        }
        var privateKey = (PrivateKey) keyStore.getKey("alias", "654321".toCharArray());
        var currentCert = (X509Certificate) keyStore.getCertificate("alias");

        // 加载证书。
        // var keyStore2 = KeyStore.getInstance("PKCS12");
        // try (var in = new FileInputStream(args[1])) {
        //     keyStore2.load(in, "123456".toCharArray());
        // }
        // var privateKey2 = (PrivateKey) keyStore.getKey("alias", "654321".toCharArray());
        // var oldCert = (X509Certificate) keyStore.getCertificate("alias");

        // 执行签名。
        // var inputFile = new File(args[2]);
        var inputFile = new File(args[1]);
        var fileName = inputFile.getName();
        var index = fileName.lastIndexOf(".");
        var resultFileName = "";
        if (index != -1) {
            resultFileName = fileName.substring(0, index);
            resultFileName = resultFileName + "_signed" + fileName.substring(index);
        } else {
            resultFileName = fileName + "_signed";
        }
        var signConfig = new ApkSigner.SignerConfig
                .Builder("sign-config-name", new KeyConfig.Jca(privateKey), List.of(currentCert/*, oldCert*/)).build();
        var signer = new ApkSigner.Builder(List.of(signConfig))
                .setInputApk(inputFile)
                .setOutputApk(new File(inputFile.getParentFile(), resultFileName))
                .setMinSdkVersion(1)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .setV4SigningEnabled(true)
                // .setVerityEnabled(true)
                // .setSourceStampTimestampEnabled(true)
                // .setSourceStampSignerConfig(signConfig)
                // .setForceSourceStampOverwrite(true)
                // .setRotationTargetsDevRelease(true)
                // .setMinSdkVersionForRotation(28)
                .setAlignFileSize(true)
                .setLibraryPageAlignmentBytes(4096)
                .setV4ErrorReportingEnabled(true)
                .setCreatedBy("ivfzhou")
                .setDebuggableApkPermitted(true)
                .setV4SignatureOutputFile(new File(inputFile.getParentFile(), "v4_signature_output"))
                .build();
        signer.sign();
    }

}
