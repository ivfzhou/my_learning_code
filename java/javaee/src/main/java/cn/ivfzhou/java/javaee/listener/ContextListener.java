package cn.ivfzhou.java.javaee.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

// @WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ContextListener init " + sce.getServletContext().getInitParameter("contextName"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ContextListener destroyed");
    }

}
