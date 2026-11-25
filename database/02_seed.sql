USE food_order;

INSERT INTO users (id, name, email, password, role, phone, is_active) VALUES
(1, 'Admin DudukDulu',   'admin@dudukdulu.test',     '$2a$10$cahVK5KNu5TBjIS5/hveY.fQ7laRNOc.ZR8883jQYoGPBNGJch1mC', 'admin',    '081200000001', 1),
(2, 'Customer Demo',     'customer@dudukdulu.test',  '$2a$10$Xt.AXLKi2IzWd4g2Os3v5ud1kYlQd3n6ssszZSaL2Ys0wxjEjQQYe', 'customer', '081200000002', 1),
(3, 'Inactive Intruder', 'penyusup@dudukdulu.test',  '$2a$10$Xt.AXLKi2IzWd4g2Os3v5ud1kYlQd3n6ssszZSaL2Ys0wxjEjQQYe', 'customer', '081200000004', 0);

INSERT INTO categories (id, name) VALUES
(1, 'Main Course'),
(2, 'Snacks'),
(3, 'Drinks'),
(4, 'Dessert');

INSERT INTO menus (id, category_id, name, description, price, image_link, is_available) VALUES
(1,  1, 'Special Fried Rice', 'Fried rice with egg, shredded chicken, and pickles.',   25000.00, 'uploads/default.png', 1),
(2,  1, 'Chicken Noodle Meatball', 'Chicken noodles with beef meatballs and fried wonton.', 22000.00, 'uploads/default.png', 1),
(3,  1, 'Honey Grilled Chicken', 'Grilled chicken in honey glaze with fresh veggies and chili paste.', 30000.00, 'uploads/default.png', 1),
(4,  1, 'Chicken Satay', 'Ten skewers of chicken satay with peanut sauce.', 28000.00, 'uploads/default.png', 1),
(5,  2, 'French Fries', 'Crispy french fries with chili sauce.', 15000.00, 'uploads/default.png', 1),
(6,  2, 'Fried Banana', 'Crispy battered fried banana with cheese topping.', 12000.00, 'uploads/default.png', 1),
(7,  2, 'Crispy Tofu', 'Crispy battered tofu with chili salt.', 13000.00, 'uploads/default.png', 1),
(8,  3, 'Sweet Iced Tea', 'Refreshing cold sweet tea.', 6000.00, 'uploads/default.png', 1),
(9,  3, 'Iced Orange', 'Freshly squeezed orange with ice cubes.', 8000.00, 'uploads/default.png', 1),
(10, 3, 'Iced Palm Sugar Milk Coffee', 'Iced coffee with milk and palm sugar.', 15000.00, 'uploads/default.png', 1),
(11, 4, 'Vanilla Ice Cream', 'Two scoops of vanilla ice cream.', 12000.00, 'uploads/default.png', 1),
(12, 4, 'Chocolate Pudding', 'Soft chocolate pudding with vanilla custard (sold out example).', 10000.00, 'uploads/default.png', 0);

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

INSERT INTO payment_methods (id, name, type, note, is_active) VALUES
(1, 'Cash',               'cash',     'Pay directly at the cashier after dining.',              1),
(2, 'QRIS',               'qris',     'Scan the QR code at the cashier, then show proof of payment.', 1),
(3, 'BCA Bank Transfer',  'transfer', 'Account no. 1234567890 a.n. Sample Restaurant.',      1),
(4, 'E-Wallet (OVO/GoPay)','ewallet','Send to 081200000001 a.n. Sample Restaurant.',         1),
(5, 'Credit Card',        'transfer', 'Inactive method example.',                             0);

INSERT INTO carts (id, user_id, menu_id, quantity) VALUES
(1, 2, 8, 2),
(2, 2, 5, 1),
(3, 2, 10, 1);

INSERT INTO orders (id, user_id, table_id, total_price, status, note, created_at, updated_at, completed_at) VALUES
(1, 2, 3, 62000.00, 'completed',  'Not spicy.',              '2026-11-20 12:15:00', '2026-11-20 12:50:00', '2026-11-20 12:50:00'),
(2, 2, 5, 60000.00, 'completed',  '',                        '2026-11-21 18:30:00', '2026-11-21 19:10:00', '2026-11-21 19:10:00'),
(3, 2, 1, 72000.00, 'processing', 'Separate chili paste.',   '2026-11-23 09:40:00', '2026-11-23 09:45:00', NULL),
(4, 2, 7, 40000.00, 'pending',    '',                        '2026-11-23 10:05:00', '2026-11-23 10:05:00', NULL),
(5, 2, 4, 28000.00, 'cancelled',  'Cancelled by admin.',     '2026-11-22 13:00:00', '2026-11-22 13:20:00', NULL),
(6, 2, 2, 37000.00, 'ready',      'Hold the ice cream.',     '2026-11-23 10:20:00', '2026-11-23 10:35:00', NULL);

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

INSERT INTO payments (id, order_id, payment_method_id, amount, status, paid_at, proof) VALUES
(1, 1, 2, 62000.00, 'paid',    '2026-11-20 12:20:00', NULL),
(2, 2, 3, 60000.00, 'paid',    '2026-11-21 18:45:00', 'uploads/proof-contoh.png'),
(3, 3, 4, 72000.00, 'paid',    '2026-11-23 09:44:00', NULL),
(4, 4, 1, 40000.00, 'pending', NULL,                  NULL),
(5, 5, 3, 28000.00, 'failed',  NULL,                  NULL),
(6, 6, 1, 37000.00, 'paid',    '2026-11-23 10:30:00', NULL);

INSERT INTO reviews (id, user_id, order_id, rating, comment, created_at) VALUES
(1, 2, 1, 5.0, 'The fried rice was delicious and service was fast.',      '2026-11-20 13:10:00'),
(2, 2, 2, 4.0, 'The grilled chicken was tasty, though it took a bit long.', '2026-11-21 19:30:00');