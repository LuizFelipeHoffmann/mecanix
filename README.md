# 🔧 MECANIX — Sistema de Gestão de Oficinas

Sistema web completo para gestão de oficinas mecânicas, desenvolvido como Trabalho de Conclusão de Curso (TCC).

---

## 🛠️ Tecnologias utilizadas

**Backend**
- Java 21
- Spring Boot 3.3.5
- Spring Data JPA + Hibernate
- PostgreSQL 16
- JavaMail (envio de e-mails)

**Frontend**
- HTML5, CSS3, JavaScript puro
- Live Server (VS Code)

---

## 📋 Pré-requisitos

Antes de rodar o projeto, instale:

| Ferramenta | Download |
|---|---|
| Java 21 (Temurin) | https://adoptium.net |
| Maven 3.9+ | https://maven.apache.org/download.cgi |
| PostgreSQL 16 | https://www.postgresql.org/download |
| VS Code | https://code.visualstudio.com |
| Extensão Live Server | Instalar no VS Code |

---

## ⚙️ Como configurar

### 1. Clonar o repositório
```bash
git clone https://github.com/LuizFelipeHoffmann/mecanix.git
cd mecanix
```

### 2. Criar o banco de dados
Abra o pgAdmin ou psql e execute:
```sql
CREATE DATABASE mecanix_db;
```

### 3. Configurar as credenciais
Edite o arquivo `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/mecanix_db
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
```

---

## ▶️ Como rodar

### Backend (terminal como Administrador)
```powershell
cd backend
mvn spring-boot:run
```

Aguarde aparecer:
```
╔══════════════════════════════════════════╗
║  MECANIX iniciado!                       ║
║  Login: admin@mecanix.com / admin123     ║
╚══════════════════════════════════════════╝
```

### Frontend
No VS Code, clique com botão direito em `frontend/index.html` → **Open with Live Server**

Acesse: `http://localhost:5500`

---

## 👤 Usuários padrão

| E-mail | Senha | Perfil |
|---|---|---|
| admin@mecanix.com | admin123 | Acesso total |
| servicos@mecanix.com | serv123 | Atendimento |
| estoque@mecanix.com | est123 | Estoque |

---

## 📦 Funcionalidades

- ✅ Login com perfis de acesso (Admin, Serviços, Estoque)
- ✅ Dashboard com métricas em tempo real
- ✅ Ordens de Serviço com serviços e peças
- ✅ Mudança rápida de status das OS
- ✅ Envio de OS por e-mail ao cliente
- ✅ Cadastro de Clientes com validação de CPF
- ✅ Cadastro de Veículos com autocomplete de marca/modelo
- ✅ Controle de Estoque com alertas de mínimo
- ✅ Relatórios com faturamento e serviços mais realizados
- ✅ Impressão de OS
- ✅ Layout responsivo (mobile e desktop)

---

## 📁 Estrutura do projeto

```
mecanix/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/mecanix/
│       │   ├── config/        # CORS, Sessão, DataInitializer
│       │   ├── controller/    # Endpoints da API REST
│       │   ├── dto/           # Objetos de transferência
│       │   ├── exception/     # Tratamento de erros
│       │   ├── model/         # Entidades JPA
│       │   ├── repository/    # Interfaces Spring Data
│       │   └── service/       # Regras de negócio
│       └── resources/
│           ├── application.properties
│           └── data.sql       # Dados iniciais
└── frontend/
    ├── css/style.css
    ├── js/
    │   ├── api.js             # Comunicação com a API
    │   ├── marcas.js          # Autocomplete de marcas/modelos
    │   └── sidebar.js         # Sidebar compartilhada
    ├── index.html             # Login
    ├── dashboard.html
    ├── os.html                # Ordens de Serviço
    ├── clientes.html
    ├── veiculos.html
    ├── estoque.html
    └── relatorios.html
```

---

## 👨‍💻 Autor

**Luiz Felipe Hoffmann Kuklik**  
TCC — Sistema de Gestão de Oficinas Mecânicas
