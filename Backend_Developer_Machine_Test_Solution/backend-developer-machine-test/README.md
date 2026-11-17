# Backend Developer Machine Test — simple guide

## Sabse pehle: Docker aur database ki current situation

- Project mein Docker se **PostgreSQL 16** start karne ka setup hai: `docker-compose.yml`.
- IntelliJ ka normal Run **PostgreSQL** profile use karta hai. Docker Desktop aur PostgreSQL container chalu hone chahiye. `application-postgres.yml` mein connection configured hai.
- Docker Desktop ab running hai; `machine-test-postgres` container PostgreSQL 16 chala raha hai.
- PostgreSQL par Product CRUD aur poora API flow Postman collection se verify hua. H2 temporary local-demo option hai; app restart par uske product records clear ho jate hain.
- Users ka account data jaan-boojhkar **memory mein** rakha hai, task ke in-memory authentication requirement ke hisaab se. Backend restart hone par accounts aur logged-out JWT ki blacklist clear hoti hai.

## App ka flow

1. Browser mein <http://localhost:3000> kholo. Frontend Express se aata hai; <http://localhost:8080> bhi ab usi page par redirect karta hai.
2. **Sign up** kholo. Username 8–50 characters ka ho. Email valid ho aur usmein `_` `@` se pehle ho, jaise `demo_user@example.com`.
3. Password 8–12 characters ka ho aur usmein uppercase, lowercase, number aur symbol ho. Example: `Pass@123`.
4. `Send code` dabao. Demo mein OTP email se nahi aata; code page par dikhta hai. Code fill karke `Create account` dabao. Signup par country server IP se check hota hai. Local computer par country detect na ho to 2-letter code, jaise `IN`, manually bhar sakte ho. `SY`, `AF`, `IR` signup ke liye blocked hain.
5. Signup ke baad **Login** karo. Backend password ko BCrypt hash se check karta hai aur ek ghante mein expire hone wala JWT deta hai. Frontend JWT ko browser `localStorage` mein rakhta hai.
6. Login ke baad Dashboard par `Welcome <Username>` dikhta hai. Sirf Administrator ko Settings khulta hai; Standard User ko `403 Access Denied` milta hai.
7. Products page se login user product add, list, edit aur delete kar sakta hai. Default mode mein records PostgreSQL mein jate hain; `local` profile H2 demo use karta hai.
8. **Logout** current JWT ko in-memory blacklist mein dalta hai. Usi token se Dashboard ya Products dobara nahi khulte.

## Project mein kya kahan hai

- `src/main/java/com/machine/auth/auth/` — signup, login, OTP aur country check.
- `src/main/java/com/machine/auth/security/` — JWT banana/check karna, token expiry aur logout blacklist.
- `src/main/java/com/machine/auth/user/` — in-memory users; passwords BCrypt hash ke roop mein.
- `src/main/java/com/machine/auth/product/` — product API aur PostgreSQL/H2 persistence.
- `src/main/java/com/machine/auth/config/` — Spring Security, Swagger aur frontend redirect.
- `frontend/public/index.html` — Northstar UI.
- `frontend/server.js` — Express web server aur API proxy.
- `postman/Backend_Developer_Machine_Test.postman_collection.json` — API requests aur automatic checks.

## App chalane ke commands

### PostgreSQL mode — Docker theek chalne par

Project folder mein terminal khol kar:

```powershell
docker compose up -d
mvn spring-boot:run
```

Ek aur terminal mein:

```powershell
cd frontend
npm install
npm start
```

IntelliJ ka normal Run bhi PostgreSQL use karega. Phir <http://localhost:3000> kholo. Swagger: <http://localhost:8080/swagger-ui.html>.

### Docker ke bina local demo

Docker unavailable ho to IntelliJ Run Configuration ke **Active profiles** field mein `local` likho; terminal se H2 demo chalane ke liye `mvn spring-boot:run -Dspring-boot.run.profiles=local` chalao. `local` profile H2 use karta hai.

Phir doosre terminal mein Express ke commands chalao: `cd frontend`, `npm install`, `npm start`.

## Postman mein API chalana

Postman mein `postman/Backend_Developer_Machine_Test.postman_collection.json` import karo. Collection ka default API address `http://localhost:8080` hai. Collection Runner mein **Standard user flow** aur uske baad **Administrator access flow** order mein chalao. OTP aur JWT collection khud save karti hai.

Collection mein validation errors, restricted country, OTP, Signup, Login, invalid Login, Dashboard, dono roles ka Settings access, Product CRUD, Logout aur logout ke baad token rejection ke checks hain.

## API addresses

| Method | Path | Kaam |
|---|---|---|
| POST | `/api/auth/otp/request` | Demo OTP mangna |
| POST | `/api/auth/otp/verify` | OTP verify karna |
| POST | `/api/auth/signup` | Account banana |
| POST | `/api/auth/login` | Login aur JWT lena |
| GET | `/api/dashboard` | Login user ka welcome message |
| GET | `/api/settings` | Administrator-only Settings |
| POST | `/api/auth/logout` | JWT revoke karna |
| GET/POST | `/api/products` | Products dekhna/banana |
| GET/PUT/DELETE | `/api/products/{id}` | Ek product dekhna/update/delete karna |

## Ab tak kya verify hua

- `mvn clean test`: BCrypt hash ko plain password se alag store karne wala unit test pass hua.
- Postman collection Newman runner se PostgreSQL 16 par chali: 22 requests, 23 assertions, 0 failed.
- PostgreSQL par signup/login, dashboard, role access, Product CRUD, logout aur revoked-token behavior verify hue.
- Frontend aur Swagger proxy ne HTTP 200 diya.
- Docker Compose PostgreSQL service running aur healthy hai.

## Zaroori demo limitations

- Demo OTP response mein code dikhata hai; email nahi bhejta.
- User accounts aur token blacklist memory mein hain, restart par reset honge.
- H2 sirf Docker/PostgreSQL unavailable hone par local demo profile ke liye hai.
- JWT secret aur database password ko deployment se pehle strong environment values se replace karo.
