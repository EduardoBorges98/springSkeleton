# 🦴 Spring Boot Skeleton

Esqueleto profissional e reutilizável para projetos Spring Boot.

---

## 🧰 Stack

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.2 | Framework principal |
| Spring Security | 6 | Autenticação e autorização |
| JWT (jjwt) | 0.12 | Tokens de acesso |
| Spring Data JPA | - | Persistência |
| PostgreSQL | 16 | Banco de dados |
| Flyway | - | Migrations |
| Redis | 7 | Cache |
| MapStruct | 1.5 | Mapeamento de objetos |
| Lombok | 1.18 | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5 | Documentação Swagger |
| Testcontainers | 1.19 | Testes de integração |

---

## 📁 Estrutura do Projeto

```
src/main/java/com/skeleton/
├── config/
│   ├── CacheConfig.java          # Redis + TTL
│   ├── JpaConfig.java            # Auditoria automática
│   ├── OpenApiConfig.java        # Swagger + JWT
│   └── SecurityConfig.java       # CORS, JWT stateless, rotas públicas
│
├── controller/
│   ├── AuthController.java       # /auth/register, /auth/login, /auth/refresh
│   └── UserController.java       # CRUD de usuários
│
├── dto/
│   ├── request/
│   │   ├── CreateUserRequest.java
│   │   ├── UpdateUserRequest.java
│   │   ├── LoginRequest.java
│   │   └── RefreshTokenRequest.java
│   └── response/
│       ├── ApiResponse.java      # Envelope padrão
│       ├── AuthResponse.java
│       ├── PageResponse.java     # Wrapper de paginação
│       └── UserResponse.java
│
├── entity/
│   ├── BaseEntity.java           # UUID, auditoria, soft delete
│   └── User.java
│
├── enums/
│   └── Role.java                 # ROLE_ADMIN, ROLE_USER, ROLE_MANAGER
│
├── exception/
│   ├── BusinessException.java
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── mapper/
│   └── UserMapper.java           # MapStruct
│
├── repository/
│   └── UserRepository.java       # Queries com soft delete
│
├── security/
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   └── service/
│       ├── JwtService.java
│       └── UserDetailsServiceImpl.java
│
└── service/
    ├── UserService.java           # Interface
    └── impl/
        ├── AuthService.java
        └── UserServiceImpl.java   # Cache + transações

src/main/resources/
├── application.yml                # Perfis: local, test, prod
└── db/migration/
    └── V1__init.sql               # Schema + seed admin

src/test/java/com/skeleton/
├── integration/
│   ├── BaseIntegrationTest.java   # Testcontainers base
│   └── AuthControllerIT.java
└── service/
    └── UserServiceTest.java       # Testes unitários com Mockito
```

---

## 🚀 Como usar

### 1. Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven 3.9+

### 2. Subir a infraestrutura

```bash
docker compose up -d
```

Isso sobe o **PostgreSQL** na porta `5432` e o **Redis** na porta `6379`.

### 3. Configurar variáveis de ambiente

```bash
cp .env.example .env
# edite o .env com seus valores
```

> Gere um JWT secret seguro:
> ```bash
> openssl rand -base64 64
> ```

### 4. Rodar a aplicação

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 5. Acessar o Swagger

Abra no navegador:
```
http://localhost:8080/api/swagger-ui.html
```

---

## 🔐 Autenticação

O projeto usa **JWT stateless**. Fluxo:

```
POST /api/auth/login
  → retorna accessToken (1 dia) + refreshToken (7 dias)

Authorization: Bearer <accessToken>
  → header obrigatório em rotas protegidas

POST /api/auth/refresh
  → renova o accessToken sem novo login
```

### Credenciais padrão (seed)

| Campo | Valor |
|---|---|
| Email | `admin@skeleton.com` |
| Senha | `Admin@123` |
| Role | `ROLE_ADMIN` |

> ⚠️ Troque a senha do admin antes de subir em produção!

---

## 🌍 Perfis

| Perfil | Quando usar |
|---|---|
| `local` | Desenvolvimento local (logs DEBUG, SQL visível) |
| `test` | Testes automatizados (H2 in-memory, Flyway off) |
| `prod` | Produção (logs WARN, sem SQL) |

---

## 🧪 Testes

```bash
# Unitários (sem Docker)
./mvnw test

# Integração (requer Docker para Testcontainers)
./mvnw verify
```

---

## ♻️ Como reutilizar em novos projetos

1. Clone este repositório
2. Renomeie o pacote `com.skeleton` para `com.suaempresa.seuprojeto`
3. Ajuste o `pom.xml` (`groupId`, `artifactId`, `name`)
4. Atualize o `application.yml` com o nome da aplicação
5. Crie suas entidades estendendo `BaseEntity`
6. Adicione suas migrations em `db/migration/`

---

## 📐 Padrões adotados

- **Soft delete**: registros nunca são deletados fisicamente
- **Auditoria automática**: `createdAt`, `updatedAt`, `createdBy`, `updatedBy` preenchidos automaticamente
- **UUID** como chave primária
- **PageResponse** padronizado para todas as listagens
- **ApiResponse** envelope em todas as respostas
- **@Transactional(readOnly = true)** por padrão nos services
- **Interface + Impl** para services (facilita mock e troca de implementação)
- **MapStruct** para mapeamento (zero reflexão em runtime)
- **Cache** com `@Cacheable`, `@CachePut`, `@CacheEvict`
- **Validação** com Bean Validation nas camadas de request
- **Erros padronizados** com `ErrorResponse` e campos de validação
