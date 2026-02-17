Milhas Web API 💳✈️
Sistema robusto para gestão de acumulação de pontos e milhas de cartões de crédito. A aplicação permite o registo de compras, cálculo automático de pontos com base no fator de conversão do cartão, gestão de promoções bonificadas e monitorização de prazos de expiração.

✨ Funcionalidades Principais
Gestão de Cartões: Registo de cartões com diferentes bandeiras (Visa, Mastercard, etc.) e programas de fidelidade (Livelo, Esfera, etc.).

Cálculo Automático de Pontos: Processamento imediato da pontuação baseada no valor gasto e fator de conversão do cartão.

Promoções Bonificadas: Lógica para detetar promoções ativas e aplicar bónus (ex: +100% de pontos) caso a compra ocorra no período da campanha.

Agendamento Automático (Schedulers): Processamento em background para creditar pontos pendentes e enviar alertas de expiração.

Segurança Avançada: Autenticação JWT, recuperação de palavra-passe via token e suporte para Autenticação de Dois Fatores (2FA).

Relatórios: Geração de extratos de movimentações em formatos PDF e CSV.

Dashboard: Visão consolidada com pontos por cartão, prazo médio de recebimento e histórico mensal.

🛠️ Tecnologias Utilizadas
Java 21 (com funcionalidades de Preview).

Spring Boot 3.5.6.

Spring Security & JWT (jjwt 0.11.5).

JPA / Hibernate com base de dados PostgreSQL.

Flyway: Gestão de migrações de base de dados.

MapStruct 1.5.5.Final: Mapeamento performático entre Entidades e DTOs.

Lombok: Redução de código boilerplate.

OpenAPI / Swagger: Documentação interativa da API.

🚀 Como Executar
Pré-requisitos
JDK 21

Maven 3.8+

PostgreSQL 15+

Configuração
Clone o repositório.

Configure as variáveis de ambiente no seu application.properties ou sistema:

Properties
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_base64
Execute o comando para compilar e gerar os mappers:

Bash
mvn clean install
Inicie a aplicação:

Bash
mvn spring-boot:run
📖 Documentação da API
Após iniciar a aplicação, pode aceder à interface do Swagger para testar os endpoints:

Swagger UI: http://localhost:8080/swagger-ui.html

Docs JSON: http://localhost:8080/v3/api-docs

🏗️ Estrutura do Projeto
config/: Configurações globais (Segurança, CORS, OpenAPI).

controller/: Endpoints REST da aplicação.

dto/: Objetos de transferência de dados (Records).

entity/: Modelos persistentes de base de dados.

mapper/: Interfaces MapStruct para conversão de objetos.

repository/: Interfaces Spring Data JPA para acesso a dados.

service/: Lógica de negócio e integrações.
