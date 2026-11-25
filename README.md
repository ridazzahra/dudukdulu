# DudukDulu: Dine-in food order website using JSP

DudukDulu is a dine-in food ordering web app. Customers select a table,
browse the menu, place an order, and pay. Admins manage menus, tables,
categories, payment methods, and orders.

> **Website link:** [link]\
**Repository Link:**  https://github.com/ridazzahra/dudukdulu.git

## Features
**Customer**
- Register / login
- Browse menu catalog by category
- Add menu to cart, change quantities, and remove items
- Select a dining table and checkout an order
- Choose a payment method
- View order history and details
- Review a completed order

**Admin**
- Manage menus (with image upload), categories, dining tables, payment methods
- Dashboard
- View and update order status
- Confirm order payments
- Manage customer reviews
- Activate / deactivate users

## Tech Stack
Java 17 · Jakarta Servlet 6 / JSP · JSTL · Apache Tomcat 10.1 · Maven · MySQL 8 · JDBC (DAO pattern) · jBCrypt · Bootstrap 5

## Running the App
Everything runs in containers, so no local Java, Tomcat, or MySQL installation is needed.
Install Docker Desktop first (on Windows it requires WSL 2).
```bash
docker compose up --build
# then open http://localhost:8080
```
Docker builds the app with Maven and runs it on Tomcat, while MySQL runs in a separate container.
The `food_order` database is created automatically on first start from
`database/01_schema.sql` and `database/02_seed.sql`.
```bash
docker compose down        # to stop the app (data is kept)
docker compose down -v     # to stop and delete all data (the database is re-created on next start)
```

## Demo account
| Role | Email | Password |
|---|---|---|
| Admin | admin@dudukdulu.test | admin123 |
| Customer | customer@dudukdulu.test | customer123 |
| Non-active (non-active user feature test) | penyusup@dudukdulu.test | customer123 |

## Database

Ten tables: `users`, `categories`, `menus`, `carts`, `orders`, `order_items`, `reviews`,
`dining_tables`, `payment_methods`, and `payments`. 

## Structure
```
database/           01_schema.sql, 02_seed.sql
src/main/java/com/foodorder/
  model/            entity (User, Category, Menu, DiningTable, PaymentMethod, ...)
  dao/              database access, PreparedStatement
  controller/       Servlet for each module (BaseServlet = as shared helper)
  filter/           EncodingFilter, FlashFilter, AuthFilter, AdminFilter  (order defined in web.xml)
  util/             DbUtil, PasswordUtil, Auth, Flash, Validator, Page, ImageUploader, Sql
  service/          OrderService
src/main/webapp/
  WEB-INF/web.xml   WEB-INF/views/{layout,components,auth,error,admin,customer}
  assets/           css, js, img (default.png)
```

## Team
EF234301 Web Programming (D), 2026 - Quiz 2
- Aisyah Putri Diza / 5025251062
- Rida Izzati Azzahra / 5025251065
- Amanda Putri C. / 5025251104

