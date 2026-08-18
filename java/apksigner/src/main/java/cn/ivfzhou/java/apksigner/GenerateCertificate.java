package cn.ivfzhou.java.apksigner;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;

public class GenerateCertificate {

    static void main(String[] args) throws Exception {
        // 注册 BouncyCastle 安全提供者。
        Security.addProvider(new BouncyCastleProvider());

        // 生成 RSA 密钥对。
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();

        // 证书主体与颁发者（自签名，二者相同）。
        var x500Name = new X500Name("CN=ivfzhou cert, OU=, O=, L=Changsha, ST=Hunan, C=CN");
        var now = new Date();
        var notAfter = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 365);

        // 构建证书。
        var certBuilder = new JcaX509v3CertificateBuilder(
                x500Name,
                new BigInteger(64, new SecureRandom()),
                now,
                notAfter,
                x500Name,
                keyPair.getPublic()
        );
        // 扩展：非 CA 证书。
        certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
        // 扩展：密钥用途——数字签名。
        certBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature));
        // 扩展：扩展密钥用途——代码签名（id-kp-codeSigning）。
        certBuilder.addExtension(Extension.extendedKeyUsage, false,
                new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning));

        // 签名。
        var signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        var cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer));
        cert.verify(keyPair.getPublic());
        System.out.println(cert);

        // 写入 PKCS#12 密钥库。
        var keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("alias", keyPair.getPrivate(), "654321".toCharArray(), new Certificate[]{cert});
        try (var out = new FileOutputStream(args[0])) {
            keyStore.store(out, "123456".toCharArray());
        }
    }

}
