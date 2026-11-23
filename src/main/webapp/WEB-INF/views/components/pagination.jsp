<%-- Paginasi bersama. Butuh atribut request: pager (util.Page) dan baseQuery (query string tanpa "page"). --%>
<c:if test="${pager.totalPages > 1}">
  <c:set var="qs" value="${empty baseQuery ? '' : baseQuery}${empty baseQuery ? '' : '&'}"/>
  <nav aria-label="Halaman">
    <ul class="pagination justify-content-center mb-0">
      <li class="page-item ${pager.hasPrev ? '' : 'disabled'}">
        <a class="page-link" href="?${qs}page=${pager.page - 1}">&laquo;</a>
      </li>
      <c:forEach begin="${pager.page - 2 < 1 ? 1 : pager.page - 2}"
                 end="${pager.page + 2 > pager.totalPages ? pager.totalPages : pager.page + 2}" var="i">
        <li class="page-item ${i == pager.page ? 'active' : ''}">
          <a class="page-link" href="?${qs}page=${i}">${i}</a>
        </li>
      </c:forEach>
      <li class="page-item ${pager.hasNext ? '' : 'disabled'}">
        <a class="page-link" href="?${qs}page=${pager.page + 1}">&raquo;</a>
      </li>
    </ul>
  </nav>
  <p class="text-center text-muted small mt-2">Total ${pager.totalItems} data</p>
</c:if>
