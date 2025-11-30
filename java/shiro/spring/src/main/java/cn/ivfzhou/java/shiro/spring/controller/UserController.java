package cn.ivfzhou.java.shiro.spring.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @RequiresRoles("admin")  // 拥有 admin 角色才能访问该方法。
    @RequiresPermissions("user:update") // 拥有 user:update 权限才能方法该方法。
    @RequestMapping("/updateUser")
    @ResponseBody
    public String updateUser() {
        System.out.println("updateUser");
        return "yes";
    }

    @RequestMapping("/login")
    @RequiresGuest // 游客放行。
    @ResponseBody
    public String login(String username, String password) {
        System.out.println("login");
        var subject = SecurityUtils.getSubject();
        var token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        subject.login(token);
        return "yes";
    }

    @RequestMapping("/info")
    @RequiresUser // 登录的放行。
    public ModelAndView info() {
        System.out.println("info");
        return new ModelAndView("index.jsp");
    }

}
