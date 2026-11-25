<c:set var="pageTitle" value="Payment Methods" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Payment Methods</h1>
  <a href="${ctx}/admin/payment-methods/create" class="btn btn-primary">+ Add Method</a>
</div>

<form method="get" class="row g-2 mb-3">
  <div class="col-sm-8 col-md-5">
    <input type="search" name="q" value="${fn:escapeXml(q)}" class="form-control" placeholder="Search method name">
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-outline-secondary">Search</button>
    <c:if test="${not empty q}"><a href="${ctx}/admin/payment-methods" class="btn btn-outline-secondary">Reset</a></c:if>
  </div>
</form>

<div class="card shadow-sm">
  <div class="table-responsive">
    <table class="table table-stack table-hover align-middle mb-0">
      <thead class="table-light">
        <tr><th>Name</th><th>Type</th><th>Instructions / account number</th><th>Status</th><th class="text-end">Action</th></tr>
      </thead>
      <tbody>
        <c:forEach var="m" items="${pager.items}">
          <tr>
            <td class="fw-semibold"><c:out value="${m.name}"/></td>
            <td>${m.typeLabel}</td>
            <td class="text-muted small"><c:out value="${m.note}"/></td>
            <td>
              <span class="badge ${m.active ? 'text-bg-success' : 'text-bg-secondary'}">${m.active ? 'Active' : 'Inactive'}</span>
            </td>
            <td class="text-end text-nowrap">
              <form method="post" action="${ctx}/admin/payment-methods/toggle" class="d-inline">
                <input type="hidden" name="id" value="${m.id}">
                <input type="hidden" name="back" value="${fn:escapeXml(pageContext.request.queryString)}">
                <button type="submit" class="btn btn-sm btn-outline-secondary">${m.active ? 'Deactivate' : 'Activate'}</button>
              </form>
              <a href="${ctx}/admin/payment-methods/edit?id=${m.id}" class="btn btn-sm btn-outline-primary">Edit</a>
              <button type="button" class="btn btn-sm btn-outline-danger"
                      data-delete-url="${ctx}/admin/payment-methods/delete" data-delete-id="${m.id}"
                      data-delete-name="${fn:escapeXml(m.name)}">Delete</button>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${pager.totalItems == 0}">
          <tr><td colspan="5" class="text-center text-muted py-4">No payment methods yet.</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>
<div class="mt-3"><jsp:include page="/WEB-INF/views/components/pagination.jsp"/></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>