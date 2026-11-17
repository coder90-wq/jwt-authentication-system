# Backend Developer Machine Test — Authentication & Products

A runnable Spring Boot 3 application with in-memory user accounts, PostgreSQL-backed product CRUD, JWT authentication, OTP verification, role-based authorization, Swagger UI, and a browser frontend.

## Requirements
- Java 17+
- Maven 3.9+
- Docker Desktop (or a local PostgreSQL 16 instance)

## Run
1. Start PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Start the application (PostgreSQL mode):
   ```bash
   mvn spring-boot:run
   ```
3. Open the frontend: http://localhost:8080/
4. Open Swagger: http://localhost:8080/swagger-ui.html

Default database settings are in `src/main/resources/application.yml`. Override with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
For a local demo without Docker/PostgreSQL, H2 is already included; start the app with `DB_URL=jdbc:h2:mem:machinetest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`, `DB_USERNAME=sa`, and an empty `DB_PASSWORD`. Product data in H2 is temporary and is cleared when the app stops. User accounts are held in memory and are cleared whenever the app restarts, as required by the in-memory authentication task.
Set a strong random `JWT_SECRET` (at least 32 bytes) before deploying.

## API endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/otp/request` | Public |
| POST | `/api/auth/otp/verify` | Public |
| POST | `/api/auth/signup` | Public, requires verified OTP |
| POST | `/api/auth/login` | Public |
| GET | `/api/dashboard` | Authenticated |
| GET | `/api/settings` | Administrator |
| POST | `/api/auth/logout` | Authenticated |
| GET | `/api/error` | Public |
| GET | `/api/products` | Authenticated |
| GET | `/api/products/{id}` | Authenticated |
| POST | `/api/products` | Authenticated |
| PUT | `/api/products/{id}` | Authenticated |
| DELETE | `/api/products/{id}` | Authenticated |

## Signup rules
- Username: 8–50 characters.
- Email: valid email format containing both `@` and `_` (as explicitly required by the PDF).
- Password: 8–12 characters with uppercase, lowercase, digit and special character.
- User type: `STANDARD_USER` or `ADMINISTRATOR` (shown as Standard User / Administrator in the UI).
- Restricted country codes: `SY`, `AF`, `IR` (configurable). The browser now detects the visitor country from their public IP and submits the detected code; the API checks the code during signup. The demo browser lookup uses [ipapi.co's documented IP geolocation API](https://ipapi.co/api/), so the browser needs internet access.
- OTP must be requested and verified before signup.

## Important implementation notes
- Users are stored in a thread-safe in-memory store. Passwords are stored as BCrypt hashes, never plain text.
- JWTs are signed and expire after one hour by default.
- Logout revokes the presented JWT in an in-memory blacklist. This meets the single-instance machine-test flow; use Redis or a shared database for a multi-instance production deployment.
- OTPs are held in memory for five minutes. The demo endpoint returns the OTP in its response so the flow can be tested without an email/SMS provider. Replace this with an email/SMS delivery provider in production.
- Country detection in the demo is performed by the browser using its public IP. A client can still alter API requests, so production should perform GeoIP lookup server-side using the connecting IP and a trusted proxy configuration.
- The task's PDF lists Express.js in the technology stack but specifies Java Spring Boot for the backend. This implementation follows the explicit Java/Spring Boot backend requirement; the frontend is served by Spring Boot.
- Admin registration is open for test purposes because the assignment provides no admin provisioning workflow. In production, administrator creation must be restricted to a secure invitation/seed process.

## Postman examples

Request OTP:
```http
POST /api/auth/otp/request
Content-Type: application/json
{"email":"demo_user@example.com"}
```

Verify OTP using `demoOtp` returned by the request:
```http
POST /api/auth/otp/verify
Content-Type: application/json
{"email":"demo_user@example.com","otp":"123456"}
```

Signup:
```json
{"username":"demo_user","email":"demo_user@example.com","password":"Pass@123","userType":"Standard User","countryCode":"IN"}
```

Login:
```json
{"email":"demo_user@example.com","password":"Pass@123"}
```
Use returned token as `Authorization: Bearer <token>` for protected endpoints.

## Validation and expected responses
- Invalid login: HTTP 401 with `{"message":"Login Failed","errorEndpoint":"/api/error"}`.
- Dashboard: `{"message":"Welcome demo_user"}`.
- Logout: `{"message":"demo_user Logged Out"}`; the revoked token receives 401 on subsequent requests.
- Non-admin Settings: HTTP 403.
- Admin Settings: `{"message":"Administrator settings access granted"}`.

## Before submission
- Run the app and test the endpoints in Postman.
- Replace the demo JWT secret and database credentials.
- Configure real server-side country detection and OTP delivery.
- Add tests for your final environment and commit the project to your GitHub repository.
