<c:set var="pageTitle" value="Something went wrong" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="text-center py-5">
  <h1 class="display-4 fw-bold">500</h1>
  <p class="lead mb-1">Something went wrong</p>
  <p class="text-muted">Sorry, there is a server issue. Please try again or contact the admin.</p>
  <a href="${ctx}/home" class="btn btn-primary">Back to home</a>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>