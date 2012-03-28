<%@ taglib tagdir="/WEB-INF/tags/layout" prefix="layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<layout:page title="MyPortal - Usuários">
	<jsp:body>
		<h2>Formulário de Usuários</h2>
		<form:form action="" method="post" commandName="userBean" class="form-login">
			<form:hidden path="id" />
			<p>
				<form:label path="realname">Nome Real:</form:label>
				<form:input path="realname" required="required" />
			</p>
			<p>
				<form:label path="username">Nome de Usuário:</form:label>
				<form:input path="username" required="required" />
				<form:errors path="username" cssClass="error" />
			</p>
			<p>
				<form:label path="password">Senha:</form:label>
				<c:if test="${empty userBean.id}">
					<form:password path="password" required="required" />
				</c:if>
				<c:if test="${!empty userBean.id}">
					<form:password path="password" />
				</c:if>
				
				<form:errors path="password" cssClass="error" />
			</p>
			<p>
				<form:label path="birthDate">Data de Nascimento:</form:label>
				<form:input path="birthDate" type="date" required="required" />
				<form:errors path="birthDate" cssClass="error" />
			</p>
			<p>
				<form:label path="role">Grupo:</form:label>
				<form:select path="role">
					<form:option value="ROLE_ADMIN">Administrador</form:option>
					<form:option value="ROLE_USER">Usu&aacute;rio</form:option>
				</form:select>
			</p>
			<p>
				<label><form:checkbox path="enabled" value="true" /> Habilitado</label>
			</p>
			<p>
				<input class="button blue" type="submit" value="Salvar" />
			</p>
		</form:form>
	</jsp:body>
</layout:page>