<%@ taglib tagdir="/WEB-INF/tags/layout" prefix="layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<layout:page title="MyPortal - Authorize Application">
	<jsp:body>
		<h2>Confirm Access</h2>
		<p>
			Do you authorize the application &quot;<c:out value="${consumer.consumerName}" />&quot; to access the following resources:
		</p>
		
		<ul>
			<li><c:out value="${consumer.resourceName}" /> &mdash; <c:out value="${consumer.resourceDescription}" /></li>
		</ul>
		
		<form action="<c:url value="/oauth/authorize"/>" method="post">
			<input name="requestToken" value="<c:out value="${oauth_token}"/>" type="hidden" />
			<c:if test="${!empty oauth_callback}">
				<input name="callbackURL" value="<c:out value="${oauth_callback}"/>" type="hidden"/>
			</c:if>
			<p>
				<input class="button blue" name="authorize" value="Authorize" type="submit" />
				 &nbsp;
				<a href="<c:url value="/oauth/revoke/${oauth_token}"/>" class="button red">Revoke</a>
			</p>
		</form>
	</jsp:body>
</layout:page>