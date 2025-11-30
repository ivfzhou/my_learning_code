package vn.ivfzhou.java.javaee;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public final class TestPlaceholder {

    @Test
    public void shouldAnswerWithTrue() throws IOException {
        try (var stream = getClass().getResourceAsStream("/application.properties")) {
            if (stream == null) {
                System.out.println("stream is null");
                return;
            }
            var bs = new byte[64];
            for (var len = stream.read(bs); len != -1; len = stream.read(bs)) {
                System.out.print(new String(bs, 0, len));
            }
        }
    }

}
