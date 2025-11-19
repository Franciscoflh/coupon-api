#  Coupon API 

Este projeto foi desenvolvido como solução para um **desafio técnico **, utilizando **Spring Boot**, **H2**, **JPA**, **Bean Validation** e **JUnit**.

A aplicação implementa criação, consulta e exclusão lógica (soft delete) de cupons, seguindo todas as regras de negócio fornecidas.

---

##  Tecnologias Utilizadas

- Java 17+
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- H2 Database (in-memory)
- JUnit 5 + MockMvc
- Maven

---

##  Como executar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/Franciscoflh/coupon-api.git
cd coupon-api
```

### 2. Rodar o projeto

```bash
mvn spring-boot:run
```

A aplicação estará disponível em:

```
http://localhost:8080
```

---

##  H2 Database

Acesse o console:

```
http://localhost:8080/h2-console
```

Configurações:

```
JDBC URL: jdbc:h2:mem:couponsdb
User: sa
Password: (vazio)
```

Você pode consultar os cupons:

```sql
SELECT * FROM coupons;
```

---

##  Como rodar os testes

```bash
mvn clean test
```

Os testes cobrem:

- Criação de cupom
- Sanitização do código
- Validação de desconto
- Data mínima de expiração
- Soft delete
- Impedir delete duplicado
- Não retornar cupom deletado

---

#  Endpoints da API

Base URL: `http://localhost:8080/coupon`

---

##  Criar cupom

**POST /coupon**

### Request body:

```json
{
  "code": "ABC-123",
  "description": "Cupom de exemplo",
  "discountValue": 10.0,
  "expirationDate": "2030-01-01T00:00:00",
  "published": true
}
```

### Regras aplicadas:

- Código deve ser **alfanumérico**
- Caracteres especiais são removidos
- Código final deve ter **6 caracteres**
- Desconto mínimo: **0.5**
- Data de expiração não pode estar no passado
- Cupom pode ser criado como publicado

---

##  Buscar cupom por ID

**GET /coupon/{id}**

Retorna o cupom caso ele **não tenha sido deletado**.

---

## 🗑️ Deletar cupom (Soft Delete)

**DELETE /coupon/{id}**

Regras:

- Soft delete → o cupom continua no banco
- Campo `deleted` = true
- `status` = DELETED
- Deletar um cupom já deletado = **400 Bad Request**

---

#  Regras de Negócio Implementadas

### ✔ Create
- Campos obrigatórios:
  - `code`
  - `description`
  - `discountValue`
  - `expirationDate`
- Código sanitizado e limitado a **6 caracteres**
- Desconto mínimo de **0.5**
- Expiração não pode estar no passado
- Pode ser criado como publicado

### ✔ Delete
- Soft delete (não remove do banco)
- Cupom deletado não pode ser deletado novamente
- Cupom deletado não aparece no GET

---

#  Estrutura do Projeto

```
src/main/java/com.coupon.coupon_api
│
├── api
│   ├── dto
│   │   ├── CouponRequest.java
│   │   └── CouponResponse.java
│   ├── handler
│   │   └── GlobalExceptionHandler.java
│   └── CouponController.java
│
├── domain
│   ├── Coupon.java
│   ├── CouponRepository.java
│   └── CouponStatus.java
│
├── service
│   ├── exception
│   │   ├── BusinessException.java
│   │   └── CouponNotFoundException.java
│   └── CouponService.java
│
└── CouponApiApplication.java
```

---

#  Estrutura dos Testes

```
src/test/java/com.coupon.coupon_api
└── CouponControllerTest.java
```

---

#  Decisões de Arquitetura

- Divisão em camadas: Controller → Service → Repository → Domain
- DTOs para comunicação limpa na API
- GlobalExceptionHandler para respostas padronizadas
- Sanitização do código no Service
- Soft delete preservando histórico no banco
- Uso de MockMvc para testes realistas da API

