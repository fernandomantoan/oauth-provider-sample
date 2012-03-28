<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ tag body-content="empty" description="Header" %>
<header>
	<h1>MyPortal</h1>
	<p class="loggedAs">
		Logged as <b><sec:authentication property="principal.realname" /></b>.
		<a href="<c:url value="/user/profile"/>">Edit Profile</a>
		 &nbsp;
		<a href="<c:url value="/logout.do"/>">Logout</a>
	</p>
	<ul id="menu">
		<li><a href="<c:url value="/dashboard"/>">Dashboard</a></li>
		<%-- <li><a href="<c:url value="/application"/>">Applications</a></li> --%>
		<sec:authorize access="hasRole('ROLE_ADMIN')" >
		<li><a href="<c:url value="/user"/>">Users</a></li>
		</sec:authorize>
	</ul>
</header>