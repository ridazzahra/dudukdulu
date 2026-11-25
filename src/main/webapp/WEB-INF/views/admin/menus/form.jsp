<c:set var="isEdit" value="${menu.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Menu' : 'Add Menu'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<h1 class="h4 mb-3">${isEdit ? 'Edit Menu' : 'Add Menu'}</h1>
<div class="card shadow-sm" style="max-width:720px">
  <div class="card-body">
    <form method="post" action="${ctx}/admin/menus/${isEdit ? 'edit' : 'create'}" enctype="multipart/form-data" novalidate>
      <c:if test="${isEdit}"><input type="hidden" name="id" value="${menu.id}"></c:if>
      <div class="row g-3">
        <div class="col-md-8">
          <label for="name" class="form-label">Menu name</label>
          <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" id="name" name="name"
                 value="${fn:escapeXml(menu.name)}" maxlength="255" autofocus required>
          <div class="invalid-feedback">${errors.name}</div>
        </div>
        <div class="col-md-4">
          <label for="categoryId" class="form-label">Category</label>
          <select id="categoryId" name="categoryId" class="form-select ${not empty errors.categoryId ? 'is-invalid' : ''}" required>
            <option value="">-- Select --</option>
            <c:forEach var="c" items="${categories}">
              <option value="${c.id}" ${menu.categoryId == c.id ? 'selected' : ''}><c:out value="${c.name}"/></option>
            </c:forEach>
          </select>
          <div class="invalid-feedback">${errors.categoryId}</div>
        </div>
        <div class="col-12">
          <label for="description" class="form-label">Description <span class="text-muted small">(optional)</span></label>
          <input type="text" class="form-control ${not empty errors.description ? 'is-invalid' : ''}" id="description"
                 name="description" value="${fn:escapeXml(menu.description)}" maxlength="255">
          <div class="invalid-feedback">${errors.description}</div>
        </div>
        <div class="col-md-5">
          <label for="price" class="form-label">Price (Rp)</label>
          <input type="number" min="0" step="0.01" class="form-control ${not empty errors.price ? 'is-invalid' : ''}"
                 id="price" name="price" value="${menu.price}" required>
          <div class="invalid-feedback">${errors.price}</div>
        </div>
        <div class="col-md-7">
          <label for="image" class="form-label">Image <span class="text-muted small">(JPG/PNG/GIF/WEBP, max 2 MB)</span></label>
          <input type="file" class="form-control ${not empty errors.image ? 'is-invalid' : ''}" id="image" name="image"
                 accept="image/png,image/jpeg,image/gif,image/webp">
          <div class="invalid-feedback">${errors.image}</div>
        </div>
        <div class="col-12 d-flex align-items-center gap-3">
          <img id="imgPreview" src="${ctx}/${fn:escapeXml(menu.imageLink)}" alt="Menu image" class="menu-preview">
          <div class="text-muted small">${isEdit ? 'Leave this blank to keep the current image.' : 'Leave this blank to use the default image.'}</div>
        </div>
        <div class="col-12">
          <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" role="switch" id="available" name="available" ${menu.available ? 'checked' : ''}>
            <label class="form-check-label" for="available">Available (turn off to mark as sold out)</label>
          </div>
        </div>
      </div>
      <div class="mt-4">
        <button type="submit" class="btn btn-primary">Save</button>
        <a href="${ctx}/admin/menus" class="btn btn-link">Cancel</a>
      </div>
    </form>
  </div>
</div>
<script>
  document.getElementById('image').addEventListener('change', function () {
    if (this.files && this.files[0]) {
      document.getElementById('imgPreview').src = URL.createObjectURL(this.files[0]);
    }
  });
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>