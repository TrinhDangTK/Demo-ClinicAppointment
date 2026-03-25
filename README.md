# clinicappointment — Hệ thống đặt lịch khám (Clinic Appointment)

Ứng dụng web quản lý bác sĩ, bệnh nhân và lịch khám, xây dựng bằng **Spring Boot 4**, **Spring Security** (đăng nhập form + **OAuth2 Google**), **Thymeleaf**, **Spring Data JPA** và **MySQL**.

## Yêu cầu môi trường

- **JDK** 17+
- **Maven** 3.8+
- **MySQL** 8.x (hoặc tương thích)

## Chạy ứng dụng

```bash
mvn spring-boot:run
```

Mặc định: `http://localhost:8080`

- Trang danh sách bác sĩ: `/` hoặc `/courses`
- Đăng nhập: `/login` — đăng ký bệnh nhân: `/register`
- Trang cá nhân bệnh nhân (sau đăng nhập): `/enroll`
- Khu vực quản trị (role ADMIN): `/admin/doctors`, `/admin/appointments`, …

## Cấu hình

Sao chép và chỉnh `src/main/resources/application.properties`:

| Thuộc tính | Mô tả |
|------------|--------|
| `spring.datasource.url` | JDBC URL (database sẽ được tạo nếu dùng `createDatabaseIfNotExist`) |
| `spring.datasource.username` / `password` | Tài khoản MySQL |
| `spring.jpa.hibernate.ddl-auto` | `update` (dev) — production nên dùng migration có kiểm soát |
| `spring.security.oauth2.client.registration.google.*` | Client ID / Secret từ Google Cloud (OAuth 2.0 Web client) |

**Redirect URI** trên Google Cloud (ví dụ local):  
`http://localhost:8080/login/oauth2/code/google`

**Authorized JavaScript origins:** `http://localhost:8080`  
**Authorized redirect URIs:** `http://localhost:8080/login/oauth2/code/google`

> **Bảo mật / Git:** Không commit mật khẩu DB, Google Client Secret, hoặc file `application-local.properties`. Xem mục *Push lên Git* ở cuối README. Mẫu cấu hình local: `application-local.properties.example`.

## Tính năng chính (tóm tắt)

- Đăng ký / đăng nhập bệnh nhân (BCrypt), phân quyền **PATIENT** / **ADMIN**
- Đăng nhập **Google** (OAuth2) — tự tạo hoặc liên kết bệnh nhân theo email
- Admin: CRUD bác sĩ, khoa, lịch khám
- Bệnh nhân: xem lịch, đặt lịch, xem chi tiết, hủy lịch
- Trang chủ: **tìm kiếm bác sĩ bằng Ajax** (`GET /api/doctors`) — không reload trang

## API tìm kiếm (JSON)

```http
GET /api/doctors?keyword=&page=0
```

Phản hồi: JSON (`content`, `currentPage`, `totalPages`, …).

---

## SQL ví dụ (MySQL)

Dùng khi bạn muốn **seed dữ liệu thủ công** hoặc hiểu schema. Với `ddl-auto=update`, Hibernate sẽ tạo/cập nhật bảng; bạn có thể chạy các `INSERT` sau **sau khi** bảng đã tồn tại (hoặc sau lần chạy app đầu tiên).

**1. Tạo database (tùy chọn — có thể để JDBC tự tạo)**

```sql
CREATE DATABASE IF NOT EXISTS clinicappointment
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clinicappointment;
```

**2. Vai trò (`roles`)**

```sql
INSERT INTO roles (name) VALUES ('PATIENT'), ('ADMIN')
ON DUPLICATE KEY UPDATE name = VALUES(name);
```

**3. Khoa (`departments`)**

```sql
INSERT INTO departments (name) VALUES
  ('Khoa Nội'),
  ('Khoa Ngoại'),
  ('Khoa Nhi')
ON DUPLICATE KEY UPDATE name = VALUES(name);
```

