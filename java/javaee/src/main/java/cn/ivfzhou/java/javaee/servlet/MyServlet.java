package cn.ivfzhou.java.javaee.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// @WebServlet("/myServlet")
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws IOException {
        rsp.getWriter().println("hello you do get");
        printApplication();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws IOException {
        try (var inputStream = req.getInputStream()) {
            var sb = new StringBuffer();
            var bs = new byte[8 * 1024];
            for (var len = inputStream.read(bs); len != -1; len = inputStream.read(bs)) {
                sb.append(new String(bs, 0, len, StandardCharsets.UTF_8));
            }
            System.out.println(sb);
        }

        rsp.addHeader("Content-Type", "text/plain");
        rsp.getOutputStream().println("hello you do post");
    }

    @Override
    public void destroy() {
        System.out.println("MyServlet destroyed");
    }

    @Override
    public void init() {
        System.out.println("MyServlet init " + this.getInitParameter("servletName"));
    }

    private void printApplication() throws IOException {
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
