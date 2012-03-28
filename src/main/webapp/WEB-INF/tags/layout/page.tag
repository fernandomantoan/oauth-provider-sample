<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag description="Page Skeleton" %>
<%@ attribute name="title" required="true" description="Page Title" %>
<%@ taglib tagdir="/WEB-INF/tags/layout" prefix="layout" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>${title}</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/styles.css"/>" />
	</head>
	<body>
		<div class="wrapper">
			<layout:header />
			<section id="content">
				<jsp:doBody />
			</section>
			<layout:footer />
		</div>
	</body>
</html>