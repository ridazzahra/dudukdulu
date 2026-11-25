<c:set var="pageTitle" value="Page not found" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="text-center py-5">
  <h1 class="display-4 fw-bold">404</h1>
  <p class="lead mb-1">Page not found</p>
  <p class="text-muted">The address you are looking for doesn't exist or has been moved.</p>
  <a href="${ctx}/home" class="btn btn-primary">Back to home</a>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>