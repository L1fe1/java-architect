package com.l1fe1.demoagent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader cl, String className, Class<?> c, ProtectionDomain pd, byte[] bytes) {
        if (!"DemoClass".equals(c.getSimpleName())) {
            // 只修改 DemoClass 的定义
            return null;
        }
        // 读取 DemoClass.class.2.2 这个 class 文件，作为 DemoClass 类的新定义
        return getBytesFromFile("E:\\IdeaProjects\\java-architect\\skywalking\\demo-agent\\src\\main\\java\\com\\l1fe1\\demoagent\\DemoClass.class.2");
    }

    public static byte[] getBytesFromFile(String fileName) {
        try {
            // precondition
            File file = new File(fileName);
            InputStream is = new FileInputStream(file);
            long length = file.length();
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            is.close();
            return bytes;
        } catch (Exception e) {
            System.out.println("error occurs in _ClassTransformer!"
                    + e.getClass().getName());
            return null;
        }
    }
}
