<%@ page import="test.springmvc.web.ItemController"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script src="${pageContext.request.contextPath}/resources-mapping/scripts/utils.js"></script>
<h2>Accounting item:</h2>
Com: ${com}<br>
<form id="detailsForm">
  <input name="<%=ItemController.ACCOUNTING_ITEM_ID_PARAM%>" type="hidden" value="${accountingItemId}">
  <c:forEach items="${fields}" var="field">
    ${field.name}&nbsp;<input name="${field.name}" type="text" title="${field.title}" value="${field.value}" /><br>
  </c:forEach>
  <input id="submit_button" type="submit" size="10" value="Submit">
  <input id="cancelButton" type="button" value="Cancel">
</form>
