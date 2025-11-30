package cn.ivfzhou.java.javaee.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

// @WebFilter(servletNames = "*", urlPatterns = "/")
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("MyFilter init " + filterConfig.getInitParameter("filterName"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("filter begin");
        chain.doFilter(request, response);
        System.out.println("filter end");
    }

    @Override
    public void destroy() {
        System.out.println("MyFilter destroyed");
    }

}
