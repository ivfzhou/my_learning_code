package cn.ivfzhou.java.jarsigner;

import org.apache.maven.shared.jarsigner.JarSignerUtil;

import java.io.File;
import java.io.IOException;

public class Delete {

    static void main(String[] args) throws IOException {
        var inputFile = new File(args[0]);
        if (!JarSignerUtil.isArchiveSigned(inputFile)) {
            System.out.println("file is not a signed archive");
            return;
        }
        JarSignerUtil.unsignArchive(inputFile);
        System.out.println("unsign archive successfully");
    }

}
