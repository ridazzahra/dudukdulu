<c:set var="isEdit" value="${method.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit payment method' : 'Add payment method'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<h1 class="h4 mb-3">${isEdit ? 'Edit payment method' : 'Add payment method'}</h1>
<div class="card shadow-sm" style="max-width:640px">
  <div class="card-body">
    <form method="post" action="${ctx}/admin/payment-methods/${isEdit ? 'edit' : 'create'}" novalidate>
      <c:if test="${isEdit}"><input type="hidden" name="id" value="${method.id}"></c:if>
      <div class="mb-3">
        <label for="name" class="form-label">Method name</label>
        <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" id="name" name="name"
               value="${fn:escapeXml(method.name)}" maxlength="50" placeholder="e.g., QRIS, BCA Bank Transfer" autofocus required>
        <div class="invalid-feedback">${errors.name}</div>
      </div>
      <div class="mb-3">
        <label for="type" class="form-label">Type</label>
        <select id="type" name="type" class="form-select ${not empty errors.type ? 'is-invalid' : ''}">
          <c:forEach var="t" items="${types}">
            <option value="${t.key}" ${method.type == t.key ? 'selected' : ''}>${t.value}</option>
          </c:forEach>
        </select>
        <div class="invalid-feedback">${errors.type}</div>
      </div>
      <div class="mb-3">
        <label for="note" class="form-label">Instructions / account number <span class="text-muted small">(optional)</span></label>
        <textarea id="note" name="note" rows="3" maxlength="255"
                  class="form-control ${not empty errors.note ? 'is-invalid' : ''}"><c:out value="${method.note}"/></textarea>
        <div class="invalid-feedback">${errors.note}</div>
      </div>
      <div class="form-check form-switch mb-4">
        <input class="form-check-input" type="checkbox" role="switch" id="active" name="active" ${method.active ? 'checked' : ''}>
        <label class="form-check-label" for="active">Active (shown in customer checkout options)</label>
      </div>
      <button type="submit" class="btn btn-primary">Save</button>
      <a href="${ctx}/admin/payment-methods" class="btn btn-link">Cancel</a>
    </form>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>