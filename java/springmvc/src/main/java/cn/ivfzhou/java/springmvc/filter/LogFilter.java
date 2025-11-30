package cn.ivfzhou.java.springmvc.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@WebFilter(value = "/", servletNames = "*")
public class LogFilter implements Filter {

    private int count;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        var rsp = new ContentCachingResponseWrapper((HttpServletResponse) response);
        req.setCharacterEncoding("UTF-8");
        rsp.setCharacterEncoding("UTF-8");
        chain.doFilter(req, rsp);
        count++;
        System.out.println(count + ". 请求体 " + new String(req.getContentAsByteArray()));
        System.out.println(count + ". 请求结果 " + new String(rsp.getContentAsByteArray()));
        rsp.copyBodyToResponse();
    }

}
