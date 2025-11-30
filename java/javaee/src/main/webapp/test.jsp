<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ include file="include.txt" %> <%-- 在 jsp 文件加载前引入内容 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<html>
<head>
    <title>JavaEE</title>
    <meta charset="UTF-8"/>
</head>
<body>
HELLO JSP
<div>
    <%-- 动作标签 --%>
    <jsp:include page="include.txt"/>
    <jsp:useBean id="user" class="cn.ivfzhou.java.javaee.bean.User"/>
    <jsp:setProperty name="user" property="name" value="ivfzhou"/>
    <jsp:getProperty name="user" property="name"/>
    <%--
    <jsp:forward page="index.jsp">
        <jsp:param name="name" value="value"/>
    </jsp:forward>
    --%>

    <br/>
    <%-- EL 表达式 --%>
    打印 user.name：${empty pageScope.user.name}
    <br/>
    打印 param：${param}
    <br/>

    <%-- 输出脚本 --%>
    打印日期：<%= new Date() %>
    <br/>

    <%-- 语句脚本 --%>
    打印变量：
    <%
        out.println("pageContext：" + pageContext);
        out.println("<br/>");
        out.println("session：" + session);
        out.println("<br/>");
        out.println("application：" + application);
        out.println("<br/>");
        out.println("config：" + config);
        out.println("<br/>");
        out.println("out：" + out);
        out.println("<br/>");
        out.println("page：" + page);
        out.println("<br/>");
    %>

    <%-- 全局代码块 --%>
    <%!
        private final static int num = 9;

        private final static List<Integer> list = List.of(1, 2, 3);

        public void run() {
            System.out.println(num);
        }
    %>
    <%
        // 把成员变量放到 request 作用域
        pageContext.setAttribute("list", list);
    %>

    打印 num：<%= num %>
    <br/>

    <%-- jstl --%>
    <c:if test="${1>=1}">show if content</c:if>
    <c:choose>
        <c:when test="${true}">show when content</c:when>
        <c:otherwise>when else content</c:otherwise>
    </c:choose>
    <br/>
    <c:forEach var="i" items="${list}" begin="0" end="${list.size()}" step="1" varStatus="status">
        ${i} ${status}
    </c:forEach>
    <br/>
    <c:url value="https://ivfzhou.cn"/>
</div>
</body>
</html>
