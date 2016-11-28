<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Accounting items</title>
    <script src="${pageContext.request.contextPath}/resources-mapping/scripts/jquery-1.10.2.min.js" defer></script>
    <script src="${pageContext.request.contextPath}/resources-mapping/scripts/utils.js" defer></script>
    <link rel="icon" href="${pageContext.request.contextPath}/resources-mapping/css/ptimtocodephoto2.gif">
    <link rel="stylesheet" type="text/css" href='${pageContext.request.contextPath}/resources-mapping/css/styles.css'>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources-mapping/bootstrap-3.3.1-dist/dist/css/bootstrap.css">
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top"></nav>
<div class="container">
    <div class="starter-template">
        <h2>Accounting items (click to show details):</h2>
    </div>
</div>
<input id="addButton" type="button" value="Add new" onclick="showAddNew()">
<div id="accounting-items">
    <c:forEach var="accountingItem" items="${accountingItems}">
        <div id="${accountingItem.id}" onclick="showDetails(this.id)">${accountingItem.name}</div>
    </c:forEach>
</div>
<div id="result-details"></div>
<div id="pay-form"></div>
<span id="csrf_key" hidden="true">${CSRF_key}</span>
</body>
</html>
