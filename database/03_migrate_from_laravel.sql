-- =====================================================================
-- 03_migrate_from_laravel.sql  |  OPSIONAL
-- Dipakai HANYA jika ingin mengubah database Laravel yang SUDAH ADA
-- (dan mempertahankan datanya), bukan membuat database baru dari 01 + 02.
--
-- Asumsi skema lama (sesuai CDM lama): users, categories, menus, carts,
-- orders (dengan kolom table_number), order_items, reviews.
-- CEK DULU dengan DESCRIBE tiap tabel. BACKUP dulu: mysqldump food_order > backup.sql
-- Jalankan SEKALI saja. Ganti nama database jika berbeda.
-- =====================================================================

USE food_order;

START TRANSACTION;

-- 1. Tabel baru -------------------------------------------------------
CREATE TABLE dining_tables (
  id        INT         NOT NULL AUTO_INCREMENT,
  number    INT         NOT NULL,
  capacity  INT         NOT NULL DEFAULT 2,
  status    VARCHAR(10) NOT NULL DEFAULT 'available',
  PRIMARY KEY (id),
  UNIQUE KEY uq_dining_tables_number (number),
  CONSTRAINT chk_tables_capacity CHECK (capacity > 0),
  CONSTRAINT chk_tables_status CHECK (status IN ('available', 'occupied', 'reserved'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payment_methods (
  id         INT          NOT NULL AUTO_INCREMENT,
  name       VARCHAR(50)  NOT NULL,
  type       VARCHAR(20)  NOT NULL,
  note       VARCHAR(255) NOT NULL DEFAULT '',
  is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  UNIQUE KEY uq_payment_methods_name (name),
  CONSTRAINT chk_pm_type CHECK (type IN ('cash', 'qris', 'transfer', 'ewallet'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Isi awal: 20 meja (pengganti hardcode) dan metode pembayaran -------
INSERT INTO dining_tables (number, capacity, status) VALUES
(1,2,'available'),(2,2,'available'),(3,2,'available'),(4,2,'available'),
(5,2,'available'),(6,2,'available'),(7,2,'available'),(8,2,'available'),
(9,4,'available'),(10,4,'available'),(11,4,'available'),(12,4,'available'),
(13,4,'available'),(14,4,'available'),(15,4,'available'),(16,4,'available'),
(17,6,'available'),(18,6,'available'),(19,6,'available'),(20,6,'available');

INSERT INTO payment_methods (name, type, note, is_active) VALUES
('Tunai',               'cash',     'Bayar langsung di kasir setelah makan.',            1),
('QRIS',                'qris',     'Scan QR di kasir, lalu tunjukkan bukti pembayaran.', 1),
('Transfer Bank BCA',   'transfer', 'No. rekening 1234567890 a.n. Resto Contoh.',         1),
('E-Wallet (OVO/GoPay)','ewallet',  'Kirim ke 081200000001 a.n. Resto Contoh.',           1);

-- 3. users: tambah is_active ---------------------------------------------
ALTER TABLE users
  ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE,
  ADD UNIQUE KEY uq_users_email (email);

-- Hash Laravel berawalan $2y$; jBCrypt hanya mengenali $2a$.
-- Algoritmanya sama, jadi cukup ganti prefiks (aman untuk password ASCII),
-- akun lama tetap bisa login dengan password lamanya.
UPDATE users SET password = CONCAT('$2a$', SUBSTRING(password, 5))
WHERE password LIKE '$2y$%';

-- 4. orders: table_number -> table_id, total_price -> DECIMAL ---------------
ALTER TABLE orders ADD COLUMN table_id INT NULL AFTER user_id;

-- Meja yang belum ada di dining_tables (nomor di luar 1-20) dibuat otomatis
INSERT INTO dining_tables (number, capacity, status)
SELECT DISTINCT o.table_number, 2, 'available'
FROM orders o
LEFT JOIN dining_tables t ON t.number = o.table_number
WHERE t.id IS NULL AND o.table_number IS NOT NULL;

UPDATE orders o
JOIN dining_tables t ON t.number = o.table_number
SET o.table_id = t.id;

-- Order tanpa nomor meja dipetakan ke meja 1 agar kolom bisa NOT NULL
UPDATE orders SET table_id = (SELECT id FROM dining_tables WHERE number = 1)
WHERE table_id IS NULL;

ALTER TABLE orders
  MODIFY COLUMN table_id INT NOT NULL,
  MODIFY COLUMN total_price DECIMAL(15,2) NOT NULL,
  ADD CONSTRAINT fk_orders_table FOREIGN KEY (table_id)
    REFERENCES dining_tables (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  DROP COLUMN table_number;

-- Seragamkan status: 'preparing' (dashboard Laravel) -> 'processing'
UPDATE orders SET status = 'processing' WHERE status = 'preparing';

ALTER TABLE orders
  ADD CONSTRAINT chk_orders_status
  CHECK (status IN ('pending', 'processing', 'ready', 'completed', 'cancelled'));

-- 5. payments: dibuat untuk setiap order lama (metode default: Tunai) -------
CREATE TABLE payments (
  id                 INT           NOT NULL AUTO_INCREMENT,
  order_id           INT           NOT NULL,
  payment_method_id  INT           NOT NULL,
  amount             DECIMAL(15,2) NOT NULL,
  status             VARCHAR(10)   NOT NULL DEFAULT 'pending',
  paid_at            DATETIME      NULL DEFAULT NULL,
  proof              VARCHAR(500)  NULL DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_payments_order (order_id),
  KEY idx_payments_method (payment_method_id),
  KEY idx_payments_status (status),
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id)
    REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_payments_method FOREIGN KEY (payment_method_id)
    REFERENCES payment_methods (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_payments_amount CHECK (amount >= 0),
  CONSTRAINT chk_payments_status CHECK (status IN ('pending', 'paid', 'failed'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO payments (order_id, payment_method_id, amount, status, paid_at)
SELECT o.id,
       (SELECT id FROM payment_methods WHERE type = 'cash' LIMIT 1),
       o.total_price,
       CASE o.status WHEN 'completed' THEN 'paid'
                     WHEN 'cancelled' THEN 'failed'
                     ELSE 'pending' END,
       CASE o.status WHEN 'completed' THEN o.completed_at ELSE NULL END
FROM orders o;

-- 6. reviews: maksimal satu review per order ---------------------------------
-- Jika ada order dengan lebih dari satu review, baris berikut gagal:
-- hapus duplikatnya dulu, lalu jalankan ulang bagian ini.
ALTER TABLE reviews ADD UNIQUE KEY uq_reviews_order (order_id);

-- 7. carts: satu baris per user per menu -------------------------------------
ALTER TABLE carts ADD UNIQUE KEY uq_carts_user_menu (user_id, menu_id);

-- 8. Tabel bawaan Laravel yang tidak dipakai (hapus bila ada) --------------------
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS cache;
DROP TABLE IF EXISTS cache_locks;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS job_batches;
DROP TABLE IF EXISTS failed_jobs;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS migrations;

COMMIT;
