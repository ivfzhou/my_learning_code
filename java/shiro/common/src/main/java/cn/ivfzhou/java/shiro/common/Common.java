package cn.ivfzhou.java.shiro.common;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.env.BasicIniEnvironment;

public final class Common {

    public static void main(String[] args) {
        var environment = new BasicIniEnvironment("classpath:shiro.ini");
        var securityManager = environment.getSecurityManager();
        SecurityUtils.setSecurityManager(securityManager);
        var subject = SecurityUtils.getSubject();
        var session = subject.getSession();
        session.setAttribute("passwd", "123456");

        if ("123456".equals(session.getAttribute("passwd"))) {
            System.out.println("OK");
        }

        if (!subject.isAuthenticated()) {
            var ivfzhou = new UsernamePasswordToken("ivfzhou", "123456");
            ivfzhou.setRememberMe(true);
            try {
                subject.login(ivfzhou);
                System.out.println("login");
            } catch (UnknownAccountException e) {
                System.out.println("UnknownAccountException" + e);
            } catch (IncorrectCredentialsException e) {
                System.out.println("IncorrectCredentialsException" + e);
            } catch (LockedAccountException e) {
                System.out.println("LockedAccountException" + e);
            }
        }
        System.out.println(subject.getPrincipal());

        System.out.println("hasRole admin " + subject.hasRole("admin"));
        System.out.println("isPermitted " + subject.isPermitted("query:data:all"));

        subject.logout();
    }

}
