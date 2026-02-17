Milhas Web API
Sistema robusto para gestão de acumulação de pontos e milhas de cartões de crédito. A aplicação permite o registo de compras, cálculo automático de pontos com base no fator de conversão do cartão, gestão de promoções bonificadas e monitorização de prazos de expiração.

Funcionalidades Principais
Gestão de Cartões: Registo de cartões com diferentes bandeiras e programas de fidelidade.

Cálculo Automático de Pontos: Processamento imediato da pontuação baseada no valor gasto e fator de conversão do cartão.

Promoções Bonificadas: Lógica para detetar participações ativas e aplicar bónus caso a compra ocorra no período da campanha.

Agendamento Automático: Schedulers para creditar pontos pendentes e enviar alertas de expiração de forma autónoma.

Segurança Avançada: Autenticação JWT, recuperação de palavra-passe e suporte para Autenticação de Dois Fatores (2FA).

Relatórios: Geração de extratos de movimentações em formatos PDF e CSV.

Dashboard: Visão consolidada com pontos por cartão, prazo médio de recebimento e histórico mensal.

Tecnologias Utilizadas
Java 21 com Spring Boot 3.5.6.

Spring Security com jjwt 0.11.5 para autenticação stateless.

JPA Hibernate com base de dados PostgreSQL.

Flyway para gestão de migrações de base de dados.

MapStruct 1.5.5.Final para mapeamento performático de objetos.

Como Executar
Pré-requisitos: JDK 21, Maven 3.8 e PostgreSQL 15.

Configuração: Configure as variáveis de ambiente no application.properties ou no seu sistema operacional para SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD e JWT_SECRET.

Compilação: Execute o comando mvn clean install para compilar e gerar as classes do MapStruct.

Execução: Inicie a aplicação com o comando mvn spring-boot:run.

Documentação da API
Após iniciar a aplicação, a documentação interativa pode ser acedida nos seguintes endereços:

Swagger UI: http://localhost:8080/swagger-ui.html.

Docs JSON: http://localhost:8080/v3/api-docs.

Estrutura do Projeto
Config: Configurações globais de segurança, CORS e recursos.

Controller: Endpoints REST para comunicação com o frontend.

Dto: Records Java para transferência segura de dados.

Entity: Modelos de dados mapeados para as tabelas do PostgreSQL.

Service: Implementação das regras de negócio e integração entre componentes.
