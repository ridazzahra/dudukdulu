<c:set var="isEdit" value="${table.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Table' : 'Add Table'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<h1 class="h4 mb-3">${isEdit ? 'Edit Table' : 'Add Table'}</h1>
<div class="card shadow-sm" style="max-width:560px">
  <div class="card-body">
    <form method="post" action="${ctx}/admin/tables/${isEdit ? 'edit' : 'create'}" novalidate>
      <c:if test="${isEdit}"><input type="hidden" name="id" value="${table.id}"></c:if>
      <div class="row g-3">
        <div class="col-sm-6">
          <label for="number" class="form-label">Table number</label>
          <input type="number" min="1" max="999" class="form-control ${not empty errors.number ? 'is-invalid' : ''}"
                 id="number" name="number" value="${table.number > 0 ? table.number : ''}" autofocus required>
          <div class="invalid-feedback">${errors.number}</div>
        </div>
        <div class="col-sm-6">
          <label for="capacity" class="form-label">Capacity (guests)</label>
          <input type="number" min="1" max="50" class="form-control ${not empty errors.capacity ? 'is-invalid' : ''}"
                 id="capacity" name="capacity" value="${table.capacity > 0 ? table.capacity : ''}" required>
          <div class="invalid-feedback">${errors.capacity}</div>
        </div>
        <div class="col-12">
          <label for="status" class="form-label">Status</label>
          <select id="status" name="status" class="form-select ${not empty errors.status ? 'is-invalid' : ''}">
            <c:forEach var="s" items="${statuses}">
              <option value="${s.key}" ${table.status == s.key ? 'selected' : ''}>${s.value}</option>
            </c:forEach>
          </select>
          <div class="invalid-feedback">${errors.status}</div>
        </div>
      </div>
      <div class="mt-4">
        <button type="submit" class="btn btn-primary">Save</button>
        <a href="${ctx}/admin/tables" class="btn btn-link">Cancel</a>
      </div>
    </form>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>