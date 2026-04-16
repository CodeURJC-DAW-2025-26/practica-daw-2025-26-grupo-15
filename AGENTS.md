# AGENTS.md

## Repo Reality (verified)
- This repo has two active apps:
  - `backend/`: Spring Boot 4 (Java 21, Maven), serves Mustache web pages and REST API.
  - `frontend/`: React Router v7 SPA (`ssr: false`) that talks to backend REST.
- The root `README.md` is partially stale for SPA work; trust executable config (`backend/pom.xml`, `frontend/package.json`, `frontend/vite.config.ts`).

## Fast Start Commands
- Start DB only (local backend dev):
  - `bash start_db.sh` (runs MySQL `9.6` on `3306`, DB `dsgram`, root password `password`).
- Run backend from repo root (important for `.env` loading):
  - `mvn -f backend/pom.xml spring-boot:run`
- Run frontend dev server:
  - `npm --prefix frontend install`
  - `npm --prefix frontend run dev`

## Required Env / Working Directory Gotcha
- Backend config imports `.env` via `spring.config.import=optional:file:.env[.properties]`.
- `.env` is resolved from the process working directory, not from `backend/` automatically.
- If you run Maven from repo root, use root `.env`; if you run from `backend/`, it will read `backend/.env`.

## Verification Commands (no CI/lint config found)
- Backend compile/test:
  - `mvn -f backend/pom.xml test`
- Frontend checks:
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
- API docs regeneration (integration-test phase plugins):
  - `mvn -f backend/pom.xml integration-test`

## Architecture Pointers
- Backend entrypoint: `backend/src/main/java/es/codeurjc/daw/library/Application.java`.
- Web MVC controllers: `backend/src/main/java/es/codeurjc/daw/library/controller/web/`.
- REST controllers: `backend/src/main/java/es/codeurjc/daw/library/controller/rest/` (`/api/v1/**`).
- Frontend routes are defined in `frontend/app/routes.ts`.

## Security/Auth Details That Affect Changes
- Two security filter chains in `SecurityConfig`:
  - `/api/**`: JWT/cookie-based, stateless, CSRF disabled.
  - Web pages: form login + OAuth2 (Google/GitHub).
- JWT auth for REST uses cookies named `AuthToken` and `RefreshToken` (not Authorization header by default path).
- Mustache forms rely on `{{token}}` inserted by `CSRFHandlerConfiguration`; keep `_csrf` hidden inputs in template forms.

## Data / Persistence Gotchas
- Entities use explicit table names (`UserTable`, `ExerciseTable`, etc.); keep this pattern for new entities.
- `DatabaseInitializer` seeds demo data only when `userRepository.count() == 0`.
- `spring.jpa.hibernate.ddl-auto` comes from `DB_CONFIG` (default `none`), so data reset behavior depends on env.

## API/Frontend Integration Quirks
- Frontend dev server proxies `/api` to `https://localhost:8443/api` with TLS verification disabled (`secure: false`).
- REST list search uses query param `ownerId` in `ExerciseListRestController`; do not assume `userId`.

## Docker Notes
- Root `docker-compose.yml` uses prebuilt image `pruizz/dsgram-app:latest`, not local source build.
- `create-image.sh`, `publish_image.sh`, and `publish_docker-compose.sh` expect `<dockerhub_user> [tag]`.
