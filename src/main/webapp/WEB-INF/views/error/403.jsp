<c:set var="pageTitle" value="Access denied" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="text-center py-5">
  <h1 class="display-4 fw-bold">403</h1>
  <p class="lead mb-1">Access denied</p>
  <p class="text-muted">You don't have permission to access this page.</p>
  <a href="${ctx}/home" class="btn btn-primary">Back to home</a>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>