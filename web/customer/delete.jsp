<%--
  Created by IntelliJ IDEA.
  User: nguyenkhanhtung
  Date: 11/26/18
  Time: 17:16
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Delete a customer</title>
    <style>
        .message {
            color: green;
        }
    </style>
</head>
<body>
<h1>Delete a customer</h1>
<p>
    <c:if test='${requestScope["message"]!= null}'>
        <span class="message">${requestScope["message"]}</span>
    </c:if>
</p>
<p>
    <a href="/customers">Back to list customer</a>
</p>
<form method="post">
    <fieldset>
        <legend>Delete a customer</legend>
        <table>
            <tr>
                <td>Name:</td>
                <td><input type="text" name="name" id="name" value="${requestScope["customer"].getName()}"></td>
            </tr>
            <tr>
                <td>Email:</td>
                <td><input type="text" name="email" id="email" value="${requestScope["customer"].getEmail()}"></td>
            </tr>
            <tr>
                <td>Address:</td>
                <td><input type="text" name="address" id="address" value="${requestScope["customer"].getAddress()}"></td>
            </tr>
            <tr>
                <td></td>
                <td><button type="submit">Delete Customer</button></td>
            </tr>
        </table>
    </fieldset>
</form>
</body>
</html>
