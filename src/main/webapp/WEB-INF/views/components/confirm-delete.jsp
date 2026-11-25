<%-- Delete confirmation dialog (shared by all modules). Usage on a button:
     <button type="button" class="btn btn-sm btn-outline-danger"
             data-delete-url="${ctx}/admin/menus/delete" data-delete-id="${m.id}"
             data-delete-name="${fn:escapeXml(m.name)}">Delete</button>
     The form is submitted via POST to data-delete-url with the parameter id. --%>
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form method="post" id="confirmDeleteForm" class="modal-content">
      <input type="hidden" name="id" id="confirmDeleteId">
      <div class="modal-header">
        <h5 class="modal-title" id="confirmDeleteTitle">Confirm deletion</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        Are you sure you want to delete <strong id="confirmDeleteName"></strong>? This action cannot be undone.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="submit" class="btn btn-danger">Yes, delete</button>
      </div>
    </form>
  </div>
</div>
<script>
  document.addEventListener('click', function (e) {
    var btn = e.target.closest('[data-delete-url]');
    if (!btn) return;
    document.getElementById('confirmDeleteForm').action = btn.getAttribute('data-delete-url');
    document.getElementById('confirmDeleteId').value = btn.getAttribute('data-delete-id');
    document.getElementById('confirmDeleteName').textContent = btn.getAttribute('data-delete-name') || '';
    new bootstrap.Modal(document.getElementById('confirmDeleteModal')).show();
  });
</script>