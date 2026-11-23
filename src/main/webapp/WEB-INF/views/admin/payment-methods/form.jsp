<c:set var="isEdit" value="${method.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Ubah metode pembayaran' : 'Tambah metode pembayaran'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<h1 class="h4 mb-3">${isEdit ? 'Ubah metode pembayaran' : 'Tambah metode pembayaran'}</h1>
<div class="card shadow-sm" style="max-width:640px">
  <div class="card-body">
    <form method="post" action="${ctx}/admin/payment-methods/${isEdit ? 'edit' : 'create'}" novalidate>
      <c:if test="${isEdit}"><input type="hidden" name="id" value="${method.id}"></c:if>
      <div class="mb-3">
        <label for="name" class="form-label">Nama metode</label>
        <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" id="name" name="name"
               value="${fn:escapeXml(method.name)}" maxlength="50" placeholder="mis. QRIS, Transfer Bank BCA" autofocus required>
        <div class="invalid-feedback">${errors.name}</div>
      </div>
      <div class="mb-3">
        <label for="type" class="form-label">Jenis</label>
        <select id="type" name="type" class="form-select ${not empty errors.type ? 'is-invalid' : ''}">
          <c:forEach var="t" items="${types}">
            <option value="${t.key}" ${method.type == t.key ? 'selected' : ''}>${t.value}</option>
          </c:forEach>
        </select>
        <div class="invalid-feedback">${errors.type}</div>
      </div>
      <div class="mb-3">
        <label for="note" class="form-label">Instruksi / nomor rekening <span class="text-muted small">(opsional)</span></label>
        <textarea id="note" name="note" rows="3" maxlength="255"
                  class="form-control ${not empty errors.note ? 'is-invalid' : ''}"><c:out value="${method.note}"/></textarea>
        <div class="invalid-feedback">${errors.note}</div>
      </div>
      <div class="form-check form-switch mb-4">
        <input class="form-check-input" type="checkbox" role="switch" id="active" name="active" ${method.active ? 'checked' : ''}>
        <label class="form-check-label" for="active">Aktif (tampil di pilihan checkout customer)</label>
      </div>
      <button type="submit" class="btn btn-primary">Simpan</button>
      <a href="${ctx}/admin/payment-methods" class="btn btn-link">Batal</a>
    </form>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
