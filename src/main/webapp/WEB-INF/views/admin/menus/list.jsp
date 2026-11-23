<c:set var="pageTitle" value="Menu" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Menu</h1>
  <a href="${ctx}/admin/menus/create" class="btn btn-primary">+ Tambah menu</a>
</div>

<form method="get" class="row g-2 mb-3">
  <div class="col-12 col-md-3">
    <input type="search" name="q" value="${fn:escapeXml(q)}" class="form-control" placeholder="Cari nama menu">
  </div>
  <div class="col-6 col-md-3">
    <select name="category" class="form-select" aria-label="Kategori">
      <option value="">Semua kategori</option>
      <c:forEach var="c" items="${categories}">
        <option value="${c.id}" ${category == c.id ? 'selected' : ''}><c:out value="${c.name}"/></option>
      </c:forEach>
    </select>
  </div>
  <div class="col-6 col-md-2">
    <select name="availability" class="form-select" aria-label="Ketersediaan">
      <option value="">Semua status</option>
      <option value="1" ${availability == '1' ? 'selected' : ''}>Tersedia</option>
      <option value="0" ${availability == '0' ? 'selected' : ''}>Sold out</option>
    </select>
  </div>
  <div class="col-6 col-md-2">
    <select name="sort" class="form-select" aria-label="Urutkan">
      <option value="name" ${sort == 'name' or empty sort ? 'selected' : ''}>Nama A-Z</option>
      <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Harga termurah</option>
      <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Harga termahal</option>
      <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Terbaru</option>
    </select>
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-outline-secondary">Terapkan</button>
    <c:if test="${not empty q or not empty category or not empty availability or not empty sort}">
      <a href="${ctx}/admin/menus" class="btn btn-link">Reset</a>
    </c:if>
  </div>
</form>

<div class="card shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr><th>Menu</th><th>Kategori</th><th class="text-end">Harga</th><th>Status</th><th class="text-end">Aksi</th></tr>
      </thead>
      <tbody>
        <c:forEach var="m" items="${pager.items}">
          <tr>
            <td>
              <div class="d-flex align-items-center gap-3">
                <img src="${ctx}/${fn:escapeXml(m.imageLink)}" alt="" class="menu-thumb">
                <div>
                  <div class="fw-semibold"><c:out value="${m.name}"/></div>
                  <div class="text-muted small"><c:out value="${m.description}"/></div>
                </div>
              </div>
            </td>
            <td><c:out value="${m.categoryName}"/></td>
            <td class="text-end text-nowrap">Rp <fmt:formatNumber value="${m.price}" pattern="#,##0"/></td>
            <td>
              <span class="badge ${m.available ? 'text-bg-success' : 'text-bg-secondary'}">${m.available ? 'Tersedia' : 'Sold out'}</span>
            </td>
            <td class="text-end text-nowrap">
              <form method="post" action="${ctx}/admin/menus/toggle" class="d-inline">
                <input type="hidden" name="id" value="${m.id}">
                <input type="hidden" name="back" value="${fn:escapeXml(backQuery)}">
                <button type="submit" class="btn btn-sm btn-outline-secondary">${m.available ? 'Tandai sold out' : 'Tandai tersedia'}</button>
              </form>
              <a href="${ctx}/admin/menus/edit?id=${m.id}" class="btn btn-sm btn-outline-primary">Ubah</a>
              <button type="button" class="btn btn-sm btn-outline-danger"
                      data-delete-url="${ctx}/admin/menus/delete" data-delete-id="${m.id}"
                      data-delete-name="${fn:escapeXml(m.name)}">Hapus</button>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${pager.totalItems == 0}">
          <tr><td colspan="5" class="text-center text-muted py-4">Tidak ada menu yang cocok.</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>
<div class="mt-3"><jsp:include page="/WEB-INF/views/components/pagination.jsp"/></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
