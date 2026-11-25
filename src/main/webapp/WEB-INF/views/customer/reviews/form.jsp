<c:set var="isEdit" value="${review.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Review' : 'Write a Review'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<h1 class="h4 mb-3">
  ${isEdit ? 'Edit Review' : 'Write a Review'}
  <span class="text-muted fs-6">for Order #${review.orderId}</span>
</h1>
<div class="card shadow-sm" style="max-width:560px">
  <div class="card-body">
    <form method="post" action="${ctx}/reviews/${isEdit ? 'edit' : 'create'}" novalidate>
      <c:choose>
        <c:when test="${isEdit}"><input type="hidden" name="id" value="${review.id}"></c:when>
        <c:otherwise><input type="hidden" name="orderId" value="${review.orderId}"></c:otherwise>
      </c:choose>

      <div class="mb-3">
        <label for="rating" class="form-label">Rating</label>
        <select id="rating" name="rating" class="form-select ${not empty errors.rating ? 'is-invalid' : ''}" required>
          <option value="">Choose a rating...</option>
          <option value="5" ${review.rating == 5 ? 'selected' : ''}>&#9733;&#9733;&#9733;&#9733;&#9733; &ndash; Excellent</option>
          <option value="4" ${review.rating == 4 ? 'selected' : ''}>&#9733;&#9733;&#9733;&#9733; &ndash; Good</option>
          <option value="3" ${review.rating == 3 ? 'selected' : ''}>&#9733;&#9733;&#9733; &ndash; Okay</option>
          <option value="2" ${review.rating == 2 ? 'selected' : ''}>&#9733;&#9733; &ndash; Poor</option>
          <option value="1" ${review.rating == 1 ? 'selected' : ''}>&#9733; &ndash; Terrible</option>
        </select>
        <div class="invalid-feedback">${errors.rating}</div>
      </div>

      <div class="mb-3">
        <label for="comment" class="form-label">Comment</label>
        <textarea id="comment" name="comment" rows="4" maxlength="255"
                  class="form-control ${not empty errors.comment ? 'is-invalid' : ''}"
                  required><c:out value="${review.comment}"/></textarea>
        <div class="invalid-feedback">${errors.comment}</div>
        <div class="form-text">Maximum 255 characters.</div>
      </div>

      <button type="submit" class="btn btn-primary">Save</button>
      <a href="${ctx}/orders/detail?id=${review.orderId}" class="btn btn-outline-secondary btn-cancel">Cancel</a>
    </form>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>