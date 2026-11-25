<!DOCTYPE html>
<html lang="id">
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
<body class="bg-light">
<nav class="navbar navbar-expand-lg app-navbar">
  <div class="container">
    <a class="navbar-brand" href="${ctx}/home">DudukDulu</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
            aria-controls="mainNav" aria-expanded="false" aria-label="Menu">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="mainNav">
      <ul class="navbar-nav me-auto">
        <c:if test="${not empty sessionScope.authUser and sessionScope.authUser.admin}">
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/dashboard">Dashboard</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/categories">Category</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/menus">Menu</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/tables">Table</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/payment-methods">Payment</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/orders">Order</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/users">User</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/reviews">Review</a></li>
        </c:if>
        <c:if test="${not empty sessionScope.authUser and not sessionScope.authUser.admin}">
          <li class="nav-item"><a class="nav-link" href="${ctx}/catalog">Catalog</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/cart">Cart</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/orders">Order</a></li>
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
  </div>
</nav>
<main class="container py-4">
<c:if test="${not empty flashSuccess}">
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    <c:out value="${flashSuccess}"/>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Tutup"></button>
  </div>
</c:if>
<c:if test="${not empty flashError}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <c:out value="${flashError}"/>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Tutup"></button>
  </div>
</c:if>
