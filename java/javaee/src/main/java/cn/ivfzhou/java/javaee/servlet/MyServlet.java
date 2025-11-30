package cn.ivfzhou.java.javaee.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @WebServlet("/myServlet")
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws IOException {
        rsp.getWriter().println("hello you do get");
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

}
