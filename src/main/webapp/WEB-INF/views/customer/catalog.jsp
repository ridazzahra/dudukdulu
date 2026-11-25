<c:set var="pageTitle" value="Catalog" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Menu Catalog</h1>
  <a href="${ctx}/cart" class="btn btn-outline-dark">
    Cart <span class="badge text-bg-dark">${cartCount}</span>
  </a>
</div>

<form method="get" action="${ctx}/catalog" class="row g-2 mb-4">
  <div class="col-12 col-md-5">
    <input type="search" name="q" value="${fn:escapeXml(q)}" class="form-control" maxlength="100"
           placeholder="Search menu name" aria-label="Search menu name">
  </div>
  <div class="col-8 col-md-4">
    <select name="category" class="form-select" aria-label="Category">
      <option value="">All categories</option>
      <c:forEach var="cat" items="${categories}">
        <option value="${cat.id}" ${category == cat.id ? 'selected' : ''}><c:out value="${cat.name}"/></option>
      </c:forEach>
    </select>
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-primary">Search</button>
    <c:if test="${not empty q or not empty category}">
      <a href="${ctx}/catalog" class="btn btn-outline-secondary btn-cancel">Reset</a>
    </c:if>
  </div>
</form>

<c:if test="${pager.totalItems == 0}">
  <div class="card shadow-sm">
    <div class="card-body text-center text-muted py-5">No menu matches your search.</div>
  </div>
</c:if>

<div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 row-cols-xl-4 g-3">
  <c:forEach var="m" items="${pager.items}">
    <c:set var="inCart" value="${cartQty[m.id]}"/>
    <div class="col">
      <div class="card h-100 shadow-sm ${m.available ? '' : 'opacity-75'}">
        <img src="${ctx}/${fn:escapeXml(m.imageLink)}" alt="${fn:escapeXml(m.name)}"
             class="card-img-top" style="height: 180px; object-fit: cover;">
        <div class="card-body d-flex flex-column">
          <div class="mb-2">
            <span class="badge text-bg-light border"><c:out value="${m.categoryName}"/></span>
            <c:if test="${not m.available}"><span class="badge text-bg-secondary">Sold out</span></c:if>
          </div>
          <h2 class="h6 mb-1"><c:out value="${m.name}"/></h2>
          <p class="text-muted small flex-grow-1"><c:out value="${m.description}"/></p>
          <div class="fw-semibold mb-2">Rp <fmt:formatNumber value="${m.price}" pattern="#,##0"/></div>
          <c:if test="${not empty inCart}">
            <div class="small text-success mb-2">In cart: ${inCart}</div>
          </c:if>
          <form method="post" action="${ctx}/cart/add" class="mt-auto">
            <input type="hidden" name="menuId" value="${m.id}">
            <input type="hidden" name="back" value="${fn:escapeXml(backQuery)}">
            <button type="submit" class="btn btn-primary w-100" ${m.available ? '' : 'disabled'}>
              ${m.available ? '+ Add to cart' : 'Sold out'}
            </button>
          </form>
        </div>
      </div>
    </div>
  </c:forEach>
</div>

<div class="mt-4"><jsp:include page="/WEB-INF/views/components/pagination.jsp"/></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
