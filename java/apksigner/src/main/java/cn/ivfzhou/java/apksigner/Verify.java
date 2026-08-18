package cn.ivfzhou.java.apksigner;

import com.android.apksig.ApkVerifier;
import com.android.apksig.apk.ApkFormatException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Verify {

    static void main(String[] args) throws ApkFormatException, IOException, NoSuchAlgorithmException {
        var verifier = new ApkVerifier.Builder(new File(args[0]))
                .setV4SignatureFile(new File(args[1])).build();

        var sourceStampRet = verifier.verifySourceStamp();
        System.out.println("source stamp " + sourceStampRet.isVerified());

        var ret = verifier.verify();
        System.out.println("verify " + ret.isVerified());
        System.out.println("v1 " + ret.isVerifiedUsingV1Scheme());
        System.out.println("v2 " + ret.isVerifiedUsingV2Scheme());
        System.out.println("v3 " + ret.isVerifiedUsingV3Scheme());
        System.out.println("v3.1 " + ret.isVerifiedUsingV31Scheme());
        System.out.println("v4 " + ret.isVerifiedUsingV4Scheme());
        System.out.println(ret.getSignerCertificates().getLast().getSubjectX500Principal().getName());
    }

}
