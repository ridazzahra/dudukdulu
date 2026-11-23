<%-- Dialog konfirmasi hapus (dipakai semua modul). Cara pakai di tombol:
     <button type="button" class="btn btn-sm btn-outline-danger"
             data-delete-url="${ctx}/admin/menus/delete" data-delete-id="${m.id}"
             data-delete-name="${fn:escapeXml(m.name)}">Hapus</button>
     Form dikirim POST ke data-delete-url dengan parameter id. --%>
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form method="post" id="confirmDeleteForm" class="modal-content">
      <input type="hidden" name="id" id="confirmDeleteId">
      <div class="modal-header">
        <h5 class="modal-title" id="confirmDeleteTitle">Konfirmasi hapus</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        Yakin ingin menghapus <strong id="confirmDeleteName"></strong>? Tindakan ini tidak bisa dibatalkan.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-danger">Ya, hapus</button>
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
