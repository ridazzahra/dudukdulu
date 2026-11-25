<c:set var="pageTitle" value="Menu" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h4 mb-0">Menu</h1>
  <a href="${ctx}/admin/menus/create" class="btn btn-primary">+ Add Menu</a>
</div>

<form method="get" class="row g-2 mb-3">
  <div class="col-12 col-md-3">
    <input type="search" name="q" value="${fn:escapeXml(q)}" class="form-control" placeholder="Search menu name">
  </div>
  <div class="col-6 col-md-3">
    <select name="category" class="form-select" aria-label="Category">
      <option value="">All Categories</option>
      <c:forEach var="c" items="${categories}">
        <option value="${c.id}" ${category == c.id ? 'selected' : ''}><c:out value="${c.name}"/></option>
      </c:forEach>
    </select>
  </div>
  <div class="col-6 col-md-2">
    <select name="availability" class="form-select" aria-label="Availability">
      <option value="">All Status</option>
      <option value="1" ${availability == '1' ? 'selected' : ''}>Available</option>
      <option value="0" ${availability == '0' ? 'selected' : ''}>Sold out</option>
    </select>
  </div>
  <div class="col-6 col-md-2">
    <select name="sort" class="form-select" aria-label="Sort by">
      <option value="name" ${sort == 'name' or empty sort ? 'selected' : ''}>Name (A-Z)</option>
      <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Lowest Price</option>
      <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Highest Price</option>
      <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Newest</option>
    </select>
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-outline-secondary">Apply</button>
    <c:if test="${not empty q or not empty category or not empty availability or not empty sort}">
      <a href="${ctx}/admin/menus" class="btn btn-outline-secondary">Reset</a>
    </c:if>
  </div>
</form>

<c:if test="${pager.totalItems == 0}">
  <div class="card shadow-sm">
    <div class="card-body text-center text-muted py-4">No matching menu items found.</div>
  </div>
</c:if>

<div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-3 align-items-start">
  <c:forEach var="m" items="${pager.items}">
    <div class="col">
      <div class="card menu-card shadow-sm">

        <a class="menu-card-toggle" data-bs-toggle="collapse" href="#menu-${m.id}" role="button"
           aria-expanded="false" aria-controls="menu-${m.id}">
          <img src="${ctx}/${fn:escapeXml(m.imageLink)}" alt=""
               class="menu-card-img ${m.available ? '' : 'is-soldout'}">
          <div class="d-flex justify-content-between align-items-center gap-2 p-3">
            <div>
              <div class="fw-semibold text-break"><c:out value="${m.name}"/></div>
              <div class="fw-semibold" style="color: var(--color-clay)">
                Rp <fmt:formatNumber value="${m.price}" pattern="#,##0"/>
              </div>
            </div>
            <div class="d-flex align-items-center gap-2 flex-shrink-0">
              <span class="badge ${m.available ? 'text-bg-success' : 'text-bg-secondary'}">
                ${m.available ? 'Available' : 'Sold out'}
              </span>
              <span class="menu-card-chevron">&#9662;</span>
            </div>
          </div>
        </a>

        <div class="collapse" id="menu-${m.id}">
          <div class="p-3 border-top">
            <div class="small text-muted">Category</div>
            <div class="mb-2"><c:out value="${m.categoryName}"/></div>
            <div class="small text-muted">Description</div>
            <p class="mb-3 text-break"><c:out value="${m.description}" default="-"/></p>
            <div class="d-flex flex-wrap gap-2">
              <form method="post" action="${ctx}/admin/menus/toggle" class="d-inline">
                <input type="hidden" name="id" value="${m.id}">
                <input type="hidden" name="back" value="${fn:escapeXml(backQuery)}">
                <button type="submit" class="btn btn-sm btn-outline-secondary">
                  ${m.available ? 'Mark as sold out' : 'Mark as available'}
                </button>
              </form>
              <a href="${ctx}/admin/menus/edit?id=${m.id}" class="btn btn-sm btn-outline-primary">Change</a>
              <button type="button" class="btn btn-sm btn-outline-danger"
                      data-delete-url="${ctx}/admin/menus/delete" data-delete-id="${m.id}"
                      data-delete-name="${fn:escapeXml(m.name)}">Delete</button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </c:forEach>
</div>

<div class="mt-3"><jsp:include page="/WEB-INF/views/components/pagination.jsp"/></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>