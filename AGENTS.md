# AGENTS.md — DSGram (practica-daw-2025-26-grupo-15)

Guidance for agentic coding tools operating in this repository.

---

## Project Overview

DSGram is a Spring Boot 4.0 social-network web application (Java 21, Maven) with:
- Server-side Mustache templates
- Spring Data JPA + MySQL (Docker)
- Spring Security with form login and OAuth2 (Google, GitHub)
- Bootstrap 5.3 + vanilla JS frontend

---

## Build & Run Commands

### Prerequisites

Start the MySQL container before running the app:
```bash
bash start_db.sh
# Equivalent to:
docker run --rm -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=books -p 3306:3306 -d mysql:9.2
```

Create a `.env` file in the project root with:
```
DB_USERNAME=root
DB_PASSWORD=password
KEYSTORE_PASSWORD=<value>
KEYSTORE_SECRET=<value>
GOOGLE_CLIENT_ID=<value>
GOOGLE_CLIENT_SECRET=<value>
GITHUB_CLIENT_ID=<value>
GITHUB_CLIENT_SECRET=<value>
```

### Maven Commands

```bash
# Run the application (HTTPS on port 8443)
./mvnw spring-boot:run

# Compile only
./mvnw compile

# Package as JAR
./mvnw clean package

# Install to local Maven repo
./mvnw clean install

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName

# Skip tests during build
./mvnw clean package -DskipTests
```

The Maven Wrapper (`./mvnw`) is preferred over a globally installed `mvn`. Java 21 is required.

### Application URL

The app runs on `https://localhost:8443`. The self-signed certificate will trigger browser warnings — this is expected in development.

---

## Project Structure

```
src/main/java/es/codeurjc/daw/library/
├── Application.java          # Spring Boot entry point
├── controller/               # Spring MVC @Controller classes
├── model/                    # JPA @Entity classes
├── repository/               # Spring Data JPA interfaces
├── security/                 # Spring Security config and services
└── service/                  # Business logic @Service classes

src/main/resources/
├── application.properties    # App config (secrets via .env)
├── keystore.jks              # SSL keystore
├── static/                   # CSS, JS, favicon
└── templates/                # Mustache HTML templates
    └── fragments/            # Partial templates (loaded via AJAX)
```

---

## Testing

No tests currently exist (`src/test/` is absent). When adding tests:

- Place them under `src/test/java/es/codeurjc/daw/library/`
- Use `@SpringBootTest` for integration tests and `@WebMvcTest` for controller slice tests
- Use `@DataJpaTest` for repository tests (requires an H2 or Testcontainers database)
- Run a single test: `./mvnw test -Dtest=MyTestClass#myTestMethod`

---

## Code Style Guidelines

No automated linter or formatter is configured. Follow the conventions already present in the codebase.

### Java

**Package & class structure**
- Base package: `es.codeurjc.daw.library`
- Sub-packages: `controller`, `model`, `repository`, `security`, `service`
- One public class per file, filename matches class name

**Naming**
- Classes: `PascalCase` — e.g., `ExerciseListController`, `WebSecurityConfig`
- Methods and fields: `camelCase` — e.g., `findByOwner`, `encodedPassword`
- Constants: `UPPER_SNAKE_CASE`
- JPA entity table names: use `@Entity(name = "XxxTable")` to avoid SQL reserved-word conflicts (e.g., `UserTable`, `ExerciseTable`)
- Repository interfaces: `XxxRepository`; services: `XxxService`; controllers: `XxxController`

**Imports**
- No wildcard (`.*`) imports
- Group: standard Java → third-party (Spring, Jakarta) → project-internal
- No blank lines between import groups (tooling does not enforce order)

**Dependency injection**
- Field injection via `@Autowired` is the existing convention — continue using it for consistency
- Do not introduce constructor injection unless refactoring an entire class

**JPA entities**
- All getters and setters written manually (no Lombok)
- Relationships: `@OneToMany(mappedBy = ..., cascade = CascadeType.ALL)`, `@ManyToOne`
- Use `@ElementCollection(fetch = FetchType.EAGER)` for embedded collections (e.g., `User.roles`)
- Use `@Lob` for binary fields (image/PDF `Blob`)
- Do not use `@NotNull` / `@Size` annotations — validation is done imperatively in service methods

**Error handling**
- Service methods throw `RuntimeException` or `IllegalArgumentException` with descriptive messages
- Controllers catch exceptions from services and return the `"error"` view:
  ```java
  model.addAttribute("errorMessage", e.getMessage());
  return "error";
  ```
- Use `Optional` for repository lookups; chain with `.orElseThrow()` or `.orElse(null)` as appropriate
- Never swallow exceptions silently

**Authentication resolution**
- Every controller needing the current user has a private `resolveUser(Principal principal)` helper that handles both `OAuth2AuthenticationToken` and standard form-login principals
- Until a shared utility class is introduced, replicate this pattern from an existing controller

**Custom queries**
- Use `@Query(nativeQuery = true)` with `@Param` for native SQL in repositories
- Prefer JPQL for simple queries; native SQL only when JPQL is insufficient

### Templates (Mustache)

- Files live in `src/main/resources/templates/` with `.html` extension
- Syntax: `{{variable}}`, `{{#section}}...{{/section}}`, `{{^section}}...{{/section}}`
- CSRF token is injected by a Spring interceptor and available as `{{token}}` in every view
- Partial templates (fragments) go in `templates/fragments/` and are served via AJAX endpoints

### Frontend (CSS / JavaScript)

- No build tooling; plain CSS and vanilla JS files in `src/main/resources/static/`
- Bootstrap 5.3.3 and Bootstrap Icons are loaded from CDN — do not bundle locally
- JavaScript uses async/await fetch for AJAX; no modules, no bundler, no npm
- Keep JS minimal and co-located with the feature it supports (e.g., `home.js` for user search)

---

## Configuration Notes

- **Database schema**: `spring.jpa.hibernate.ddl-auto=create-drop` — the schema is dropped and recreated on every startup. Initial data is seeded by `DatabaseInitializer.java`.
- **Database URL**: `jdbc:mysql://localhost/books` — the database name `books` is a leftover scaffold name.
- **Secrets**: never commit `.env`, `application-local.properties`, or `application-secrets.properties`. All secrets must be injected via environment variables or the `.env` file.
- **Logging**: SQL and Security logging are set to DEBUG/TRACE in `application.properties` — this is intentional for development; adjust before any production deployment.

---

## Common Pitfalls

- The `User` entity is stored in a table named `UserTable` because `user` is a reserved word in MySQL.
- `resolveUser(Principal principal)` must handle both `OAuth2AuthenticationToken` and `UsernamePasswordAuthenticationToken` — missing either branch will cause `ClassCastException` at runtime.
- `ddl-auto=create-drop` means data is lost on every restart; always rely on `DatabaseInitializer` for required seed data rather than manual DB inserts.
- The app uses HTTPS only (port 8443). Plain HTTP is not configured.
