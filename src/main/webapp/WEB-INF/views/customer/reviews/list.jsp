<c:set var="pageTitle" value="My Reviews" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">My Reviews</h1>
  <a href="${ctx}/orders" class="btn btn-outline-secondary">&larr; My orders</a>
</div>

<c:choose>
  <c:when test="${empty reviews}">
    <div class="card shadow-sm">
      <div class="card-body text-center py-5">
        <p class="text-muted mb-3">You have not written any reviews yet.</p>
        <a href="${ctx}/orders" class="btn btn-primary">Go to my orders</a>
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
              <th>Rating</th>
              <th>Comment</th>
              <th>Date</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${reviews}">
              <tr>
                <td><a href="${ctx}/orders/detail?id=${r.orderId}">#${r.orderId}</a></td>
                <td class="text-nowrap">
                  <fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1"/> &#9733;
                </td>
                <td class="text-break" style="min-width:200px"><c:out value="${r.comment}"/></td>
                <td class="text-nowrap"><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy, HH:mm"/></td>
                <td class="text-end text-nowrap">
                  <a href="${ctx}/reviews/edit?id=${r.id}" class="btn btn-sm btn-outline-primary">Edit</a>
                  <button type="button" class="btn btn-sm btn-outline-danger"
                          data-delete-url="${ctx}/reviews/delete"
                          data-delete-id="${r.id}"
                          data-delete-name="your review for order #${r.orderId}">Delete</button>
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