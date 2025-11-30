<%@ page contentType="text/html; UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro" %>
<html>
<head>
    <title>Shiro</title>
    <meta charset="UTF-8"/>
</head>
<body>
index
<shiro:guest>
    guest游客可见
</shiro:guest>
<shiro:user>
    user用户可见[<shiro:principal/>]
</shiro:user>
<shiro:authenticated>
    已授权可见[<shiro:principal/>]
</shiro:authenticated>
<shiro:notAuthenticated>
    未验证身份，记住我也不行
</shiro:notAuthenticated>
<shiro:hasRole name="admin">
    有角色就显示[<shiro:principal/>]
</shiro:hasRole>
<shiro:hasAnyRoles name="admin,user">
    有任意角色就显示[<shiro:principal/>]
</shiro:hasAnyRoles>
<shiro:lacksRole name="admin">
    没有这个角色就显示[<shiro:principal/>]
</shiro:lacksRole>
<shiro:hasPermission name="admin:query">
    有权限就显示[<shiro:principal/>]
</shiro:hasPermission>
<shiro:lacksPermission name="admin:query">
    没有权限就显示[<shiro:principal/>]
</shiro:lacksPermission>

</body>
</html>
