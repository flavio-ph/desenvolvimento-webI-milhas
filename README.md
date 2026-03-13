# Milhas Web API — Backend

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

API REST para gerenciamento de acumulacao de pontos e milhas de cartoes de credito, desenvolvida com Java 21 e Spring Boot 3.

---

## Sobre o Projeto

Sistema desenvolvido como projeto academico na disciplina de Projeto Web I do Instituto Federal de Sergipe (IFS). A aplicacao permite o cadastro de cartoes com diferentes bandeiras e programas de fidelidade, o registro de compras, o calculo automatico de pontos com base no fator de conversao do cartao, a participacao em promocoes bonificadas e o acompanhamento de movimentacoes e saldos.

Este repositorio contem exclusivamente o back-end (API REST).

Repositorio front-end: [Projeto-Web-1-FrontEnd](https://github.com/flavio-ph/desenvolvimento-web-milhas-interface.git)

---

## Funcionalidades

- Autenticacao com JWT, recuperacao de senha por e-mail e suporte a autenticacao de dois fatores (2FA)
- Gerenciamento de cartoes com bandeiras e programas de pontos associados
- Registro de compras e calculo automatico de pontos com base no fator de conversao
- Deteccao de promocoes ativas e aplicacao de bonus de pontuacao
- Agendamento automatico para credito de pontos pendentes e alertas de expiracao
- Relatorios de movimentacoes em PDF e CSV
- Dashboard consolidado com saldo por cartao, prazo medio de recebimento e historico mensal
- Documentacao interativa via Swagger UI

---

## Tecnologias Utilizadas

| Camada         | Tecnologia                          |
|----------------|-------------------------------------|
| Linguagem      | Java 21                             |
| Framework      | Spring Boot 3.x                     |
| Seguranca      | Spring Security + JJWT 0.11.5       |
| Persistencia   | Spring Data JPA / Hibernate         |
| Banco de Dados | PostgreSQL 15                       |
| Migracao       | Flyway                              |
| Mapeamento     | MapStruct 1.5.5                     |
| Build          | Maven                               |
| Containerizacao| Docker / Docker Compose             |

---

## Estrutura do Projeto

```
src/
└── main/
    ├── java/com/web/milhas/
    │   ├── config/         # Configuracoes globais (CORS, seguranca, Swagger, rate limit)
    │   ├── controller/     # Endpoints REST
    │   ├── dto/            # Records de transferencia de dados (request/response)
    │   ├── entity/         # Entidades JPA mapeadas para o banco
    │   │   └── enums/      # Enumeracoes do dominio
    │   ├── repository/     # Interfaces Spring Data JPA
    │   ├── service/        # Regras de negocio
    │   └── security/       # Filtros e configuracoes JWT
    └── resources/
        ├── application.properties
        └── db/migration/   # Scripts Flyway
```

---

## Diagrama Entidade-Relacionamento

```mermaid
erDiagram
    USUARIO {
        bigint id PK
        string nome
        string email
        string senha
        string telefone
        string cpf
        datetime data_cadastro
        string foto_perfil
        string role
        boolean two_factor_enabled
        string verification_code
        datetime verification_code_expiry
        string reset_password_token
        datetime reset_token_expiry
    }

    BANDEIRA {
        bigint id PK
        string nome
        string status
        string cor
    }

    PROGRAMA_PONTOS {
        bigint id PK
        string nome
    }

    CARTAO {
        bigint id PK
        string nome_personalizado
        string ultimos_digitos
        decimal fator_conversao
        string cor
        bigint usuario_id FK
        bigint bandeira_id FK
        bigint programa_pontos_id FK
    }

    COMPRA {
        bigint id PK
        string descricao
        decimal valor_gasto
        decimal pontos_calculados
        date data_compra
        date data_credito_prevista
        string status
        bigint cartao_id FK
    }

    COMPROVANTE_COMPRA {
        bigint id PK
        string nome_arquivo
        string tipo_arquivo
        string url_arquivo
        bigint compra_id FK
    }

    MOVIMENTACAO_PONTOS {
        bigint id PK
        string tipo
        decimal quantidade_pontos
        datetime data_movimentacao
        date data_validade
        string descricao
        bigint saldo_pontos_id FK
        bigint compra_id FK
    }

    SALDO_PONTOS {
        bigint id PK
        decimal total_pontos
        bigint usuario_id FK
        bigint programa_pontos_id FK
    }

    PROMOCAO {
        bigint id PK
        string titulo
        string descricao
        string url_promocao
        date data_inicio
        date data_fim
        decimal bonus_porcentagem
        bigint programa_pontos_id FK
    }

    PARTICIPACAO_PROMOCAO {
        bigint id PK
        datetime data_adesao
        bigint usuario_id FK
        bigint promocao_id FK
    }

    NOTIFICACAO {
        bigint id PK
        string mensagem
        boolean lida
        string tipo
        datetime data_envio
        bigint usuario_id FK
        bigint compra_id FK
    }

    USUARIO ||--o{ CARTAO : possui
    USUARIO ||--o{ SALDO_PONTOS : acumula
    USUARIO ||--o{ NOTIFICACAO : recebe
    USUARIO ||--o{ PARTICIPACAO_PROMOCAO : participa
    BANDEIRA ||--o{ CARTAO : classifica
    PROGRAMA_PONTOS ||--o{ CARTAO : vincula
    PROGRAMA_PONTOS ||--o{ SALDO_PONTOS : agrupa
    PROGRAMA_PONTOS ||--o{ PROMOCAO : oferece
    CARTAO ||--o{ COMPRA : registra
    COMPRA ||--o{ COMPROVANTE_COMPRA : anexa
    COMPRA ||--o{ MOVIMENTACAO_PONTOS : gera
    COMPRA ||--o{ NOTIFICACAO : referencia
    SALDO_PONTOS ||--o{ MOVIMENTACAO_PONTOS : historico
    PROMOCAO ||--o{ PARTICIPACAO_PROMOCAO : registra
```

---

## Prerequisitos

- JDK 21
- Maven 3.8+
- PostgreSQL 15+
- Docker (opcional)

---

## Configuracao e Execucao

### 1. Clone o repositorio

```bash
git clone https://github.com/seu-usuario/desenvolvimento-webI-milhas.git
cd desenvolvimento-webI-milhas
```

### 2. Configure as variaveis de ambiente

As variaveis abaixo devem ser definidas no sistema operacional ou em um arquivo `.env`:

| Variavel                   | Descricao                        |
|----------------------------|----------------------------------|
| `SPRING_DATASOURCE_URL`    | URL JDBC do banco PostgreSQL     |
| `SPRING_DATASOURCE_USERNAME` | Usuario do banco               |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco                 |
| `JWT_SECRET`               | Chave secreta para tokens JWT    |

### 3. Execute a aplicacao

Via Maven:

```bash
./mvnw spring-boot:run
```

Via Docker Compose:

```bash
docker compose up --build
```

A API estara disponivel em `http://localhost:8080`.

---

## Documentacao da API

Com a aplicacao em execucao, acesse:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Endpoints Principais

| Metodo | Rota                        | Descricao                          |
|--------|-----------------------------|------------------------------------|
| POST   | /auth/login                 | Autenticacao do usuario            |
| POST   | /auth/register              | Cadastro de novo usuario           |
| PUT    | /usuarios/{id}              | Atualizar perfil                   |
| GET    | /cartoes                    | Listar cartoes do usuario          |
| POST   | /cartoes                    | Cadastrar cartao                   |
| GET    | /compras                    | Listar compras                     |
| POST   | /compras                    | Registrar compra                   |
| GET    | /movimentacoes              | Historico de movimentacoes         |
| GET    | /saldo                      | Consultar saldo por programa       |
| GET    | /promocoes                  | Listar promocoes ativas            |
| GET    | /dashboard                  | Dados consolidados do dashboard    |
| GET    | /relatorios/extrato         | Extrato em PDF ou CSV              |

---

## Testes

```bash
./mvnw test
```

---

## Sobre

Projeto desenvolvido para a disciplina de Projeto Web I no Instituto Federal de Sergipe (IFS), com foco em boas praticas de API REST, seguranca com JWT e arquitetura em camadas.
