<c:set var="pageTitle" value="Daftar" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="card shadow-sm auth-card">
  <div class="card-body p-4">
    <h1 class="h4 mb-3">Buat akun</h1>
    <form method="post" action="${ctx}/register" novalidate>
      <div class="mb-3">
        <label for="name" class="form-label">Nama lengkap</label>
        <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" id="name" name="name"
               value="${fn:escapeXml(name)}" maxlength="255" required>
        <div class="invalid-feedback">${errors.name}</div>
      </div>
      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}" id="email" name="email"
               value="${fn:escapeXml(email)}" maxlength="255" required>
        <div class="invalid-feedback">${errors.email}</div>
      </div>
      <div class="mb-3">
        <label for="phone" class="form-label">Nomor HP</label>
        <input type="tel" class="form-control ${not empty errors.phone ? 'is-invalid' : ''}" id="phone" name="phone"
               value="${fn:escapeXml(phone)}" maxlength="16" placeholder="081234567890" required>
        <div class="invalid-feedback">${errors.phone}</div>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password <span class="text-muted small">(minimal 8 karakter)</span></label>
        <input type="password" class="form-control ${not empty errors.password ? 'is-invalid' : ''}" id="password" name="password" required>
        <div class="invalid-feedback">${errors.password}</div>
      </div>
      <div class="mb-3">
        <label for="confirm" class="form-label">Ulangi password</label>
        <input type="password" class="form-control ${not empty errors.confirm ? 'is-invalid' : ''}" id="confirm" name="confirm" required>
        <div class="invalid-feedback">${errors.confirm}</div>
      </div>
      <button type="submit" class="btn btn-primary w-100">Daftar</button>
    </form>
    <p class="text-center text-muted small mt-3 mb-0">Sudah punya akun? <a href="${ctx}/login">Login</a></p>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