**4. Bác sĩ (`doctors`)** — `department_id` phải tồn tại (thường 1, 2, 3 sau bước trên)

```sql
INSERT INTO doctors (name, image_url, specialty, department_id) VALUES
  ('BS. Nguyễn Văn An','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcwBqDuuxTdnhdDW5eMlOXIUfR6pL2VeAvjQ&s', 'Nội tổng quát', 1),
```

**5. Bệnh nhân (`patients`)** — `password_hash` là **BCrypt** (ví dụ plain text tương ứng: `password`)

```sql
-- Hash BCrypt (Spring BCryptPasswordEncoder, strength 10) cho mật khẩu: password, password ví dụ đã hash là 123456
INSERT INTO patients (username, password_hash, email) VALUES
  ('patient01', '$2a$12$zLSm07Pi5u8b5loWXW48/.4mHzCQSpUJMp/A502pIu6RLazO7Mfgm', 'patient01@example.com'),
ON DUPLICATE KEY UPDATE email = VALUES(email);
```

**6. Gán quyền (`patient_roles`)** — khóa chính `(patient_id, role_id)`

```sql
-- Giả sử patient có id = 1, PATIENT = id 1, ADMIN = id 2 trong bảng roles
INSERT INTO patient_roles (patient_id, role_id) VALUES (1, 1)
ON DUPLICATE KEY UPDATE patient_id = patient_id;
```

Để gán **ADMIN** cho user `patient01` (nếu cần thử khu admin):

```sql
INSERT INTO patient_roles (patient_id, role_id) VALUES (1, 2)
ON DUPLICATE KEY UPDATE patient_id = patient_id;
```

**7. Lịch khám (`appointments`) — ví dụ**

```sql
INSERT INTO appointments (patient_id, doctor_id, appointment_time, status, note) VALUES
  (1, 1, '2026-04-01 08:30:00', 'PENDING', 'Kham tong quat');
```

Giá trị `status` theo enum ứng dụng: `PENDING`, `CONFIRMED`, `CANCELLED`, `DONE`.

---

### Ghi chú

- **Mật khẩu:** Nên tạo user qua `/register` hoặc sinh BCrypt bằng `BCryptPasswordEncoder` trong code / tiện ích uy tín; không dùng hash mẫu trên cho môi trường thật.
- **ID tự tăng:** Các `INSERT` phụ thuộc thứ tự; điều chỉnh `id` nếu trùng dữ liệu cũ.
- **Ràng buộc:** `(doctor_id, appointment_time)` là duy nhất — không trùng cặp bác sĩ + giờ.

## Cấu trúc mã nguồn (rút gọn)

```
src/main/java/com/clinicappointment/
  config/          Spring Security, OAuth2 beans
  controller/      MVC + REST (tìm kiếm bác sĩ)
  dto/
  model/           Entity JPA
  repository/
  service/
src/main/resources/
  templates/       Thymeleaf (Home, auth, admin, enroll)
  application.properties
```

## Push lên Git — nên bỏ qua / không commit

| Nội dung | Ghi chú |
|----------|---------|
| `target/` | Build Maven (đã có trong `.gitignore`) |
| `.idea/`, `*.iml`, `.vscode/` | Cấu hình IDE |
| `application-local.properties` | Chứa secret trên máy bạn — **đã gitignore** |
| `.env` | Biến môi trường (nếu dùng) |
| File log, `*.log` | |
| **Mật khẩu DB, OAuth secret** | Chỉ trong `application-local.properties` hoặc biến môi trường — **không** để trong `application.properties` đã push |

**Đã cấu hình:** `application.properties` trong repo dùng placeholder / `${...}`; copy `application-local.properties.example` → `application-local.properties` và điền thật (file này không lên Git).

Nếu **đã từng commit** secret: xoá khỏi Git history (ví dụ `git filter-repo`) và **đổi** mật khẩu DB + **xoay** Google OAuth Client Secret trên Google Cloud.

## License

Dự án chỉ là demo phục vụ học tập
