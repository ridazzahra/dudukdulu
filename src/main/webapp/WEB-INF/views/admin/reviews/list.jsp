<c:set var="pageTitle" value="Manage Reviews" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<h1 class="h4 mb-3">Manage Reviews</h1>

<form method="get" action="${ctx}/admin/reviews" class="row g-2 mb-3">
  <div class="col-12 col-md-6">
    <input type="search" name="q" class="form-control" placeholder="Search comment, name, or email..."
           value="<c:out value='${q}'/>">
  </div>
  <div class="col-12 col-md-3">
    <select name="star" class="form-select" aria-label="Filter by rating">
      <option value="0">All ratings</option>
      <option value="5" ${star == 5 ? 'selected' : ''}>5 stars</option>
      <option value="4" ${star == 4 ? 'selected' : ''}>4 stars</option>
      <option value="3" ${star == 3 ? 'selected' : ''}>3 stars</option>
      <option value="2" ${star == 2 ? 'selected' : ''}>2 stars</option>
      <option value="1" ${star == 1 ? 'selected' : ''}>1 star</option>
    </select>
  </div>
  <div class="col-12 col-md-3 d-flex gap-2">
    <button class="btn btn-outline-primary flex-fill" type="submit">Search</button>
    <a href="${ctx}/admin/reviews" class="btn btn-outline-secondary flex-fill d-flex align-items-center justify-content-center">Reset</a>
  </div>
</form>

<div class="card shadow-sm">
<div class="table-responsive">
  <table class="table table-stack table-hover align-middle mb-0">
    <thead class="table-light">
      <tr>
        <th>Customer</th>
        <th>Order</th>
        <th>Rating</th>
        <th>Comment</th>
        <th>Date</th>
        <th class="text-end">Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="r" items="${result.items}">
        <tr>
          <td>
            <c:out value="${r.userName}"/><br>
            <span class="small text-muted text-break"><c:out value="${r.userEmail}"/></span>
          </td>
          <td>#${r.orderId}</td>
          <td class="text-nowrap">
            <fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1"/> &#9733;
          </td>
          <td class="text-break" style="min-width:180px"><c:out value="${r.comment}"/></td>
          <td class="text-nowrap"><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
          <td class="text-end">
            <button type="button" class="btn btn-sm btn-outline-danger"
                    data-delete-url="${ctx}/admin/reviews/delete"
                    data-delete-id="${r.id}"
                    data-delete-name="review from <c:out value='${r.userName}'/>">Delete</button>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty result.items}">
        <tr><td colspan="6" class="text-center text-muted py-4">No matching reviews found.</td></tr>
      </c:if>
    </tbody>
  </table>
</div>
</div>

<div class="small text-muted">Showing ${result.from}&ndash;${result.to} of ${result.total} reviews</div>
<jsp:include page="/WEB-INF/views/admin/users/_pager.jsp"/>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>