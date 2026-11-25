</main>
<c:if test="${not empty sessionScope.authUser and sessionScope.authUser.admin}">
</div>
</c:if>
<footer class="app-footer text-center small py-4">DudukDulu &copy; 2026 &mdash; EF234301 Web Programming</footer>
<jsp:include page="/WEB-INF/views/components/confirm-delete.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  document.querySelectorAll('table.table-stack').forEach(function (table) {
    var heads = Array.prototype.map.call(table.querySelectorAll('thead th'), function (th) {
      return th.textContent.trim();
    });
    table.querySelectorAll('tbody tr').forEach(function (tr) {
      Array.prototype.forEach.call(tr.children, function (td, i) {
        if (td.tagName !== 'TD' || td.colSpan > 1 || !heads[i]) return;
        if (/^actions?$/i.test(heads[i])) { td.classList.add('td-actions'); return; }
        if (i > 0) td.setAttribute('data-label', heads[i]);
      });
    });
  });
</script>
</body>
</html>