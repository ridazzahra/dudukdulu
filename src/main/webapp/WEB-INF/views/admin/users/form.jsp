<c:set var="pageTitle" value="${isEdit ? 'Edit User' : 'Add User'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-12 col-md-8 col-lg-6">
    <h1 class="h4 mb-3">${isEdit ? 'Edit User' : 'Add User'}</h1>

    <form method="post" action="${ctx}/admin/users/${isEdit ? 'edit' : 'create'}" novalidate>
      <c:if test="${isEdit}"><input type="hidden" name="id" value="${u.id}"></c:if>

      <div class="mb-3">
        <label for="name" class="form-label">Name</label>
        <input type="text" id="name" name="name" maxlength="100" required
               class="form-control ${not empty errors.name ? 'is-invalid' : ''}"
               value="<c:out value='${u.name}'/>">
        <div class="invalid-feedback"><c:out value="${errors.name}"/></div>
      </div>

      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" id="email" name="email" maxlength="100" required
               class="form-control ${not empty errors.email ? 'is-invalid' : ''}"
               value="<c:out value='${u.email}'/>">
        <div class="invalid-feedback"><c:out value="${errors.email}"/></div>
      </div>

      <div class="mb-3">
        <label for="phone" class="form-label">Phone <span class="text-muted">(optional)</span></label>
        <input type="text" id="phone" name="phone" maxlength="20"
               class="form-control ${not empty errors.phone ? 'is-invalid' : ''}"
               value="<c:out value='${u.phone}'/>">
        <div class="invalid-feedback"><c:out value="${errors.phone}"/></div>
      </div>

      <div class="mb-3">
        <label for="role" class="form-label">Role</label>
        <select id="role" name="role" class="form-select ${not empty errors.role ? 'is-invalid' : ''}"
                ${isEdit && u.id == meId ? 'disabled' : ''}>
          <option value="customer" ${u.role == 'customer' ? 'selected' : ''}>Customer</option>
          <option value="admin" ${u.role == 'admin' ? 'selected' : ''}>Admin</option>
        </select>
        <%-- a disabled select is not submitted, so send the role through a hidden field --%>
        <c:if test="${isEdit && u.id == meId}"><input type="hidden" name="role" value="${u.role}"></c:if>
        <div class="invalid-feedback"><c:out value="${errors.role}"/></div>
        <c:if test="${isEdit && u.id == meId}"><div class="form-text">Your own role cannot be changed.</div></c:if>
      </div>

      <div class="mb-3">
        <label for="password" class="form-label">${isEdit ? 'New password' : 'Password'}</label>
        <input type="password" id="password" name="password" autocomplete="new-password"
               class="form-control ${not empty errors.password ? 'is-invalid' : ''}">
        <div class="invalid-feedback"><c:out value="${errors.password}"/></div>
        <c:if test="${isEdit}"><div class="form-text">Leave blank to keep the current password.</div></c:if>
      </div>

      <div class="mb-4">
        <label for="password2" class="form-label">Confirm password</label>
        <input type="password" id="password2" name="password2" autocomplete="new-password"
               class="form-control ${not empty errors.password2 ? 'is-invalid' : ''}">
        <div class="invalid-feedback"><c:out value="${errors.password2}"/></div>
      </div>

      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Save</button>
        <a href="${ctx}/admin/users" class="btn btn-outline-secondary">Cancel</a>
      </div>
    </form>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>