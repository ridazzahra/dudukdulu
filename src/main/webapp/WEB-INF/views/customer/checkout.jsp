<c:set var="pageTitle" value="Checkout" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Checkout</h1>
  <a href="${ctx}/cart" class="btn btn-outline-secondary">&larr; Back to cart</a>
</div>

<div class="row g-3">
  <div class="col-lg-7">
    <form method="post" action="${ctx}/checkout" novalidate>
      <div class="card shadow-sm mb-3">
        <div class="card-body">
          <h2 class="h6 mb-3">1. Choose your table</h2>
          <c:choose>
            <c:when test="${empty tables}">
              <div class="alert alert-warning mb-0">No tables are available right now. Please try again in a moment.</div>
            </c:when>
            <c:otherwise>
              <select name="tableId" class="form-select ${not empty errors.tableId ? 'is-invalid' : ''}"
                      aria-label="Table" required>
                <option value="">Select a table</option>
                <c:forEach var="t" items="${tables}">
                  <option value="${t.id}" ${selectedTable == t.id ? 'selected' : ''}>
                    Table ${t.number} (seats ${t.capacity})
                  </option>
                </c:forEach>
              </select>
              <div class="invalid-feedback"><c:out value="${errors.tableId}"/></div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <div class="card shadow-sm mb-3">
        <div class="card-body">
          <h2 class="h6 mb-3">2. Payment method</h2>
          <c:choose>
            <c:when test="${empty paymentMethods}">
              <div class="alert alert-warning mb-0">No payment methods are available right now.</div>
            </c:when>
            <c:otherwise>
              <div class="list-group">
                <c:forEach var="pm" items="${paymentMethods}">
                  <label class="list-group-item d-flex gap-2">
                    <input class="form-check-input flex-shrink-0 mt-1" type="radio" name="paymentMethodId"
                           value="${pm.id}" ${selectedMethod == pm.id ? 'checked' : ''}>
                    <span>
                      <span class="fw-semibold"><c:out value="${pm.name}"/></span>
                      <c:if test="${not empty pm.note}">
                        <span class="d-block small text-muted"><c:out value="${pm.note}"/></span>
                      </c:if>
                    </span>
                  </label>
                </c:forEach>
              </div>
              <c:if test="${not empty errors.paymentMethodId}">
                <div class="text-danger small mt-2"><c:out value="${errors.paymentMethodId}"/></div>
              </c:if>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <div class="card shadow-sm mb-3">
        <div class="card-body">
          <h2 class="h6 mb-3">3. Note (optional)</h2>
          <textarea name="note" rows="3" maxlength="255" aria-label="Note"
                    class="form-control ${not empty errors.note ? 'is-invalid' : ''}"
                    placeholder="Allergies, spice level, special requests..."><c:out value="${note}"/></textarea>
          <div class="invalid-feedback"><c:out value="${errors.note}"/></div>
        </div>
      </div>

      <button type="submit" class="btn btn-success btn-lg w-100"
              ${empty tables or empty paymentMethods ? 'disabled' : ''}>Place order</button>
    </form>
  </div>

  <div class="col-lg-5">
    <div class="card shadow-sm">
      <div class="card-body">
        <h2 class="h6 mb-3">Order summary</h2>
        <ul class="list-unstyled mb-3">
          <c:forEach var="item" items="${items}">
            <li class="d-flex justify-content-between py-1">
              <span><c:out value="${item.menuName}"/> <span class="text-muted">&times; ${item.quantity}</span></span>
              <span class="text-nowrap">Rp <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/></span>
            </li>
          </c:forEach>
        </ul>
        <hr>
        <div class="d-flex justify-content-between fw-semibold fs-5">
          <span>Total (${totalQty} items)</span>
          <span>Rp <fmt:formatNumber value="${total}" pattern="#,##0"/></span>
        </div>
        <p class="small text-muted mt-3 mb-0">Prices are locked in when you place the order.</p>
      </div>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
