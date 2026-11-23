<c:set var="pageTitle" value="Login" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="card shadow-sm auth-card">
  <div class="card-body p-4">
    <h1 class="h4 mb-3">Masuk</h1>
    <form method="post" action="${ctx}/login" novalidate>
      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" class="form-control" id="email" name="email" value="${fn:escapeXml(email)}" autofocus required>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input type="password" class="form-control" id="password" name="password" required>
      </div>
      <button type="submit" class="btn btn-primary w-100">Login</button>
    </form>
    <p class="text-center text-muted small mt-3 mb-0">Belum punya akun? <a href="${ctx}/register">Daftar</a></p>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
