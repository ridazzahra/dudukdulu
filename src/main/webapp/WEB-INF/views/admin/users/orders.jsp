<c:set var="pageTitle" value="Order History" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
  <div>
    <h1 class="h4 mb-0">Order History</h1>
    <div class="text-muted"><c:out value="${u.name}"/> &middot; <c:out value="${u.email}"/></div>
  </div>
  <a href="${ctx}/admin/users" class="btn btn-outline-secondary btn-sm">&laquo; Back</a>
</div>

<div class="table-responsive">
  <table class="table table-hover align-middle">
    <thead class="table-light">
      <tr><th>Order No.</th><th>Date</th><th>Status</th><th class="text-end">Total</th></tr>
    </thead>
    <tbody>
      <c:forEach var="o" items="${result.items}">
        <tr>
          <td>#${o.id}</td>
          <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
          <td>
            <c:choose>
              <c:when test="${o.status == 'completed'}"><c:set var="cls" value="text-bg-success"/></c:when>
              <c:when test="${o.status == 'cancelled'}"><c:set var="cls" value="text-bg-secondary"/></c:when>
              <c:when test="${o.status == 'ready'}"><c:set var="cls" value="text-bg-primary"/></c:when>
              <c:when test="${o.status == 'processing'}"><c:set var="cls" value="text-bg-info"/></c:when>
              <c:otherwise><c:set var="cls" value="text-bg-warning"/></c:otherwise>
            </c:choose>
            <span class="badge ${cls}"><c:out value="${o.status}"/></span>
          </td>
          <td class="text-end">Rp <fmt:formatNumber value="${o.total}" groupingUsed="true" maxFractionDigits="0"/></td>
        </tr>
      </c:forEach>
      <c:if test="${empty result.items}">
        <tr><td colspan="4" class="text-center text-muted py-4">This user has no orders yet.</td></tr>
      </c:if>
    </tbody>
  </table>
</div>

<div class="small text-muted">Showing ${result.from}&ndash;${result.to} of ${result.total} orders</div>
<jsp:include page="_pager.jsp"/>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>