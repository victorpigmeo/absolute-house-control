# Backend Spec

## Tech stack
- Language/framework: Java, Spring Boot 4
- Build tool: Gradle
- Database: PostgreSQL
- Auth provider: Keycloak
- Architecture style: microservices
- Deployment target: Kubernetes (Talos OS)

## Confirmed decisions
- **Service communication**: event-driven (message broker) for asynchronous events/state changes, plain REST for synchronous service-to-service and client-facing calls. The specific broker (e.g. Kafka, RabbitMQ) is not yet chosen.

## Open / not yet defined
- Concrete domain microservices (no feature scope has been defined yet)
- Message broker choice
- Database-per-service vs. shared-instance/schema-per-service strategy
- API gateway approach (e.g. Spring Cloud Gateway vs. plain Kubernetes Ingress)
- Service discovery / configuration management approach
- Keycloak realm/client structure
- Gradle module layout for individual services
