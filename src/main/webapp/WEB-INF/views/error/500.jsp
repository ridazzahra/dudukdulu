<c:set var="pageTitle" value="Terjadi kesalahan" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="text-center py-5">
  <h1 class="display-4 fw-bold">500</h1>
  <p class="lead mb-1">Terjadi kesalahan</p>
  <p class="text-muted">Maaf, ada masalah di server. Silakan coba lagi atau hubungi admin.</p>
  <a href="${ctx}/home" class="btn btn-primary">Kembali ke beranda</a>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
