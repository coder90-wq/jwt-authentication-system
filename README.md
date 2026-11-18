# Backend Developer Machine Test

## How to run the project

### 1. Start PostgreSQL

Open Docker Desktop. In IntelliJ, open the `Backend_Developer_Machine_Test_Solution/backend-developer-machine-test` folder. In the IntelliJ terminal, run:

```powershell
docker compose up -d
```

### 2. Start the backend

Open `src/main/java/com/machine/auth/AuthProductApiApplication.java` in IntelliJ and click the green **Run** button. The backend uses PostgreSQL by default.

### 3. Start the frontend

Open a second terminal tab and run:

```powershell
cd frontend
npm install
npm start
```

Run `npm install` only the first time. Keep the frontend terminal open.

### 4. Open the app

- Frontend: <http://localhost:3000>
- Backend API: <http://localhost:8080>
- Swagger: <http://localhost:8080/swagger-ui.html>

## If a port is busy

The app may already be running. Stop the existing app from IntelliJ's Run panel with the red **Stop** button, then run it once. Keep only one backend and one frontend running.

## Demo flow

Sign up, request the demo OTP, verify it, then log in. The demo OTP appears on the page; it is not sent by email. After login, try the Dashboard, Products, and Settings pages.

The Postman collection is in `Backend_Developer_Machine_Test_Solution/backend-developer-machine-test/postman/`.
