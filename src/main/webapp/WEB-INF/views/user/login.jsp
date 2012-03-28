<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>MyPortal - Login</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/styles.css"/>" />
	</head>
	<body>
		<div class="login-wrapper">
			<header>
				<h1>MyPortal - Login</h1>
				<c:if test="${not empty param.login_error}">
					<div class="error">
						Invalid username or password.
					</div>
				</c:if>
			</header>
			<section id="content">
				<form method="post" action="<c:url value="/login.do"/>" class="form-login">
					<p>
						<label for="username">Username:</label>
						<input type="text" id="username" name="j_username" required="required" />
					</p>
					<p>
						<label for="password">Password:</label>
						<input type="password" id="password" name="j_password" required="required" />
					</p>
					<p>
						<input class="button blue" type="submit" value="Authenticate" />
					</p>
				</form>
			</section>
		</div>
	</body>
</html>