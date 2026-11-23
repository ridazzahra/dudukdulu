-- =====================================================================
-- 02_seed.sql  |  Data contoh Food Order (JSP)
-- Jalankan SETELAH 01_schema.sql.
--
-- AKUN DEMO (tulis di README):
--   admin     : admin@dudukdulu.test      / admin123
--   customer  : customer@dudukdulu.test   / customer123
--   nonaktif  : penyusup@dudukdulu.test   / customer123  (is_active = 0, untuk uji fitur nonaktifkan)
-- Hash di bawah berawalan $2a$ sehingga kompatibel dengan jBCrypt.
-- =====================================================================

USE food_order;

-- ---------------------------------------------------------------------
-- users
-- ---------------------------------------------------------------------
INSERT INTO users (id, name, email, password, role, phone, is_active) VALUES
(1, 'Admin DudukDulu',   'admin@dudukdulu.test',     '$2a$10$cahVK5KNu5TBjIS5/hveY.fQ7laRNOc.ZR8883jQYoGPBNGJch1mC', 'admin',    '081200000001', 1),
(2, 'Customer Demo',     'customer@dudukdulu.test',  '$2a$10$Xt.AXLKi2IzWd4g2Os3v5ud1kYlQd3n6ssszZSaL2Ys0wxjEjQQYe', 'customer', '081200000002', 1),
(3, 'Penyusup Nonaktif', 'penyusup@dudukdulu.test',  '$2a$10$Xt.AXLKi2IzWd4g2Os3v5ud1kYlQd3n6ssszZSaL2Ys0wxjEjQQYe', 'customer', '081200000004', 0);

-- ---------------------------------------------------------------------
-- categories
-- ---------------------------------------------------------------------
INSERT INTO categories (id, name) VALUES
(1, 'Makanan Utama'),
(2, 'Camilan'),
(3, 'Minuman'),
(4, 'Dessert');

-- ---------------------------------------------------------------------
-- menus  (image_link: ganti dengan file gambar asli di folder upload)
-- ---------------------------------------------------------------------
INSERT INTO menus (id, category_id, name, description, price, image_link, is_available) VALUES
(1,  1, 'Nasi Goreng Spesial', 'Nasi goreng dengan telur, ayam suwir, dan acar.',   25000.00, 'uploads/default.png', 1),
(2,  1, 'Mie Ayam Bakso',      'Mie ayam dengan bakso sapi dan pangsit goreng.',    22000.00, 'uploads/default.png', 1),
(3,  1, 'Ayam Bakar Madu',     'Ayam bakar bumbu madu dengan lalapan dan sambal.',  30000.00, 'uploads/default.png', 1),
(4,  1, 'Sate Ayam',           'Sepuluh tusuk sate ayam dengan bumbu kacang.',      28000.00, 'uploads/default.png', 1),
(5,  2, 'Kentang Goreng',      'Kentang goreng renyah dengan saus sambal.',         15000.00, 'uploads/default.png', 1),
(6,  2, 'Pisang Goreng',       'Pisang goreng tepung krispi dengan taburan keju.',  12000.00, 'uploads/default.png', 1),
(7,  2, 'Tahu Crispy',         'Tahu goreng tepung dengan cabai garam.',            13000.00, 'uploads/default.png', 1),
(8,  3, 'Es Teh Manis',        'Teh manis dingin segar.',                            6000.00, 'uploads/default.png', 1),
(9,  3, 'Es Jeruk',            'Jeruk peras asli dengan es batu.',                   8000.00, 'uploads/default.png', 1),
(10, 3, 'Kopi Susu',           'Kopi susu gula aren dingin.',                       15000.00, 'uploads/default.png', 1),
(11, 4, 'Es Krim Vanilla',     'Dua scoop es krim vanilla.',                        12000.00, 'uploads/default.png', 1),
(12, 4, 'Pudding Cokelat',     'Pudding cokelat lembut dengan vla (contoh sold out).', 10000.00, 'uploads/default.png', 0);

-- ---------------------------------------------------------------------
-- dining_tables: 20 meja (menggantikan hardcode 20 meja di Laravel)
-- Meja 1-8 kapasitas 2, 9-16 kapasitas 4, 17-20 kapasitas 6
-- Meja 1, 2, 7 'occupied' karena dipakai order aktif pada data contoh.
-- ---------------------------------------------------------------------
INSERT INTO dining_tables (id, number, capacity, status) VALUES
(1,  1,  2, 'occupied'),
(2,  2,  2, 'occupied'),
(3,  3,  2, 'available'),
(4,  4,  2, 'available'),
(5,  5,  2, 'available'),
(6,  6,  2, 'available'),
(7,  7,  2, 'occupied'),
(8,  8,  2, 'available'),
(9,  9,  4, 'available'),
(10, 10, 4, 'available'),
(11, 11, 4, 'available'),
(12, 12, 4, 'available'),
(13, 13, 4, 'available'),
(14, 14, 4, 'available'),
(15, 15, 4, 'reserved'),
(16, 16, 4, 'available'),
(17, 17, 6, 'available'),
(18, 18, 6, 'available'),
(19, 19, 6, 'available'),
(20, 20, 6, 'available');

