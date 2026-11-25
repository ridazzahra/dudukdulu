<c:set var="pageTitle" value="My Orders" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">My Orders</h1>
  <a href="${ctx}/catalog" class="btn btn-outline-secondary">&larr; Back to catalog</a>
</div>

<c:choose>
  <c:when test="${empty orders}">
    <div class="card shadow-sm">
      <div class="card-body text-center py-5">
        <p class="text-muted mb-3">You have no orders yet.</p>
        <a href="${ctx}/catalog" class="btn btn-primary">Browse the catalog</a>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="card shadow-sm">
      <div class="table-responsive">
        <table class="table align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th>Order</th>
              <th>Date</th>
              <th class="text-center">Table</th>
              <th class="text-center">Items</th>
              <th class="text-end">Total</th>
              <th>Status</th>
              <th>Payment</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="o" items="${orders}">
              <tr>
                <td class="fw-semibold">#${o.id}</td>
                <td class="text-nowrap"><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, HH:mm"/></td>
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
                <td class="text-end text-nowrap">
                  <a href="${ctx}/orders/detail?id=${o.id}" class="btn btn-sm btn-outline-primary">Detail</a>
                  <c:if test="${o.canBeReviewed}">
                    <a href="${ctx}/reviews/create?orderId=${o.id}" class="btn btn-sm btn-outline-success">Review</a>
                  </c:if>
                  <c:if test="${o.canCustomerCancel}">
                    <form method="post" action="${ctx}/orders/cancel" class="d-inline"
                          onsubmit="return confirm('Cancel order #${o.id}?');">
                      <input type="hidden" name="id" value="${o.id}">
                      <button type="submit" class="btn btn-sm btn-outline-danger">Cancel</button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
