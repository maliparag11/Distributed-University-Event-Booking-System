# **BookingService_Setup.md**

## **1. Requirements**

Install these before running the Booking Service:

### ✔ Java 21
https://adoptium.net

### ✔ Maven (optional – project includes `mvnw`)
Check version:
```powershell
mvn -v
```

### ✔ Docker Desktop
https://www.docker.com/products/docker-desktop/

### ✔ Postman (for API testing)

---

## **2. Start PostgreSQL (Docker – PowerShell)**

Run:

```powershell
docker run -d `
  --name booking-postgres `
  -e POSTGRES_USER=booking_service_user `
  -e POSTGRES_PASSWORD=booking_service_pass `
  -e POSTGRES_DB=booking_service_db `
  -p 5432:5432 `
  postgres:15
```

Verify container:

```powershell
docker ps
```

Connect to DB:

```powershell
docker exec -it booking-postgres psql -U booking_service_user -d booking_service_db
```

If you see the `booking_service_db=#` prompt → DB is running.

---

## **3. Start RabbitMQ (Docker – PowerShell)**

Run:

```powershell
docker run -d `
  --name booking-rabbit `
  -p 5672:5672 `
  -p 15672:15672 `
  rabbitmq:3-management
```

Check UI:

👉 http://localhost:15672  
User: **guest**  
Password: **guest**

---

## **4. Confirm Application Properties**

`src/main/resources/application.properties` must contain:

```
spring.application.name=booking-service
server.port=8082

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/booking_service_db
spring.datasource.username=booking_service_user
spring.datasource.password=booking_service_pass

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Event Service (must run on port 8081)
event-service.base-url=http://localhost:8081
```

---

## **5. Run Booking Service**

Navigate to project folder:

```powershell
cd booking-service
```

Run using Maven wrapper:

```powershell
./mvnw spring-boot:run
```

Or if Maven is installed:

```powershell
mvn spring-boot:run
```

Service is ready when you see:

```
Tomcat started on port 8082
Started BookingServiceApplication
```

---

## **6. Test API with Postman**

### **POST /bookings**

**URL:**
```
http://localhost:8082/bookings
```

**Body (JSON):**
```json
{
  "userId": 1,
  "eventId": 10
}
```

### Expected Responses

#### ✔ If everything works:
```json
{
  "id": 100,
  "userId": 1,
  "eventId": 10
}
```

#### ❌ If Event Service is down:
```json
{
  "message": "Event is fully booked or reservation failed.",
  "status": 400
}
```

#### ❌ If RabbitMQ or DB fails:
```json
{
  "message": "Internal server error",
  "status": 500
}
```

---

## **7. Stop and Clean Containers**

### Stop:
```powershell
docker stop booking-postgres
docker stop booking-rabbit
```

### Remove:
```powershell
docker rm booking-postgres
docker rm booking-rabbit
```

### Remove ALL containers (optional):
```powershell
docker ps -aq | ForEach-Object { docker stop $_; docker rm $_ }
```

### Remove volumes (optional – deletes DB data):
```powershell
docker volume prune -f
```

---

## **8. What Must Be Running Before Testing?**

| Component | Status |
|----------|--------|
| PostgreSQL | **Must be running** |
| RabbitMQ | **Must be running** |
| Event Service | **Must be running** |
| Booking Service | **This project** |

If **Event Service is not running**, bookings will always fail.

---

## **9. Common Issues & Fixes**

### ❌ Always getting “Event is fully booked or reservation failed.”
Event Service is **offline** → must run on port **8081**.

### ❌ Queue not visible in RabbitMQ UI
Booking failed before publish → fix Event Service.

### ❌ Cannot connect to DB
Restart:

```powershell
docker restart booking-postgres
```

### ❌ Ports in use
Find PID:

```powershell
netstat -ano | findstr :5432
```

Kill:

```powershell
taskkill /PID <pid> /F
```

---

# ✔ Booking Service setup is complete.
