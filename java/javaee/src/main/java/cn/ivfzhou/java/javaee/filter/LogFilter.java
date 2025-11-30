package cn.ivfzhou.java.javaee.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

// @WebFilter(servletNames = "*", urlPatterns = "/")
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var req = wrapperReq((HttpServletRequest) request);
        var rsp = wrapperRsp((HttpServletResponse) response);
        var now = System.currentTimeMillis();
        chain.doFilter(req, rsp);
        var cost = Duration.ofMillis(System.currentTimeMillis() - now);

        if (isEnableLog(request.getContentType())) {
            var reqBody = ((ServletInputStreamDecorator) req.getInputStream()).getReqBody();
            System.out.printf("HTTP REQUEST BODY: %s\n", new String(reqBody, StandardCharsets.UTF_8));
        }
        if (isEnableLog(rsp.getContentType())) {
            var rspBody = ((ServletOutputStreamDecorator) rsp.getOutputStream()).getRspBody();
            System.out.printf("HTTP RESPONSE BODY: %s\n", new String(rspBody, StandardCharsets.UTF_8));
        }
        System.out.printf("HTTP COST: %s\n", cost.toString());
    }

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.printf("LogFilter init %s\n", filterConfig);
    }

    @Override
    public void destroy() {
        System.out.println("LogFilter destroyed");
    }

    private HttpServletResponseWrapper wrapperRsp(HttpServletResponse rsp) throws IOException {
        final var rspDecorator = new ServletOutputStreamDecorator(rsp.getOutputStream());

        return new HttpServletResponseWrapper(rsp) {
            final PrintWriter pw = new PrintWriter(rspDecorator);

            public ServletOutputStream getOutputStream() {
                return rspDecorator;
            }

            public PrintWriter getWriter() {
                return pw;
            }
        };
    }

    private HttpServletRequestWrapper wrapperReq(HttpServletRequest req) throws IOException {
        final var reqDecorator = new ServletInputStreamDecorator(req.getInputStream());

        return new HttpServletRequestWrapper(req) {
            public ServletInputStream getInputStream() {
                return reqDecorator;
            }
        };
    }

    private boolean isEnableLog(String ct) {
        if (ct == null || ct.isBlank()) return false;
        ct = ct.toLowerCase();
        return ct.contains("application/json") ||
                ct.contains("application/x-www-form-urlencoded") ||
                ct.contains("text/plain");
    }

    private static class ServletOutputStreamDecorator extends ServletOutputStream {
        final ServletOutputStream stream;

        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        ServletOutputStreamDecorator(ServletOutputStream stream) {this.stream = stream;}

        byte[] getRspBody() {return bs.toByteArray();}

        @Override
        public boolean isReady() {return stream.isReady();}

        @Override
        public void setWriteListener(WriteListener writeListener) {stream.setWriteListener(writeListener);}

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
            bs.write(b);
        }
    }

    private static class ServletInputStreamDecorator extends ServletInputStream {
        final ServletInputStream stream;

        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        ServletInputStreamDecorator(ServletInputStream stream) {this.stream = stream;}

        byte[] getReqBody() {return bs.toByteArray();}

        @Override
        public boolean isFinished() {return stream.isFinished();}

        @Override
        public boolean isReady() {return stream.isReady();}

        @Override
        public void setReadListener(ReadListener readListener) {stream.setReadListener(readListener);}

        @Override
        public int read() throws IOException {
            var read = stream.read();
            bs.write(read);
            return read;
        }
    }

}
