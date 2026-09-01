# 🔧 MECANIX — Sistema de Gestão de Oficinas

Sistema web para gestão de oficinas mecânicas: ordens de serviço, clientes, veículos e estoque.
Desenvolvido como Trabalho de Conclusão de Curso (TCC).

---

## 🛠️ Stack

**Backend** — API REST em `http://localhost:8080/api`
- Java 21 · Spring Boot 3.3.5
- Spring Data JPA + Hibernate (`ddl-auto=update`)
- PostgreSQL 16
- Bean Validation · BCrypt (`spring-security-crypto`) · JavaMail (Gmail SMTP)
- Autenticação por **sessão HTTP** (cookie `JSESSIONID`), sem Spring Security web

**Frontend** — SPA em `http://localhost:5173`
- React 19 · TypeScript 5.7 · Vite 8
- React Router 7 · Context API para autenticação
- CSS puro (`src/index.css`), sem framework de UI

---

## 📋 Pré-requisitos

| Ferramenta | Download |
|---|---|
| Java 21 (Temurin) | https://adoptium.net |
| Maven 3.9+ | https://maven.apache.org/download.cgi |
| Node.js 20+ | https://nodejs.org |
| PostgreSQL 16 | https://www.postgresql.org/download |

---

## ⚙️ Configuração

### 1. Clonar e criar o banco

```bash
git clone https://github.com/LuizFelipeHoffmann/mecanix.git
cd mecanix
```

```sql
CREATE DATABASE mecanix_db;
```

### 2. Configurar credenciais

O `application.properties` **não contém senhas** — tudo vem de variáveis de ambiente.
Copie `backend/.env.example` e preencha:

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://127.0.0.1:5432/mecanix_db` | URL do PostgreSQL |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | — | **Obrigatória** |
| `MAIL_USERNAME` | — | Gmail remetente das OS |
| `MAIL_PASSWORD` | — | *App Password* do Gmail (não a senha da conta) |
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` ou `prod` |

No PowerShell:

```powershell
$env:DB_PASSWORD   = "sua_senha"
$env:MAIL_USERNAME = "seu_email@gmail.com"
$env:MAIL_PASSWORD = "sua_app_password"
```

> **Alternativa (dev):** criar `backend/src/main/resources/application-dev.properties` com os valores.
> Esse arquivo é ignorado pelo Git — nunca commite credenciais.

---

## ▶️ Como rodar

### Backend

```bash
cd backend
mvn spring-boot:run
```

Com o perfil `dev`, os usuários de teste são criados no primeiro boot:

```
╔══════════════════════════════════════════╗
║  MECANIX iniciado! (perfil: dev)         ║
║  Login: admin@mecanix.com / admin123     ║
╚══════════════════════════════════════════╝
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Acesse `http://localhost:5173`.

> As origens liberadas no CORS estão em [WebConfig.java](backend/src/main/java/com/mecanix/config/WebConfig.java) — usar outra porta exige ajuste lá.

---

## 👤 Usuários padrão

Criados por [DataInitializer](backend/src/main/java/com/mecanix/config/DataInitializer.java), **apenas no perfil `dev`**.
Em `prod` nenhum usuário é semeado — é preciso inserir o primeiro manualmente (senha em BCrypt).

| E-mail | Senha | Perfil | Acesso |
|---|---|---|---|
| admin@mecanix.com | admin123 | `ADMIN` | Tudo |
| servicos@mecanix.com | serv123 | `SERVICOS` | Dashboard, OS, Clientes, Veículos |
| estoque@mecanix.com | est123 | `ESTOQUE` | Dashboard, Estoque |

Dados de exemplo (clientes, veículos, peças) vêm de [data.sql](backend/src/main/resources/data.sql), idempotente via `ON CONFLICT DO NOTHING`.

---

## 🔐 Autenticação

1. `POST /api/auth/login` valida o hash BCrypt e grava o usuário na `HttpSession` (expira em **8h**).
2. [SessionInterceptor](backend/src/main/java/com/mecanix/config/SessionInterceptor.java) bloqueia todo `/api/**` sem sessão, retornando `401 {"erro":"Não autenticado"}`.
   Exceções: `/api/auth/login` e `/api/auth/logout`.
3. O frontend envia `credentials: 'include'` em toda requisição e, ao receber `401`, limpa a sessão e redireciona para `/login`.

> O controle por perfil é feito **no menu do frontend** ([Layout.tsx](frontend/src/components/Layout.tsx)). O backend valida sessão, não papel — qualquer usuário autenticado alcança qualquer endpoint.

---

