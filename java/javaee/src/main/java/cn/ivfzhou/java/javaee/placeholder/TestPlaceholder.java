package cn.ivfzhou.java.javaee.placeholder;

import java.io.IOException;

public final class TestPlaceholder {

    public static void main(String[] args) throws IOException {
        try (var stream = TestPlaceholder.class.getResourceAsStream("/application.properties")) {
            if (stream == null) {
                System.out.println("stream is null");
                return;
            }

            var bs = new byte[64];
            for (var len = 0; len != -1; len = stream.read(bs)) {
                System.out.print(new String(bs, 0, len));
            }
        }
    }

}
