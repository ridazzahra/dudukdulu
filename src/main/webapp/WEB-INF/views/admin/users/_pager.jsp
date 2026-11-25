<c:if test="${result.totalPages > 1}">
  <nav aria-label="Page navigation" class="mt-3">
    <ul class="pagination pagination-sm justify-content-center flex-wrap mb-0">
      <li class="page-item ${result.page <= 1 ? 'disabled' : ''}">
        <a class="page-link" href="${ctx}<c:out value="${pagerUrl}"/>${result.page - 1}" aria-label="Previous">&laquo;</a>
      </li>
      <c:forEach var="p" begin="${result.page - 2 < 1 ? 1 : result.page - 2}"
                 end="${result.page + 2 > result.totalPages ? result.totalPages : result.page + 2}">
        <li class="page-item ${p == result.page ? 'active' : ''}">
          <a class="page-link" href="${ctx}<c:out value="${pagerUrl}"/>${p}">${p}</a>
        </li>
      </c:forEach>
      <li class="page-item ${result.page >= result.totalPages ? 'disabled' : ''}">
        <a class="page-link" href="${ctx}<c:out value="${pagerUrl}"/>${result.page + 1}" aria-label="Next">&raquo;</a>
      </li>
    </ul>
  </nav>
</c:if>