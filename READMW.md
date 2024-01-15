---
runme:
  id: 01HM57FTKAHQ3MNNADSXR5AMPF
  version: v2.2
---

# api-quarkus-social

Este projeto de estudo do Java com Quarkus, foi desenvolvido para a aplicação dos seguintes conteúdos:
( This Java with Quarkus study project was developed to apply the following content):

1. REST API with sub-resources
2. Java 17
3. Quarkus
4. Lombook
5. H2 Memory Database
6. PostgreSQL
7. Junit 5
8. Swagger (API Documentation)
9. Docker
10. Git

> Siga as instruções abaixo para rodar este projeto  
> (Follow the instructions below to run this project)

## local run

`./mvnw compile quarkus:dev`

## docker run

`docker build -f src/main/docker/Dockerfile.jvm -t api-quarkus-social:1.0.0 .`

`docker run -i --rm -p 8080:8080 --name quarkus-social-container api-quarkus-social:1.0.0`

## java run

`java -jar ./target/quarkus-app/quarkus-run.jar`

## Test run

`./mvnw test`

## API Documentation

- localhost:8080/q/swagger-ui

## Git

- https://github.com/thiagosf-dev/api-quarkus-social