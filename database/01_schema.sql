-- =====================================================================
-- 01_schema.sql  |  Food Order (JSP)  |  Skema final, 10 tabel
-- Jalankan di database KOSONG / baru. Script ini menghapus tabel lama.
-- Ganti nama database di bawah jika berbeda dengan konfigurasi tim.
-- Kompatibel: MySQL 8.0.16+ dan MariaDB 10.2+ (CHECK constraint aktif).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS food_order
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_order;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS payment_methods;
DROP TABLE IF EXISTS dining_tables;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- users
-- role: 'admin' | 'customer'. password: hash BCrypt ($2a$...).
-- is_active = 0 -> akun dinonaktifkan (tidak bisa login).
-- ---------------------------------------------------------------------
CREATE TABLE users (
  id         INT          NOT NULL AUTO_INCREMENT,
  name       VARCHAR(255) NOT NULL,
  email      VARCHAR(255) NOT NULL,
  password   VARCHAR(255) NOT NULL,
  role       VARCHAR(9)   NOT NULL DEFAULT 'customer',
  phone      VARCHAR(15)  NOT NULL,
  is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email),
  CONSTRAINT chk_users_role CHECK (role IN ('admin', 'customer'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- categories
-- ---------------------------------------------------------------------
CREATE TABLE categories (
  id    INT          NOT NULL AUTO_INCREMENT,
  name  VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- menus
-- Kategori tidak boleh dihapus jika masih dipakai menu (ON DELETE RESTRICT).
-- ---------------------------------------------------------------------
CREATE TABLE menus (
  id            INT           NOT NULL AUTO_INCREMENT,
  category_id   INT           NOT NULL,
  name          VARCHAR(255)  NOT NULL,
  description   VARCHAR(255)  NOT NULL,
  price         DECIMAL(15,2) NOT NULL,
  image_link    VARCHAR(500)  NOT NULL DEFAULT 'uploads/default.png',
  is_available  BOOLEAN       NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  KEY idx_menus_category (category_id),
  KEY idx_menus_name (name),
  CONSTRAINT fk_menus_category FOREIGN KEY (category_id)
    REFERENCES categories (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_menus_price CHECK (price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- dining_tables  (BARU) - menggantikan hardcode 20 meja di checkout
-- status: 'available' | 'occupied' | 'reserved'
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- payment_methods  (BARU)
-- type: 'cash' | 'qris' | 'transfer' | 'ewallet'
-- note: instruksi pembayaran / nomor rekening / nomor e-wallet
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- carts  (satu baris per user per menu)
-- ---------------------------------------------------------------------
CREATE TABLE carts (
  id        INT NOT NULL AUTO_INCREMENT,
  user_id   INT NOT NULL,
  menu_id   INT NOT NULL,
  quantity  INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uq_carts_user_menu (user_id, menu_id),
  KEY idx_carts_menu (menu_id),
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_carts_menu FOREIGN KEY (menu_id)
    REFERENCES menus (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT chk_carts_qty CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- orders
-- status: 'pending' | 'processing' | 'ready' | 'completed' | 'cancelled'
-- table_id menggantikan kolom lama table_number.
-- User yang sudah punya order tidak bisa dihapus (RESTRICT) -> nonaktifkan saja.
-- ---------------------------------------------------------------------
CREATE TABLE orders (
  id            INT           NOT NULL AUTO_INCREMENT,
  user_id       INT           NOT NULL,
  table_id      INT           NOT NULL,
  total_price   DECIMAL(15,2) NOT NULL,
  status        VARCHAR(50)   NOT NULL DEFAULT 'pending',
  note          VARCHAR(255)  NOT NULL DEFAULT '',
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  completed_at  TIMESTAMP     NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_orders_user (user_id),
  KEY idx_orders_table (table_id),
  KEY idx_orders_status (status),
  KEY idx_orders_created (created_at),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_orders_table FOREIGN KEY (table_id)
    REFERENCES dining_tables (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_orders_total CHECK (total_price >= 0),
  CONSTRAINT chk_orders_status CHECK (status IN ('pending', 'processing', 'ready', 'completed', 'cancelled'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- order_items  (price = snapshot harga saat checkout)
-- Menu yang sudah ada di order tidak bisa dihapus (RESTRICT).
-- ---------------------------------------------------------------------
CREATE TABLE order_items (
  id        INT           NOT NULL AUTO_INCREMENT,
  order_id  INT           NOT NULL,
  menu_id   INT           NOT NULL,
  quantity  INT           NOT NULL,
  price     DECIMAL(15,2) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_order_items_order (order_id),
  KEY idx_order_items_menu (menu_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
    REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_order_items_menu FOREIGN KEY (menu_id)
    REFERENCES menus (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_order_items_qty CHECK (quantity > 0),
  CONSTRAINT chk_order_items_price CHECK (price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- payments  (BARU) - relasi 1:1 dengan orders (order_id UNIQUE)
-- status: 'pending' | 'paid' | 'failed'
-- Jika nanti diputuskan 1:N (bisa bayar ulang), hapus UNIQUE uq_payments_order.
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- reviews  (maksimal satu review per order -> order_id UNIQUE)
-- ---------------------------------------------------------------------
CREATE TABLE reviews (
  id          INT          NOT NULL AUTO_INCREMENT,
  user_id     INT          NOT NULL,
  order_id    INT          NOT NULL,
  rating      DECIMAL(2,1) NOT NULL,
  comment     VARCHAR(255) NOT NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_reviews_order (order_id),
  KEY idx_reviews_user (user_id),
  CONSTRAINT fk_reviews_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_reviews_order FOREIGN KEY (order_id)
    REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT chk_reviews_rating CHECK (rating >= 0.5 AND rating <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
