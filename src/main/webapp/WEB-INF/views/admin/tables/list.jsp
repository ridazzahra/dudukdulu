<c:set var="pageTitle" value="Meja" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Meja</h1>
  <a href="${ctx}/admin/tables/create" class="btn btn-primary">+ Tambah meja</a>
</div>

<form method="get" class="row g-2 mb-3">
  <div class="col-sm-5 col-md-3">
    <input type="search" name="q" value="${fn:escapeXml(q)}" class="form-control" placeholder="Cari nomor meja">
  </div>
  <div class="col-sm-4 col-md-3">
    <select name="status" class="form-select">
      <option value="">Semua status</option>
      <c:forEach var="s" items="${statuses}">
        <option value="${s.key}" ${status == s.key ? 'selected' : ''}>${s.value}</option>
      </c:forEach>
    </select>
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-outline-secondary">Filter</button>
    <c:if test="${not empty q or not empty status}"><a href="${ctx}/admin/tables" class="btn btn-link">Reset</a></c:if>
  </div>
</form>

<div class="card shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr><th>No. meja</th><th>Kapasitas</th><th>Status</th><th class="text-end">Aksi</th></tr>
      </thead>
      <tbody>
        <c:forEach var="t" items="${pager.items}">
          <tr>
            <td class="fw-semibold">Meja ${t.number}</td>
            <td>${t.capacity} orang</td>
            <td>
              <span class="badge ${t.status == 'available' ? 'text-bg-success' : (t.status == 'occupied' ? 'text-bg-danger' : 'text-bg-warning')}">
                ${t.statusLabel}
              </span>
            </td>
            <td class="text-end">
              <a href="${ctx}/admin/tables/edit?id=${t.id}" class="btn btn-sm btn-outline-primary">Ubah</a>
              <button type="button" class="btn btn-sm btn-outline-danger"
                      data-delete-url="${ctx}/admin/tables/delete" data-delete-id="${t.id}"
                      data-delete-name="Meja ${t.number}">Hapus</button>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${pager.totalItems == 0}">
          <tr><td colspan="4" class="text-center text-muted py-4">Tidak ada meja.</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>
<div class="mt-3"><jsp:include page="/WEB-INF/views/components/pagination.jsp"/></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
