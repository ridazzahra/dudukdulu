<c:set var="isAdmin" value="${not empty sessionScope.authUser and sessionScope.authUser.admin}"/>
<c:set var="isCustomer" value="${not empty sessionScope.authUser and not sessionScope.authUser.admin}"/>
<c:set var="uri" value="${requestScope['jakarta.servlet.forward.request_uri']}"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${empty pageTitle ? 'DudukDulu' : pageTitle}"/> | DudukDulu</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${ctx}/assets/css/app.css" rel="stylesheet">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Lora:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg app-navbar">
  <div class="container">

    <c:if test="${isAdmin}">
      <button class="navbar-toggler d-lg-none me-2" type="button" data-bs-toggle="offcanvas"
              data-bs-target="#adminSidebar" aria-controls="adminSidebar" aria-label="Menu">
        <span class="navbar-toggler-icon"></span>
      </button>
    </c:if>

    <a class="navbar-brand me-auto me-lg-3" href="${ctx}/home">DudukDulu</a>

    <c:choose>
      <c:when test="${isAdmin}">
        <div class="d-flex align-items-center gap-2">
          <span class="navbar-text text-light d-none d-sm-inline"><c:out value="${sessionScope.authUser.name}"/></span>
          <form method="post" action="${ctx}/logout" class="m-0">
            <button type="submit" class="btn btn-outline-light btn-sm">Logout</button>
          </form>
        </div>
      </c:when>

      <c:otherwise>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Menu">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
          <ul class="navbar-nav me-auto">
            <c:if test="${isCustomer}">
              <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/catalog') ? 'active' : ''}" href="${ctx}/catalog">Catalog</a></li>
              <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/cart') ? 'active' : ''}" href="${ctx}/cart">Cart</a></li>
              <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/orders') ? 'active' : ''}" href="${ctx}/orders">My Orders</a></li>
              <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/reviews') ? 'active' : ''}" href="${ctx}/reviews">My Reviews</a></li>
            </c:if>
          </ul>
          <div class="d-flex align-items-center gap-2">
            <c:choose>
              <c:when test="${not empty sessionScope.authUser}">
                <span class="navbar-text text-light"><c:out value="${sessionScope.authUser.name}"/></span>
                <form method="post" action="${ctx}/logout" class="m-0">
                  <button type="submit" class="btn btn-outline-light btn-sm">Logout</button>
                </form>
              </c:when>
              <c:otherwise>
                <a class="btn btn-outline-light btn-sm" href="${ctx}/login">Login</a>
                <a class="btn btn-light btn-sm" href="${ctx}/register">Register</a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</nav>

<c:choose>
  <c:when test="${isAdmin}">
<div class="app-shell">
  <aside class="app-sidebar">
    <div class="offcanvas-lg offcanvas-start" tabindex="-1" id="adminSidebar" aria-labelledby="adminSidebarLabel">
      <div class="offcanvas-header d-lg-none">
        <h5 class="offcanvas-title" id="adminSidebarLabel">Admin Menu</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas" data-bs-target="#adminSidebar" aria-label="Close"></button>
      </div>
      <div class="offcanvas-body p-0">
        <nav class="nav flex-column w-100 py-lg-3">
          <a class="nav-link ${fn:contains(uri, '/admin/dashboard') ? 'active' : ''}" href="${ctx}/admin/dashboard">Dashboard</a>
          <a class="nav-link ${fn:contains(uri, '/admin/categories') ? 'active' : ''}" href="${ctx}/admin/categories">Category</a>
          <a class="nav-link ${fn:contains(uri, '/admin/menus') ? 'active' : ''}" href="${ctx}/admin/menus">Menu</a>
          <a class="nav-link ${fn:contains(uri, '/admin/tables') ? 'active' : ''}" href="${ctx}/admin/tables">Table</a>
          <a class="nav-link ${fn:contains(uri, '/admin/payment-methods') ? 'active' : ''}" href="${ctx}/admin/payment-methods">Payment</a>
          <a class="nav-link ${fn:contains(uri, '/admin/orders') ? 'active' : ''}" href="${ctx}/admin/orders">Order</a>
          <a class="nav-link ${fn:contains(uri, '/admin/users') ? 'active' : ''}" href="${ctx}/admin/users">User</a>
          <a class="nav-link ${fn:contains(uri, '/admin/reviews') ? 'active' : ''}" href="${ctx}/admin/reviews">Review</a>
        </nav>
      </div>
    </div>
  </aside>
  <div class="app-main d-flex flex-column">
  <main class="flex-grow-1 px-3 px-lg-4 py-4">
  </c:when>
  <c:otherwise>
<main class="container py-4">
  </c:otherwise>
</c:choose>
<c:if test="${not empty flashSuccess}">
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    <c:out value="${flashSuccess}"/>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
</c:if>
<c:if test="${not empty flashError}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <c:out value="${flashError}"/>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
</c:if>