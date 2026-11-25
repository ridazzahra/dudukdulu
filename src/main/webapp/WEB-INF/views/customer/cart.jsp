<c:set var="pageTitle" value="Cart" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Cart</h1>
  <a href="${ctx}/catalog" class="btn btn-outline-secondary">&larr; Continue shopping</a>
</div>

<c:choose>
  <c:when test="${empty items}">
    <div class="card shadow-sm">
      <div class="card-body text-center py-5">
        <p class="text-muted mb-3">Your cart is empty.</p>
        <a href="${ctx}/catalog" class="btn btn-primary">Browse the catalog</a>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="row g-3">
      <div class="col-lg-8">
        <div class="card shadow-sm">
          <div class="table-responsive">
            <table class="table table-stack align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th>Menu</th>
                  <th class="text-end">Price</th>
                  <th class="text-center">Quantity</th>
                  <th class="text-end">Subtotal</th>
                  <th class="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="item" items="${items}">
                  <tr>
                    <td>
                      <div class="d-flex align-items-center gap-3">
                        <img src="${ctx}/${fn:escapeXml(item.menuImage)}" alt="" class="menu-thumb">
                        <div>
                          <div class="fw-semibold"><c:out value="${item.menuName}"/></div>
                          <div class="text-muted small"><c:out value="${item.categoryName}"/></div>
                          <c:if test="${not item.menuAvailable}">
                            <span class="badge text-bg-danger">Sold out</span>
                          </c:if>
                        </div>
                      </div>
                    </td>
                    <td class="text-end text-nowrap">Rp <fmt:formatNumber value="${item.menuPrice}" pattern="#,##0"/></td>
                    <td class="text-center">
                      <form method="post" action="${ctx}/cart/update" class="d-inline-flex align-items-center gap-2">
                        <input type="hidden" name="id" value="${item.id}">
                        <button type="submit" name="quantity" value="${item.quantity - 1}"
                                class="btn btn-sm btn-outline-secondary" aria-label="Decrease"
                                ${item.quantity <= 1 ? 'disabled' : ''}>&minus;</button>
                        <span class="fw-semibold" style="min-width: 1.5rem;">${item.quantity}</span>
                        <button type="submit" name="quantity" value="${item.quantity + 1}"
                                class="btn btn-sm btn-outline-secondary" aria-label="Increase"
                                ${item.quantity >= 99 or not item.menuAvailable ? 'disabled' : ''}>+</button>
                      </form>
                    </td>
                    <td class="text-end text-nowrap">Rp <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/></td>
                    <td class="text-end">
                      <button type="button" class="btn btn-sm btn-outline-danger"
                              data-delete-url="${ctx}/cart/delete" data-delete-id="${item.id}"
                              data-delete-name="${fn:escapeXml(item.menuName)}">Remove</button>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card shadow-sm">
          <div class="card-body">
            <h2 class="h6 mb-3">Summary</h2>
            <div class="d-flex justify-content-between mb-1">
              <span class="text-muted">Items</span><span>${totalQty}</span>
            </div>
            <div class="d-flex justify-content-between fw-semibold fs-5 mb-3">
              <span>Total</span><span>Rp <fmt:formatNumber value="${total}" pattern="#,##0"/></span>
            </div>
            <c:if test="${hasUnavailable}">
              <div class="alert alert-warning small">
                Some items are sold out. Remove them from your cart before checking out.
              </div>
            </c:if>
            <a href="${ctx}/checkout" class="btn btn-success w-100 ${hasUnavailable ? 'disabled' : ''}"
               ${hasUnavailable ? 'aria-disabled="true" tabindex="-1"' : ''}>Proceed to checkout</a>
          </div>
        </div>
      </div>
    </div>
  </c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