-- ---------------------------------------------------------------------
-- payment_methods
-- ---------------------------------------------------------------------
INSERT INTO payment_methods (id, name, type, note, is_active) VALUES
(1, 'Tunai',            'cash',     'Bayar langsung di kasir setelah makan.',                 1),
(2, 'QRIS',             'qris',     'Scan QR di kasir, lalu tunjukkan bukti pembayaran.',      1),
(3, 'Transfer Bank BCA','transfer', 'No. rekening 1234567890 a.n. Resto Contoh.',              1),
(4, 'E-Wallet (OVO/GoPay)','ewallet','Kirim ke 081200000001 a.n. Resto Contoh.',               1),
(5, 'Kartu Kredit',     'transfer', 'Contoh metode nonaktif.',                                 0);

-- ---------------------------------------------------------------------
-- carts (keranjang aktif customer)
-- ---------------------------------------------------------------------
INSERT INTO carts (id, user_id, menu_id, quantity) VALUES
(1, 2, 8, 2),
(2, 2, 5, 1),
(3, 2, 10, 1);

-- ---------------------------------------------------------------------
-- orders: mencakup semua status (completed, ready, processing, pending, cancelled)
-- total_price = jumlah (quantity x price) dari order_items-nya
-- ---------------------------------------------------------------------
INSERT INTO orders (id, user_id, table_id, total_price, status, note, created_at, updated_at, completed_at) VALUES
(1, 2, 3, 62000.00, 'completed',  'Tidak pedas.',            '2026-11-20 12:15:00', '2026-11-20 12:50:00', '2026-11-20 12:50:00'),
(2, 2, 5, 60000.00, 'completed',  '',                        '2026-11-21 18:30:00', '2026-11-21 19:10:00', '2026-11-21 19:10:00'),
(3, 2, 1, 72000.00, 'processing', 'Sambal dipisah.',         '2026-11-23 09:40:00', '2026-11-23 09:45:00', NULL),
(4, 2, 7, 40000.00, 'pending',    '',                        '2026-11-23 10:05:00', '2026-11-23 10:05:00', NULL),
(5, 2, 4, 28000.00, 'cancelled',  'Dibatalkan oleh admin.',  '2026-11-22 13:00:00', '2026-11-22 13:20:00', NULL),
(6, 2, 2, 37000.00, 'ready',      'Es krim jangan dulu.',    '2026-11-23 10:20:00', '2026-11-23 10:35:00', NULL);

-- ---------------------------------------------------------------------
-- order_items (price = snapshot harga saat checkout)
-- ---------------------------------------------------------------------
INSERT INTO order_items (id, order_id, menu_id, quantity, price) VALUES
-- order 1: 2x25000 + 2x6000 = 62000
(1,  1, 1,  2, 25000.00),
(2,  1, 8,  2,  6000.00),
-- order 2: 30000 + 15000 + 15000 = 60000
(3,  2, 3,  1, 30000.00),
(4,  2, 10, 1, 15000.00),
(5,  2, 5,  1, 15000.00),
-- order 3: 2x28000 + 2x8000 = 72000
(6,  3, 4,  2, 28000.00),
(7,  3, 9,  2,  8000.00),
-- order 4: 22000 + 6000 + 12000 = 40000
(8,  4, 2,  1, 22000.00),
(9,  4, 8,  1,  6000.00),
(10, 4, 6,  1, 12000.00),
-- order 5: 13000 + 15000 = 28000
(11, 5, 7,  1, 13000.00),
(12, 5, 10, 1, 15000.00),
-- order 6: 25000 + 12000 = 37000
(13, 6, 1,  1, 25000.00),
(14, 6, 11, 1, 12000.00);

-- ---------------------------------------------------------------------
-- payments (satu per order; amount = total_price order)
-- ---------------------------------------------------------------------
INSERT INTO payments (id, order_id, payment_method_id, amount, status, paid_at, proof) VALUES
(1, 1, 2, 62000.00, 'paid',    '2026-11-20 12:20:00', NULL),
(2, 2, 3, 60000.00, 'paid',    '2026-11-21 18:45:00', 'uploads/proof-contoh.png'),
(3, 3, 4, 72000.00, 'paid',    '2026-11-23 09:44:00', NULL),
(4, 4, 1, 40000.00, 'pending', NULL,                  NULL),
(5, 5, 3, 28000.00, 'failed',  NULL,                  NULL),
(6, 6, 1, 37000.00, 'paid',    '2026-11-23 10:30:00', NULL);

-- ---------------------------------------------------------------------
-- reviews (hanya untuk order completed)
-- ---------------------------------------------------------------------
INSERT INTO reviews (id, user_id, order_id, rating, comment, created_at) VALUES
(1, 2, 1, 5.0, 'Nasi gorengnya enak, pelayanan cepat.',         '2026-11-20 13:10:00'),
(2, 2, 2, 4.0, 'Ayam bakarnya enak, tapi agak lama datangnya.', '2026-11-21 19:30:00');