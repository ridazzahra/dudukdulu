<c:set var="pageTitle" value="Halaman tidak ditemukan" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="text-center py-5">
  <h1 class="display-4 fw-bold">404</h1>
  <p class="lead mb-1">Halaman tidak ditemukan</p>
  <p class="text-muted">Alamat yang Anda buka tidak ada atau sudah dipindahkan.</p>
  <a href="${ctx}/home" class="btn btn-primary">Kembali ke beranda</a>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
