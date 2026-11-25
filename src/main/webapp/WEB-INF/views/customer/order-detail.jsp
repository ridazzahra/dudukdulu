<c:set var="pageTitle" value="Order #${order.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">
    Order #${order.id}
    <span class="badge ${order.statusBadge} ms-2"><c:out value="${order.statusLabel}"/></span>
  </h1>
  <a href="${ctx}/orders" class="btn btn-outline-secondary">&larr; My orders</a>
</div>

<div class="row g-3">
  <div class="col-lg-8">
    <div class="card shadow-sm mb-3">
      <div class="card-body">
        <div class="row small">
          <div class="col-sm-4 mb-2 mb-sm-0">
            <div class="text-muted">Date</div>
            <div><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, HH:mm"/></div>
          </div>
          <div class="col-sm-4 mb-2 mb-sm-0">
            <div class="text-muted">Table</div>
            <div>${order.tableNumber}</div>
          </div>
          <div class="col-sm-4">
            <div class="text-muted">Completed</div>
            <div>
              <c:choose>
                <c:when test="${not empty order.completedAt}">
                  <fmt:formatDate value="${order.completedAt}" pattern="dd MMM yyyy, HH:mm"/>
                </c:when>
                <c:otherwise>-</c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
        <c:if test="${not empty order.note}">
          <hr>
          <div class="small text-muted">Note</div>
          <div><c:out value="${order.note}"/></div>
        </c:if>
      </div>
    </div>

    <div class="card shadow-sm">
      <div class="table-responsive">
        <table class="table align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th>Menu</th>
              <th class="text-end">Price</th>
              <th class="text-center">Qty</th>
              <th class="text-end">Subtotal</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="item" items="${order.items}">
              <tr>
                <td>
                  <div class="d-flex align-items-center gap-3">
                    <img src="${ctx}/${fn:escapeXml(item.menuImage)}" alt="" class="menu-thumb">
                    <span class="fw-semibold"><c:out value="${item.menuName}"/></span>
                  </div>
                </td>
                <td class="text-end text-nowrap">Rp <fmt:formatNumber value="${item.price}" pattern="#,##0"/></td>
                <td class="text-center">${item.quantity}</td>
                <td class="text-end text-nowrap">Rp <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/></td>
              </tr>
            </c:forEach>
          </tbody>
          <tfoot>
            <tr>
              <th colspan="3" class="text-end">Total</th>
              <th class="text-end text-nowrap">Rp <fmt:formatNumber value="${order.totalPrice}" pattern="#,##0"/></th>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  </div>

  <div class="col-lg-4">
    <div class="card shadow-sm mb-3">
      <div class="card-body">
        <h2 class="h6 mb-3">Payment</h2>
        <c:choose>
          <c:when test="${empty order.payment}">
            <p class="text-muted small mb-0">No payment record for this order.</p>
          </c:when>
          <c:otherwise>
            <div class="d-flex justify-content-between mb-1">
              <span class="text-muted">Method</span>
              <span><c:out value="${order.payment.methodName}"/></span>
            </div>
            <div class="d-flex justify-content-between mb-1">
              <span class="text-muted">Amount</span>
              <span>Rp <fmt:formatNumber value="${order.payment.amount}" pattern="#,##0"/></span>
            </div>
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Status</span>
              <span class="badge ${order.payment.statusBadge}"><c:out value="${order.payment.statusLabel}"/></span>
            </div>
            <c:if test="${order.payment.paid and not empty order.payment.paidAt}">
              <div class="small text-muted mb-2">
                Paid on <fmt:formatDate value="${order.payment.paidAt}" pattern="dd MMM yyyy, HH:mm"/>
              </div>
            </c:if>
            <c:if test="${order.payment.pending}">
              <div class="alert alert-warning small mb-0">
                <c:if test="${not empty method and not empty method.note}">
                  <div class="mb-1"><strong>How to pay:</strong> <c:out value="${method.note}"/></div>
                </c:if>
                Your payment will be confirmed by our staff.
              </div>
            </c:if>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <c:if test="${order.canCustomerCancel}">
      <form method="post" action="${ctx}/orders/cancel" class="mb-3"
            onsubmit="return confirm('Cancel order #${order.id}?');">
        <input type="hidden" name="id" value="${order.id}">
        <button type="submit" class="btn btn-outline-danger w-100">Cancel order</button>
      </form>
    </c:if>

    <c:if test="${order.canBeReviewed}">
      <a href="${ctx}/reviews/create?orderId=${order.id}" class="btn btn-primary w-100">Write a review</a>
    </c:if>
    <c:if test="${order.reviewed and not empty review}">
      <div class="card shadow-sm">
        <div class="card-body">
          <h2 class="h6 mb-2">Your review</h2>
          <div class="mb-1">
            <fmt:formatNumber value="${review.rating}" minFractionDigits="1" maxFractionDigits="1"/> &#9733;
          </div>
          <p class="mb-3 text-break"><c:out value="${review.comment}"/></p>
          <a href="${ctx}/reviews/edit?id=${review.id}" class="btn btn-sm btn-outline-primary">Edit</a>
          <button type="button" class="btn btn-sm btn-outline-danger"
                  data-delete-url="${ctx}/reviews/delete"
                  data-delete-id="${review.id}"
                  data-delete-name="your review">Delete</button>
        </div>
      </div>
    </c:if>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
