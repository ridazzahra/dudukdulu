# DudukDulu — aplikasi pemesanan makanan kafe (JSP) 

Aplikasi pemesanan makanan untuk kafe. Lanjutan proyek midterm (Laravel) yang ditulis ulang dengan
**Jakarta Servlet + JSP + JSTL**, MySQL (JDBC + DAO), BCrypt, dan Servlet Filter.
Repo: https://github.com/ridazzahra/dudukdulu · 

## Teknologi
Java 17 · Jakarta Servlet 6 / JSP · JSTL 2.0 · Apache Tomcat **10.1** · Maven · MySQL 8 · jBCrypt · Bootstrap 5 (CDN)

> Tomcat 10+ memakai namespace `jakarta.*`. Jangan memakai Tomcat 9 (itu `javax.*`).

## Cara menjalankan

### A. Docker (paling mudah, versi sama di semua laptop)
```bash
docker compose up --build
# buka http://localhost:8080
```
Database `food_order` otomatis dibuat dari `database/01_schema.sql` + `database/02_seed.sql`.

### B. Manual
1. Buat database: `mysql -u root -p < database/01_schema.sql` lalu `mysql -u root -p < database/02_seed.sql`
2. Salin `src/main/resources/db.properties.example` menjadi `db.properties`, sesuaikan user/password MySQL.
3. `mvn clean package` → hasilnya `target/foodorder.war`.
4. Deploy war ke Tomcat 10.1 (IDE: Eclipse/IntelliJ/VS Code + Tomcat, atau salin ke `webapps/`).
   Untuk context path `/`, namai file `ROOT.war`.

### Konfigurasi (environment variable mengalahkan db.properties)
| Variabel | Fungsi | Default |
|---|---|---|
| `DB_URL` | URL JDBC | `jdbc:mysql://localhost:3306/food_order?...` |
| `DB_USER` / `DB_PASSWORD` | akun MySQL | `root` / kosong |
| `UPLOAD_DIR` | folder gambar upload (di luar webapp) | `./uploads` |

## Akun demo
| Role | Email | Password |
|---|---|---|
| Admin | admin@dudukdulu.test | admin123 |
| Customer | customer@dudukdulu.test | customer123 |
| Nonaktif (uji fitur nonaktifkan) | penyusup@dudukdulu.test | customer123 |

## Struktur
```
database/           01_schema.sql, 02_seed.sql, 03_migrate_from_laravel.sql (opsional)
src/main/java/com/foodorder/
  model/            entitas (User, Category, Menu, DiningTable, PaymentMethod, ...)
  dao/              akses database, PreparedStatement saja
  controller/       Servlet per modul (BaseServlet = helper bersama)
  filter/           EncodingFilter, FlashFilter, AuthFilter, AdminFilter  (urutan di web.xml)
  util/             DbUtil, PasswordUtil, Auth, Flash, Validator, Page, ImageUploader, Sql
src/main/webapp/
  WEB-INF/web.xml  WEB-INF/views/{layout,components,auth,error,admin,customer}
  assets/           css, js, img (default.png)
```

## KONTRAK TIM (untuk Anggota B dan C)
**Hanya Anggota A yang mengubah** `web.xml`, `pom.xml`, `util/*`, `filter/*`. Perlu perubahan? Minta lewat chat/PR kecil.

- **URL admin**: `/admin/...` (otomatis wajib admin lewat `AdminFilter`). Halaman customer cukup login (`AuthFilter` menjaga
  semua URL kecuali `/`, `/home`, `/login`, `/register`, `/logout`, `/assets/*`, `/uploads/*`). Servlet baru **tidak perlu** konfigurasi filter.
- **User login**: Java `Auth.currentUser(req)`, `Auth.userId(req)`, `Auth.isAdmin(req)` · JSP `${sessionScope.authUser.name}`,
  `.role`, `.admin`. Objek di session tidak berisi hash password.
- **Flash message**: `Flash.success(req, "...")` / `Flash.error(req, "...")` lalu `redirect`. Tanpa redirect (forward): `Flash.successNow/errorNow`.
  JSP cukup `${flashSuccess}` / `${flashError}` (sudah ditampilkan `layout/header.jsp`).
- **Layout JSP**: awali halaman dengan `<c:set var="pageTitle" value="Judul" scope="request"/>` lalu
  `<jsp:include page="/WEB-INF/views/layout/header.jsp"/>` ... `<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>`.
  Taglib `c`, `fmt`, `fn` dan variabel `${ctx}` (context path) otomatis tersedia di semua JSP. **Anggota C** boleh menimpa
  `header.jsp`, `footer.jsp`, `assets/css/app.css` (saat ini versi sementara dari A) selama kontrak di atas tetap.
- **Komponen**: `components/pagination.jsp` (butuh atribut `pager` dan `baseQuery`), `components/confirm-delete.jsp`
  (tombol dengan `data-delete-url`, `data-delete-id`, `data-delete-name`; sudah otomatis ada di footer).
- **Servlet baru**: `extends BaseServlet` (`forward`, `redirect`, `action`, `intParam`, `baseQuery`), validasi dengan `Validator`,
  hasil berhalaman dengan `Page<T>`. Pola: GET `""` daftar, `/create`, `/edit?id=`, POST `/create`, `/edit`, `/delete`.
- **DAO milik A yang boleh dipakai B/C**:
  - `MenuDao.findAll(q, categoryId, availability, sort, page, size)` (katalog: kirim `Boolean.TRUE`), `findById`
  - `CategoryDao.findAll()` untuk dropdown
  - `DiningTableDao.findAvailable()`, `findById`, `updateStatus(id, status)` (isi `occupied` saat order dibuat, `available` saat selesai/batal)
  - `PaymentMethodDao.findAllActive()`, `findById`
  - `UserDao.findById/findByEmail` (C menambah findAll/update/setActive/delete)
- **Transaksi** (B): `Connection c = DbUtil.getConnection(); c.setAutoCommit(false); ... c.commit(); / c.rollback();`
- **Gambar menu**: kolom `image_link` berisi `uploads/namafile.ext`; tampilkan `<img src="${ctx}/${menu.imageLink}">`.
- **Setelah login**: customer ke `/catalog`, admin ke `/admin/menus` (ganti ke `/admin/dashboard` di `Auth.homeFor` bila dashboard C sudah jadi).
- Status order & konstanta: `pending, processing, ready, completed, cancelled` (milik B).
- Jangan commit `db.properties`. Hapus yang bergantung pada FK memakai `try/catch SQLIntegrityConstraintViolationException` (lihat servlet milik A).

## Status modul Anggota A
Setup Maven/Tomcat · koneksi DB · register/login/logout · BCrypt · AuthFilter/AdminFilter ·
CRUD **Category, Menu (upload gambar, filter, cari, urut, toggle sold out), Meja, Metode Pembayaran** · SQL schema+seed · Docker.

## Catatan keamanan
PreparedStatement di semua query · password BCrypt · sesi diperbarui saat login (anti session fixation) · AuthFilter membaca ulang
user dari DB tiap request (role/nonaktif langsung berlaku) · output JSP di-escape (`c:out`, `fn:escapeXml`) · upload dicek
ekstensi, ukuran, dan signature file · tujuan redirect pasca-login divalidasi · belum ada token CSRF dan pembatasan percobaan login
(kandidat pengembangan lanjutan).