## 🔌 API

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/auth/login` | Autentica e abre sessão |
| `POST` | `/api/auth/logout` | Invalida a sessão |
| `GET` | `/api/auth/me` | Usuário da sessão atual |
| `GET` | `/api/dashboard` | Métricas agregadas |
| `GET` `POST` | `/api/clientes` | Lista / cria |
| `GET` `PUT` `DELETE` | `/api/clientes/{id}` | Busca / atualiza / remove |
| `GET` | `/api/veiculos?clienteId=` | Filtra por cliente |
| `GET` `POST` | `/api/veiculos` | Lista / cria |
| `GET` `PUT` `DELETE` | `/api/veiculos/{id}` | Busca / atualiza / remove |
| `GET` | `/api/ordens?status=` | Filtra por status |
| `GET` `POST` | `/api/ordens` | Lista / cria |
| `GET` `PUT` `DELETE` | `/api/ordens/{id}` | Busca / atualiza / remove |
| `POST` | `/api/ordens/{id}/enviar-email` | Envia a OS por e-mail ao cliente |
| `GET` | `/api/estoque/alertas` | Itens abaixo do mínimo |
| `GET` | `/api/estoque?tipo=` | Peças compatíveis com um tipo de veículo |
| `GET` `POST` | `/api/estoque` | Lista / cria |
| `GET` `PUT` `DELETE` | `/api/estoque/{id}` | Busca / atualiza / remove |

Erros seguem um formato único via [GlobalExceptionHandler](backend/src/main/java/com/mecanix/exception/GlobalExceptionHandler.java): `BusinessException` → `400`, `ResourceNotFoundException` → `404`.

---

## 📦 Funcionalidades

- ✅ Login com sessão e três perfis de acesso (ADMIN, SERVICOS, ESTOQUE)
- ✅ Dashboard: OS abertas, concluídas, faturamento, ticket médio, alertas de estoque
- ✅ Ordens de Serviço com itens de serviço (mão de obra) e peças
- ✅ **Baixa automática de estoque** ao lançar peças na OS, com estorno na edição e na exclusão
- ✅ Bloqueio de OS com estoque insuficiente
- ✅ Envio da OS por e-mail em HTML ([EmailService](backend/src/main/java/com/mecanix/service/EmailService.java))
- ✅ Clientes com CPF único e validação de formato
- ✅ Veículos com placa única, tipo (Sedan/Hatch/SUV/Pickup/Elétrico) e autocomplete de marca/modelo
- ✅ Estoque com código único, quantidade mínima e compatibilidade por tipo de veículo
- ✅ Relatórios com faturamento, ticket médio e ranking de serviços
- ✅ Impressão de OS e relatórios (`window.print()`)
- ✅ Badges de pendências na sidebar (OS abertas, alertas de estoque)
- ✅ Layout responsivo (mobile e desktop)

### Status de OS

`ANDAMENTO` · `AGUARDANDO` (peça) · `AGENDADO` · `CONCLUIDO` · `CANCELADO`

---

## 📁 Estrutura

```
mecanix/
├── backend/
│   ├── .env.example
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/mecanix/
│       │   ├── config/        # CORS, SessionInterceptor, DataInitializer (dev)
│       │   ├── controller/    # Auth, Cliente, Veiculo, Ordem, Estoque, Dashboard
│       │   ├── dto/           # Request/Response da API
│       │   ├── exception/     # BusinessException, ResourceNotFound, handler global
│       │   ├── model/         # Cliente, Veiculo, OrdemServico, ItemServico,
│       │   │                  #   ItemPeca, EstoqueItem, Usuario
│       │   ├── repository/    # Spring Data JPA
│       │   └── service/       # Regras de negócio + EmailService
│       └── resources/
│           ├── application.properties      # base, lê variáveis de ambiente
│           ├── application-dev.properties  # local, fora do Git
│           └── data.sql                    # dados de exemplo
└── frontend/
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── api/index.ts        # cliente HTTP, tipos, máscaras e helpers
        ├── context/AuthContext.tsx
        ├── components/Layout.tsx   # sidebar com filtro por perfil
        ├── data/marcas.ts          # catálogo marca/modelo
        ├── pages/                  # Login, Dashboard, OS, Clientes,
        │                           #   Veiculos, Estoque, Relatorios
        ├── index.css
        ├── App.tsx                 # rotas + PrivateRoute
        └── main.tsx
```

---

## 🏗️ Build de produção

```bash
cd backend  && mvn clean package     # → target/mecanix-api-1.0.0.jar
cd frontend && npm run build         # → dist/
```

```bash
SPRING_PROFILES_ACTIVE=prod java -jar backend/target/mecanix-api-1.0.0.jar
```

Antes de publicar, ajuste `API_BASE` em [frontend/src/api/index.ts](frontend/src/api/index.ts) e as origens de CORS em [WebConfig.java](backend/src/main/java/com/mecanix/config/WebConfig.java).

---

## 👨‍💻 Autor

**Luiz Felipe Hoffmann Kuklik**
TCC — Sistema de Gestão de Oficinas Mecânicas
