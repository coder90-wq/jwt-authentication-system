# Project Run Guide

## 1. Start PostgreSQL

Start Docker Desktop. From this project folder, run in the IntelliJ Terminal:

```powershell
docker compose up -d
```

## 2. Start the backend

Open `src/main/java/com/machine/auth/AuthProductApiApplication.java` in IntelliJ and click **Run**. It uses PostgreSQL by default.

## 3. Start the frontend

Open a second IntelliJ Terminal tab:

```powershell
cd frontend
npm install
npm start
```

Run `npm install` only the first time. Leave the frontend terminal running.

## 4. Open the app

- Frontend: <http://localhost:3000>
- Backend API: <http://localhost:8080>
- Swagger: <http://localhost:8080/swagger-ui.html>

## If a port is busy

The app may already be running. Stop the existing app from IntelliJ's Run panel, then run it once. Keep only one backend and one frontend running.

For a local demo without Docker, set IntelliJ's active profile to `local`; this uses temporary H2 instead of PostgreSQL.

The Postman collection is in `postman/Backend_Developer_Machine_Test.postman_collection.json`.
