<c:set var="isEdit" value="${category.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Category' : 'Add Category'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<h1 class="h4 mb-3">${isEdit ? 'Edit Category' : 'Add Category'}</h1>
<div class="card shadow-sm" style="max-width:560px">
  <div class="card-body">
    <form method="post" action="${ctx}/admin/categories/${isEdit ? 'edit' : 'create'}" novalidate>
      <c:if test="${isEdit}"><input type="hidden" name="id" value="${category.id}"></c:if>
      <div class="mb-3">
        <label for="name" class="form-label">Category name</label>
        <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" id="name" name="name"
              value="${fn:escapeXml(category.name)}" maxlength="255" autofocus required>
        <div class="invalid-feedback">${errors.name}</div>
      </div>
      <button type="submit" class="btn btn-primary">Save</button>
      <a href="${ctx}/admin/categories" class="btn btn-link">Cancel</a>
    </form>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>