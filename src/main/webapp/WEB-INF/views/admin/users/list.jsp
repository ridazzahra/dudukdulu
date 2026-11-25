<c:set var="pageTitle" value="Manage Users" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
  <h1 class="h4 mb-0">Manage Users</h1>
  <a href="${ctx}/admin/users/create" class="btn btn-primary btn-sm">+ Add User</a>
</div>

<form method="get" action="${ctx}/admin/users" class="row g-2 mb-3">
  <div class="col-12 col-md-5">
    <input type="search" name="q" class="form-control" placeholder="Search name, email, or phone..."
           value="<c:out value='${q}'/>">
  </div>
  <div class="col-6 col-md-2">
    <select name="role" class="form-select" aria-label="Filter by role">
      <option value="">All roles</option>
      <option value="admin" ${role == 'admin' ? 'selected' : ''}>Admin</option>
      <option value="customer" ${role == 'customer' ? 'selected' : ''}>Customer</option>
    </select>
  </div>
  <div class="col-6 col-md-2">
    <select name="status" class="form-select" aria-label="Filter by status">
      <option value="">All statuses</option>
      <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
      <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
    </select>
  </div>
  <div class="col-12 col-md-3 d-flex gap-2">
    <button class="btn btn-outline-primary flex-fill" type="submit">Search</button>
    <a href="${ctx}/admin/users" class="btn btn-outline-secondary flex-fill">Reset</a>
  </div>
</form>

<div class="table-responsive">
  <table class="table table-stack table-hover align-middle mb-0">
    <thead class="table-light">
      <tr>
        <th>Name</th>
        <th>Email</th>
        <th>Role</th>
        <th>Status</th>
        <th class="text-center">Orders</th>
        <th class="text-end">Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="u" items="${result.items}">
        <tr>
          <td><c:out value="${u.name}"/><c:if test="${u.id == meId}"> <span class="badge text-bg-light">You</span></c:if></td>
          <td class="text-break"><c:out value="${u.email}"/></td>
          <td><span class="badge ${u.admin ? 'text-bg-dark' : 'text-bg-info'}"><c:out value="${u.role}"/></span></td>
          <td><span class="badge ${u.active ? 'text-bg-success' : 'text-bg-secondary'}">${u.active ? 'Active' : 'Inactive'}</span></td>
          <td class="text-center">
            <a href="${ctx}/admin/users/orders?id=${u.id}">${u.orderCount}</a>
          </td>
          <td class="text-end">
            <div class="d-flex flex-wrap justify-content-end gap-1">
              <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/users/orders?id=${u.id}">History</a>
              <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/users/edit?id=${u.id}">Edit</a>
              <form method="post" action="${ctx}/admin/users/toggle" class="d-inline">
                <input type="hidden" name="id" value="${u.id}">
                <input type="hidden" name="active" value="${u.active ? 0 : 1}">
                <button type="submit" class="btn btn-sm ${u.active ? 'btn-outline-warning' : 'btn-outline-success'}"
                        ${u.id == meId ? 'disabled' : ''}>${u.active ? 'Deactivate' : 'Activate'}</button>
              </form>
              <button type="button" class="btn btn-sm btn-outline-danger"
                      data-delete-url="${ctx}/admin/users/delete"
                      data-delete-id="${u.id}"
                      data-delete-name="<c:out value='${u.name}'/>"
                      ${u.id == meId ? 'disabled' : ''}>Delete</button>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty result.items}">
        <tr><td colspan="6" class="text-center text-muted py-4">No matching users found.</td></tr>
      </c:if>
    </tbody>
  </table>
</div>

<div class="small text-muted">Showing ${result.from}&ndash;${result.to} of ${result.total} users</div>
<jsp:include page="_pager.jsp"/>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>