<c:set var="pageTitle" value="Orders" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Orders</h1>
</div>

<div class="d-flex flex-wrap gap-2 mb-3">
  <a href="${ctx}/admin/orders" class="btn btn-sm ${empty status ? 'btn-dark' : 'btn-outline-secondary'}">All</a>
  <c:forEach var="s" items="${statuses}">
    <a href="${ctx}/admin/orders?status=${s.key}"
       class="btn btn-sm ${status == s.key ? 'btn-dark' : 'btn-outline-secondary'}">
      <c:out value="${s.value}"/> <span class="badge text-bg-light border">${counts[s.key]}</span>
    </a>
  </c:forEach>
</div>

<form method="get" class="row g-2 mb-3">
  <div class="col-sm-6 col-md-3">
    <input type="search" name="q" value="${fn:escapeXml(q)}" class="form-control"
           placeholder="Search customer or order #">
  </div>
  <div class="col-sm-6 col-md-2">
    <select name="status" class="form-select">
      <option value="">All status</option>
      <c:forEach var="s" items="${statuses}">
        <option value="${s.key}" ${status == s.key ? 'selected' : ''}><c:out value="${s.value}"/></option>
      </c:forEach>
    </select>
  </div>
  <div class="col-sm-6 col-md-2">
    <input type="date" name="from" value="${fn:escapeXml(from)}" class="form-control" aria-label="From date">
  </div>
  <div class="col-sm-6 col-md-2">
    <input type="date" name="to" value="${fn:escapeXml(to)}" class="form-control" aria-label="To date">
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-outline-secondary">Filter</button>
    <c:if test="${not empty q or not empty status or not empty from or not empty to}">
      <a href="${ctx}/admin/orders" class="btn btn-link">Reset</a>
    </c:if>
  </div>
</form>

<div class="card shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr>
          <th>Order</th>
          <th>Date</th>
          <th>Customer</th>
          <th class="text-center">Table</th>
          <th class="text-center">Items</th>
          <th class="text-end">Total</th>
          <th>Status</th>
          <th>Payment</th>
          <th class="text-end">Actions</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="o" items="${pager.items}">
          <tr>
            <td class="fw-semibold">#${o.id}</td>
            <td class="text-nowrap"><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, HH:mm"/></td>
            <td>
              <div><c:out value="${o.userName}"/></div>
              <div class="small text-muted"><c:out value="${o.userEmail}"/></div>
            </td>
            <td class="text-center">${o.tableNumber}</td>
            <td class="text-center">${o.itemCount}</td>
            <td class="text-end text-nowrap">Rp <fmt:formatNumber value="${o.totalPrice}" pattern="#,##0"/></td>
            <td><span class="badge ${o.statusBadge}"><c:out value="${o.statusLabel}"/></span></td>
            <td>
              <c:if test="${not empty o.payment}">
                <span class="badge ${o.payment.statusBadge}"><c:out value="${o.payment.statusLabel}"/></span>
                <div class="small text-muted"><c:out value="${o.payment.methodName}"/></div>
              </c:if>
            </td>
            <td class="text-end">
              <a href="${ctx}/admin/orders/detail?id=${o.id}" class="btn btn-sm btn-outline-primary">Detail</a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${pager.totalItems == 0}">
          <tr><td colspan="9" class="text-center text-muted py-4">No orders found.</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>
<div class="mt-3"><jsp:include page="/WEB-INF/views/components/pagination.jsp"/></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
