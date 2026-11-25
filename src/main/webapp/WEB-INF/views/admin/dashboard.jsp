<c:set var="pageTitle" value="Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<h1 class="h4 mb-3">Dashboard</h1>

<div class="row g-3 mb-4">
  <div class="col-6 col-lg-3">
    <div class="card shadow-sm h-100"><div class="card-body">
      <div class="text-muted small">Total orders</div>
      <div class="h3 mb-0">${stats.totalOrders}</div>
      <div class="small text-muted">Today: ${stats.ordersToday}</div>
    </div></div>
  </div>

  <div class="col-6 col-lg-3">
    <div class="card shadow-sm h-100"><div class="card-body">
      <div class="text-muted small">Revenue (completed orders)</div>
      <div class="h4 mb-0">Rp <fmt:formatNumber value="${stats.revenueTotal}" groupingUsed="true" maxFractionDigits="0"/></div>
      <div class="small text-muted">Today: Rp <fmt:formatNumber value="${stats.revenueToday}" groupingUsed="true" maxFractionDigits="0"/></div>
    </div></div>
  </div>

  <div class="col-6 col-lg-3">
    <div class="card shadow-sm h-100"><div class="card-body">
      <div class="text-muted small">Revenue this month</div>
      <div class="h4 mb-0">Rp <fmt:formatNumber value="${stats.revenueMonth}" groupingUsed="true" maxFractionDigits="0"/></div>
    </div></div>
  </div>

  <div class="col-6 col-lg-3">
    <div class="card shadow-sm h-100"><div class="card-body">
      <div class="text-muted small">Average rating</div>
      <c:choose>
        <c:when test="${stats.reviewCount > 0}">
          <div class="h3 mb-0">
            <fmt:formatNumber value="${stats.avgRating}" minFractionDigits="1" maxFractionDigits="1"/>
            <span class="fs-6 text-muted">/ 5 &#9733;</span>
          </div>
          <div class="small text-muted">from ${stats.reviewCount} reviews</div>
        </c:when>
        <c:otherwise>
          <div class="h5 mb-0 text-muted">No reviews yet</div>
        </c:otherwise>
      </c:choose>
    </div></div>
  </div>
</div>

<div class="row g-3">
  <div class="col-12 col-lg-5">
    <div class="card shadow-sm h-100">
      <div class="card-header bg-white fw-semibold">Orders by status</div>
      <ul class="list-group list-group-flush">
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Pending <span class="badge text-bg-warning">${stats.pending}</span>
        </li>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Processing <span class="badge text-bg-info">${stats.processing}</span>
        </li>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Ready to Serve <span class="badge text-bg-primary">${stats.ready}</span>
        </li>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Completed <span class="badge text-bg-success">${stats.completed}</span>
        </li>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Cancelled <span class="badge text-bg-secondary">${stats.cancelled}</span>
        </li>
      </ul>
    </div>
  </div>

  <div class="col-12 col-lg-7">
    <div class="card shadow-sm h-100">
      <div class="card-header bg-white fw-semibold">Top selling menu (top 5)</div>
      <div class="table-responsive">
        <table class="table table-stack align-middle mb-0">
          <thead class="table-light">
            <tr><th>#</th><th>Menu</th><th class="text-end">Sold</th><th class="text-end">Revenue</th></tr>
          </thead>
          <tbody>
            <c:forEach var="m" items="${stats.topMenus}" varStatus="st">
              <tr>
                <td>${st.count}</td>
                <td><c:out value="${m.name}"/></td>
                <td class="text-end">${m.sold}</td>
                <td class="text-end">Rp <fmt:formatNumber value="${m.revenue}" groupingUsed="true" maxFractionDigits="0"/></td>
              </tr>
            </c:forEach>
            <c:if test="${empty stats.topMenus}">
              <tr><td colspan="4" class="text-center text-muted py-4">No sales data yet.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>