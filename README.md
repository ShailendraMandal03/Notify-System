# 🔔 Notify Hub – Real-Time Notification System

A full-stack **real-time notification system** built using **Spring Boot** and **Angular**, designed for enterprise-level communication across users, roles, and departments.

---

## 🚀 Features

* 🔐 **Session-based Authentication**
* 👤 **Role-based Access Control (Admin/User)**
* 📢 **Send Notifications**

  * To ALL users
  * Specific users
  * Departments
  * Roles
* ⚡ **Real-time Notifications (WebSocket + STOMP)**
* 📬 **User Notification Dashboard**
* ✔️ **Mark as Read / Mark All as Read**
* ✅ **Approval / Rejection Actions**
* 📊 **Unread Notification Count (Bell icon)**
* 🗄️ **Database Storage for all notifications**
* 🧾 **Global Exception Handling**
* 📝 **Production-level Logging (Logback)**

---

## 🏗️ Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Data JPA
* MySQL
* WebSocket (STOMP)
* SLF4J + Logback
* Spring Session (HttpSession)

### Frontend

* Angular (Standalone API)
* TypeScript
* RxJS
* SockJS + STOMP.js

---

## 📂 Project Structure

### Backend

```
com.peoplestrong.NotificationSystem
│
├── config/            # WebSocket, CORS, Swagger, Logging
├── controller/        # REST APIs
├── service/           # Business logic
├── repository/        # Database layer
├── entity/            # JPA entities
├── dto/               # Request/Response DTOs
├── enums/             # Enums (Type, Priority, etc.)
├── exception/         # Global exception handling
```

---

### Frontend

```
src/app
│
├── components/
│   ├── login/
│   ├── dashboard/
│   └── notification-bell/
│
├── services/
│   ├── auth.service.ts
│   ├── notification.service.ts
│   └── websocket.service.ts
│
├── models/
│   └── notification.model.ts
```

---

## 🔌 API Endpoints

### 🔐 Auth

* `POST /api/auth/login`
* `POST /api/auth/logout`

---

### 📢 Admin (Send Notification)

* `POST /api/admin/notifications`

---

### 👤 User Notifications

* `GET /api/user/notifications`
* `PUT /api/user/notifications/{id}/read`
* `PUT /api/user/notifications/read-all`
* `GET /api/user/notifications/unread-count`
* `POST /api/user/notifications/{id}/action`

---

## ⚡ Real-Time Flow

1. Admin sends notification
2. Backend:

   * Saves in DB
   * Pushes via WebSocket
3. User frontend:

   * Subscribes to `/topic/notification/{userId}`
   * Receives instantly ⚡

---

## 🗄️ Database Design

* `notifications` → Main notification
* `notification_targets` → Who should receive
* `user_notifications` → Per-user copy (important)
* `notification_actions` → Approve/Reject tracking

---

## 🔐 Authentication Flow (Session-Based)

* User logs in → Session created (JSESSIONID)
* Cookie stored in browser
* All APIs validated via session
* No JWT used

---

## 📊 Logging

* SLF4J + Logback
* Daily log files
* Stored in `/logs` folder
* Auto cleanup using `maxHistory`

---

## 🧪 How to Run

### Backend

```bash
mvn clean install
mvn spring-boot:run
```

---

### Frontend

```bash
npm install
ng serve
```

---

## 🌐 URLs

* Frontend: http://localhost:4202
* Backend: http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## 📸 Screens

* Login Page
* Dashboard
* Notification Bell
* Real-time updates

---

## 🎯 Use Cases

* Enterprise communication systems
* HR notifications
* Approval workflows
* Internal alerts system

---

## 🚀 Future Improvements

* JWT Authentication
* Mobile push notifications
* Email/SMS integration
* Kafka-based scaling
* ELK logging dashboard

---

## 👨‍💻 Author

**Shailendra Mandal**

---

## ⭐ If you like this project

Give it a ⭐ on GitHub!
