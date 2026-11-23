<c:set var="pageTitle" value="Akses ditolak" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="text-center py-5">
  <h1 class="display-4 fw-bold">403</h1>
  <p class="lead mb-1">Akses ditolak</p>
  <p class="text-muted">Anda tidak punya izin untuk membuka halaman ini.</p>
  <a href="${ctx}/home" class="btn btn-primary">Kembali ke beranda</a>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
