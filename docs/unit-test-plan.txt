# SuperDive Backend — Unit Test Documentation

**Project:** `com.example.superdive.backend` (Spring Boot 3.4.2, Java 21)
**Document version:** 1.0
**Date:** 2026-07-30

---

## 1. Purpose and Scope

This document specifies the unit test cases for the SuperDive CRM backend. It covers every
class in the backend that contains branching logic or data mapping:

| Layer | Classes in scope |
|---|---|
| Service | `CustomerService`, `ProductService`, `OrdersService`, `ReferenceService`, `DivingDataService`, `UserService` |
| Security | `JwtService`, `CustomUserDetailsService`, `AuthenticatedUserProvider` |
| Controller | `AuthController`, `CustomerController`, `ProductController`, `OrdersController`, `ReferenceController`, `DivingDataController` |

**Out of scope.** Repository interfaces (`CustomerRepository`, `OrdersRepository`, …) are
Spring Data JPA interfaces with no hand-written implementation; their derived queries and
`@Query` statements need an integration test with a real or embedded database, not a unit
test. Entities and DTOs are plain getter/setter holders. `SecurityConfig`, `CorsConfig` and
`JwtAuthenticationFilter` are wiring and are better covered by a `@SpringBootTest` slice.

---

## 2. Test Approach

**Level:** Unit / component. Every collaborator is replaced with a Mockito mock; no Spring
context and no database is started.

**Frameworks** (all supplied transitively by `spring-boot-starter-test`):

| Tool | Use |
|---|---|
| JUnit 5 (Jupiter) | Test runner, `@Test`, `@BeforeEach` |
| Mockito 5 + `MockitoExtension` | `@Mock`, `@InjectMocks`, stubbing, verification |
| AssertJ | Fluent assertions (`assertThat`, `assertThatThrownBy`) |
| Spring MockMvc (`standaloneSetup`) | Controller tests without a servlet container |
| Jackson `ObjectMapper` | Request-body serialisation in controller tests |
| `ReflectionTestUtils` | Injecting `@Value` fields and field-injected dependencies |

**Conventions**

- One test class per production class: `<ClassName>Test` in the mirrored package under `src/test/java`.
- Test method naming: `methodUnderTest_expectedBehaviour_whenCondition`.
- Mockito runs in **strict stubs** mode by default. Every `when(...)` must be exercised by the
  test, or the test fails with `UnnecessaryStubbingException`. Stub only what the code path uses.
- Injection style must match the production class:
  - constructor injection (`CustomerService`, `ProductService`, `UserService`,
    `CustomUserDetailsService`, `AuthenticatedUserProvider`) → `@InjectMocks` or manual `new`;
  - field injection (`OrdersService`, `ReferenceService`, `DivingDataService`,
    `DivingDataController`) → `@InjectMocks` performs field injection; for controllers built by
    hand, use `ReflectionTestUtils.setField(...)`.

**Entry / exit criteria**

- *Entry:* the module compiles (`mvn -q compile`).
- *Exit:* all listed cases implemented and green; no `UnnecessaryStubbingException`; every
  defect in §7 either fixed or accepted with the corresponding test asserting current behaviour.

**How to run**

```bash
cd superdive-backend
mvn test                                  # whole suite
mvn test -Dtest=CustomerServiceTest       # one class
mvn test -Dtest='*ServiceTest'            # the service layer
```

---

## 3. Current Coverage Status

| Test class | Status | Cases |
|---|---|---|
| `service/UserServiceTest` | Implemented | 6 |
| `service/CustomerServiceTest` | Implemented | 20 |
| `controller/AuthControllerTest` | Implemented | 4 |
| `service/ProductServiceTest` | **Not written** | 13 specified |
| `service/OrdersServiceTest` | **Not written** | 17 specified |
| `service/ReferenceServiceTest` | **Not written** | 4 specified |
| `service/DivingDataServiceTest` | **Not written** | 2 specified |
| `security/JwtServiceTest` | **Not written** | 5 specified |
| `security/CustomUserDetailsServiceTest` | **Not written** | 3 specified |
| `security/AuthenticatedUserProviderTest` | **Not written** | 5 specified |
| `controller/CustomerControllerTest` | **Not written** | 7 specified |
| `controller/ProductControllerTest` | **Not written** | 4 specified |
| `controller/OrdersControllerTest` | **Not written** | 9 specified |
| `controller/ReferenceControllerTest` | **Not written** | 2 specified |
| `controller/DivingDataControllerTest` | **Not written** | 2 specified |

`BackendApplicationTests` exists but is a context-load smoke test, not a unit test.

---

## 4. Service Layer Test Cases

### 4.1 `CustomerService` — `CustomerServiceTest`

Mocks: `CustomerRepository`, `AuthenticatedUserProvider`.

**Shared fixture.** A valid `CustomerDTO` must always have `divingData` set to at least an
empty list — `createCustomer` streams it without a null check (see DEF-02).

| ID | Method | Scenario | Setup / stubs | Expected result |
|---|---|---|---|---|
| CS-01 | `createCustomer` | Happy path | `findByName` → empty; `save` echoes arg; `getCurrentUser` → user | Returns customer with name, phoneNum, dob, diver copied from DTO; `user` set from provider; `save` called once |
| CS-02 | `createCustomer` | `isDiver = false` | as CS-01 | `customer.diver()` is `false` |
| CS-03 | `createCustomer` | One `DivingDataDTO` supplied | as CS-01 | 1 `DivingData` with agencyName / level / reference mapped; its `customer` back-reference is the saved customer |
| CS-04 | `createCustomer` | One `ReferenceDTO` supplied | as CS-01 | 1 `Reference` with name and phone mapped; back-reference set |
| CS-05 | `createCustomer` | `reference` list is `null` | as CS-01 | No exception; `getReferences()` empty (guarded by `Optional.ofNullable`) |
| CS-06 | `createCustomer` | Name already taken | `findByName` → one existing customer | Throws `CustomerAlreadyExistException` containing the name; `save` **never** called |
| CS-07 | `createCustomer` | `divingData` is `null` | as CS-01 | **Currently throws `NullPointerException`** — see DEF-02. Test asserts current behaviour until fixed |
| CS-08 | `findCustomerByNameAndPhoneNum` | Two matches returned | repo returns list of 2 | Returns the **first** element |
| CS-09 | `findCustomerByNameAndPhoneNum` | No match | repo returns empty list | Throws `MessageErrorException` whose message contains both name and phone number |
| CS-10 | `getCustomerById` | Found | `findById` → present | Returns that customer instance |
| CS-11 | `getCustomerById` | Not found | `findById` → empty | Throws `RuntimeException("Customer not found")` |
| CS-12 | `getAllCustomerByPagination` | Delegation | `findAll(pageable)` → page | Returns the same `Page` instance |
| CS-13 | `getAllCustomer` | Delegation | `findAll()` → list | Returns the same list |
| CS-14 | `deleteCustomer` | Delegation | — | `customerRepository.deleteById(5L)` invoked |
| CS-15 | `updateCustomer` | Scalar fields | `findById` → existing; `save` echoes | name / phoneNum / dob / diver overwritten from DTO |
| CS-16 | `updateCustomer` | Child collections replaced | existing has 1 stale diving + 1 stale reference | The **same `List` instances** are reused (cleared and refilled), not swapped — required for Hibernate `orphanRemoval`; new children carry the correct back-reference |
| CS-17 | `updateCustomer` | DTO collections `null` | existing has children | Both collections end up empty |
| CS-18 | `updateCustomer` | Customer missing | `findById` → empty | Throws `RuntimeException("Customer not found")`; `save` never called |
| CS-19 | `updateCustomer` | Saves managed entity | `findById` → existing | `ArgumentCaptor` confirms the instance passed to `save` is the one loaded from the repository, not a new object |
| CS-20 | `createCustomer` | Ordering | `getCurrentUser` throws | Exception propagates; `save` never called |

### 4.2 `ProductService` — `ProductServiceTest`

Mocks: `ProductRepository`.

| ID | Method | Scenario | Setup / stubs | Expected result |
|---|---|---|---|---|
| PS-01 | `createProduct` | Happy path | `save` echoes arg | `Product` with name, type, details, price mapped from DTO; `save` called once |
| PS-02 | `createProduct` | `details` is `null` | `save` echoes arg | Accepted — `details` is not validated; saved product has `details == null` |
| PS-03 | `createProduct` | DTO is `null` | — | `MessageErrorException("Product data is required")`; `save` never called |
| PS-04 | `createProduct` | `name` is `null` | — | `MessageErrorException("Product name is required")`; `save` never called |
| PS-05 | `createProduct` | `type` is `null` | — | `MessageErrorException("Product type is required")`; `save` never called |
| PS-06 | `createProduct` | `price` is `null` | — | `MessageErrorException("Product price is required")`; `save` never called |
| PS-07 | `createProduct` | Blank name `""` | `save` echoes arg | **Currently accepted** — only `null` is rejected. See DEF-06 |
| PS-08 | `getProductsByType` | Valid enum name `"Retail"` | `findByType(Retail)` → list | Returns that list |
| PS-09 | `getProductsByType` | Unknown value `"NotAType"` | — | Throws `IllegalArgumentException` from `ProductType.valueOf` (uncaught → HTTP 500) |
| PS-10 | `getProductsByType` | Wrong case `"retail"` | — | Throws `IllegalArgumentException` — enum constants are capitalised (`Retail`, `Course`, `Trip`, `Service`) |
| PS-11 | `getProductById` | Found | `findById` → present | Returns that product |
| PS-12 | `getProductById` | Not found | `findById` → empty | Throws `RuntimeException("Product not found")` |
| PS-13 | `getAll` | Delegation | `findAll()` → list | Returns the same list |

### 4.3 `OrdersService` — `OrdersServiceTest`

Mocks: `CustomerService`, `ProductService`, `OrdersRepository`, `AuthenticatedUserProvider`.
All four are **field-injected** in production, so `@InjectMocks` uses field injection.

| ID | Method | Scenario | Setup / stubs | Expected result |
|---|---|---|---|---|
| OS-01 | `createOrders` | `ordersItems` is `null` | — | `MessageErrorException("orders must contain at least one item.")`; no repository interaction |
| OS-02 | `createOrders` | `ordersItems` is empty | — | Same exception as OS-01 |
| OS-03 | `createOrders` | Customer not found | `findCustomerByNameAndPhoneNum` throws `MessageErrorException` | Exception propagates unchanged; `ordersRepo.save` never called |
| OS-04 | `createOrders` | Item qty `null` | customer + `getCurrentUser` stubbed | `MessageErrorException("Invalid quantity for product")` |
| OS-05 | `createOrders` | Item qty `0` | as OS-04 | Same exception |
| OS-06 | `createOrders` | Item qty negative | as OS-04 | Same exception |
| OS-07 | `createOrders` | Happy path, 2 items | customer, user, `prodService.createProduct` → products, `save` echoes | Order has 2 `OrdersItem`s; each `item.getorders()` points back at the order |
| OS-08 | `createOrders` | Total calculation | items 2 × 100.00 and 1 × 50.00 | `totalPrice` equals `250.00` (`calculateTotal` = Σ price × qty) |
| OS-09 | `createOrders` | Metadata stamped | as OS-07 | `ordersDate` non-null and ≈ now; `user` is the value from `AuthenticatedUserProvider`; `customer` is the looked-up customer |
| OS-10 | `addProductToOrders` | Order not found | `findByIdWithItemsAndProducts` → empty | `MessageErrorException("orders not found with id: 1")` |
| OS-11 | `addProductToOrders` | Product not in order yet | order with no items; `getProductById` → product | A new `OrdersItem` is appended with qty and price from the DTO |
| OS-12 | `addProductToOrders` | Product already in order | order already holds product #1 with qty 2; DTO qty 3 | Item count stays 1; qty becomes 5; no new item created |
| OS-13 | `addProductToOrders` | Total recalculated | as OS-12 | `totalPrice` reflects the merged quantity |
| OS-14 | `getOrdersById` | Found / not found | `findById` present / empty | Returns order / throws `MessageErrorException("orders not found")` |
| OS-15 | `getAllOrders` | Delegation | `findAll()` → list | Returns the same list |
| OS-16 | `getCustomerOrdersHistoryDTO` | No orders | customer found; `findByCustomerIdWithItemsAndProducts` → empty | `MessageErrorException("No orderss found for this customer")` |
| OS-17 | `getCustomerOrdersHistoryDTO` | Happy path | customer + 2 orders with items | DTO carries customerId, name, phone; `totalPrice` = Σ order totals; `totalItems` = **order count** (see DEF-07); each item summary has `subtotal = price × quantity` and product name/type/details copied; a `null` product yields `"Unknown"` / `BigDecimal.ZERO` fallbacks |

### 4.4 `ReferenceService` — `ReferenceServiceTest`

Mocks: `ReferenceRepository`.

| ID | Method | Scenario | Setup / stubs | Expected result |
|---|---|---|---|---|
| RS-01 | `addReference` | Happy path | `save` echoes arg | `Reference` with `referenceName` and `referencePhoneNum` mapped; `save` called once |
| RS-02 | `addReference` | `referenceName` is `null` | — | `MessageErrorException("Reference name cannot be empty.")`; `save` never called |
| RS-03 | `addReference` | `referenceName` is `""` | — | Same exception; `save` never called |
| RS-04 | `addReference` | Customer association | `save` echoes arg | `reference.getCustomer()` is `null` — the service never links the reference to a customer (see DEF-08) |

### 4.5 `DivingDataService` — `DivingDataServiceTest`

Mocks: `DivingDataRepository`.

| ID | Method | Scenario | Setup / stubs | Expected result |
|---|---|---|---|---|
| DS-01 | `saveDivingData` | Delegation | `save(entity)` → saved | Returns exactly what the repository returned |
| DS-02 | `saveDivingData` | Pass-through | — | `divingDataRepo.save` called once with the same instance; no validation or mutation performed |

### 4.6 `UserService` — `UserServiceTest` *(already implemented)*

Mocks: `UserRepository`, `PasswordEncoder`, `JwtService`, `CustomUserDetailsService`.

| ID | Method | Scenario | Expected result |
|---|---|---|---|
| US-01 | `register` | Email free | Saves user; response carries id, firstname, lastname, email |
| US-02 | `register` | Password handling | `passwordEncoder.encode` called with the raw password before `save` |
| US-03 | `register` | Email taken | `UserAlreadyExistException` containing the email; `save` never called |
| US-04 | `login` | Valid credentials | Returns token `"jwt-token"`, type `"Bearer"`, and the user payload |
| US-05 | `login` | Unknown email | `InvalidCredentialException("Invalid email or password")` |
| US-06 | `login` | Wrong password | Same exception — message must not distinguish the two cases (no user enumeration) |

---

## 5. Security Layer Test Cases

### 5.1 `JwtService` — `JwtServiceTest`

`secretKey` and `jwtExpiration` are `@Value` fields; set them with
`ReflectionTestUtils.setField`. The secret must be Base64 and decode to ≥ 32 bytes for HS256.

| ID | Method | Scenario | Expected result |
|---|---|---|---|
| JWT-01 | `generateToken` / `extractUsername` | Round trip | The username extracted equals `userDetails.getUsername()` |
| JWT-02 | `isTokenValid` | Same user, unexpired | `true` |
| JWT-03 | `isTokenValid` | Token issued for a different user | `false` |
| JWT-04 | `isTokenValid` | Expiry already passed (`jwtExpiration` negative) | **Throws `ExpiredJwtException`** rather than returning `false` — see DEF-04 |
| JWT-05 | `extractUsername` | Token signed with a different key | Throws `io.jsonwebtoken.security.SignatureException` |

### 5.2 `CustomUserDetailsService` — `CustomUserDetailsServiceTest`

Mocks: `UserRepository`.

| ID | Method | Scenario | Expected result |
|---|---|---|---|
| UDS-01 | `loadUserByUsername` | User exists | `UserDetails` with username = email and password = stored hash |
| UDS-02 | `loadUserByUsername` | User exists | Authorities collection is empty (no roles modelled yet) |
| UDS-03 | `loadUserByUsername` | Unknown email | `UsernameNotFoundException("User not found with email: …")` |

### 5.3 `AuthenticatedUserProvider` — `AuthenticatedUserProviderTest`

Mocks: `UserRepository`. Manipulates `SecurityContextHolder`; **must call
`SecurityContextHolder.clearContext()` in `@AfterEach`** or state leaks between tests.

| ID | Method | Scenario | Expected result |
|---|---|---|---|
| AUP-01 | `getCurrentUser` | No `Authentication` in context | `IllegalStateException("No authenticated user found in the security context")` |
| AUP-02 | `getCurrentUser` | `isAuthenticated()` returns `false` | Same exception |
| AUP-03 | `getCurrentUser` | Principal is the string `"anonymousUser"` | Same exception |
| AUP-04 | `getCurrentUser` | Authenticated but email not in DB | `UsernameNotFoundException("Authenticated user not found: …")` |
| AUP-05 | `getCurrentUser` | Authenticated and found | Returns the `User` entity looked up by `authentication.getName()` |

---

## 6. Controller Layer Test Cases

All controller tests use `MockMvcBuilders.standaloneSetup(new XController(mockService)).build()`
with a Mockito-mocked service — no Spring context, no security filter chain. Note that
`DivingDataController` has **no constructor**, so its dependency must be set with
`ReflectionTestUtils.setField(controller, "divingDataService", mock)`.

### 6.1 `AuthController` — `AuthControllerTest` *(already implemented)*

| ID | Request | Scenario | Expected result |
|---|---|---|---|
| AC-01 | `POST /api/auth/register` | Success | `201 Created`, body carries `id` and `email` |
| AC-02 | `POST /api/auth/register` | `UserAlreadyExistException` | `409 Conflict`, body is the exception message |
| AC-03 | `POST /api/auth/login` | Success | `200 OK`, body has `token`, `type`, `user.email` |
| AC-04 | `POST /api/auth/login` | `InvalidCredentialException` | `401 Unauthorized`, body is the exception message |

### 6.2 `CustomerController` — `CustomerControllerTest`

| ID | Request | Scenario | Expected result |
|---|---|---|---|
| CC-01 | `POST /api/create-customer` | Service returns a customer | `201 Created`; JSON body is the saved customer |
| CC-02 | `POST /api/create-customer` | `CustomerAlreadyExistException` | `409 Conflict`; body is the exception message as plain text |
| CC-03 | `GET /api/customers/{id}` | Service returns a customer | `200 OK`; `$.name` matches |
| CC-04 | `GET /api/customers/{id}` | Service returns `null` | `404 Not Found`. (Unreachable in practice — the service throws instead of returning `null`; see DEF-05) |
| CC-05 | `GET /api/all-customer` | Defaults | `200 OK`; service receives `PageRequest.of(0, 50)` — captured with `ArgumentCaptor<Pageable>` |
| CC-06 | `GET /api/all-customer?page=3&limit=10` | Explicit paging | Service receives `PageRequest.of(2, 10)`; response `currentPage` is `3` (1-based), plus `data`, `totalItems`, `totalPages` |
| CC-07 | `PUT /api/customers/update-customer/{id}` | Success / `MessageErrorException` | `200 OK` with the updated customer / `404 Not Found` with an empty body |

### 6.3 `ProductController` — `ProductControllerTest`

| ID | Request | Scenario | Expected result |
|---|---|---|---|
| PC-01 | `GET /api/products?type=Retail` | Two products | `200 OK`; JSON array of 2 with `id`, `type`, `details`, `price` |
| PC-02 | `GET /api/products?type=Retail` | Field mapping | `$[0].name` is `null` — the controller never calls `dto.setName(...)` (DEF-03) |
| PC-03 | `GET /api/products` | `type` parameter missing | `400 Bad Request` — `@RequestParam` is mandatory |
| PC-04 | `GET /api/all-products` | Delegation | `200 OK`; array of the entities returned by the service |

### 6.4 `OrdersController` — `OrdersControllerTest`

| ID | Request | Scenario | Expected result |
|---|---|---|---|
| OC-01 | `POST /api/create-orders` | Success | `201 Created`; body is the created order |
| OC-02 | `POST /api/create-orders` | `MessageErrorException` | `400 Bad Request`; body is `"Error: " + message` |
| OC-03 | `POST /api/{ordersId}/items` | Success | `200 OK`; body is the updated order |
| OC-04 | `POST /api/{ordersId}/items` | `MessageErrorException` | `400 Bad Request`; empty body |
| OC-05 | `GET /api/orders/{id}` | Found | `200 OK`; `$.id` matches |
| OC-06 | `GET /api/orders/{id}` | `MessageErrorException` | `404 Not Found` |
| OC-07 | `GET /api/all-orders` | Non-empty | `200 OK`; array of orders |
| OC-08 | `GET /api/all-orders` | Empty | `200 OK`; empty array (not 404) |
| OC-09 | `GET /api/customer-orders-history/{customerId}` | Success / failure | `200 OK` with the history DTO / `404 Not Found` with the exception message |

### 6.5 `ReferenceController` — `ReferenceControllerTest`

| ID | Request | Scenario | Expected result |
|---|---|---|---|
| RC-01 | `POST /api/reference` | Success | `201 Created`; body is the saved reference |
| RC-02 | `POST /api/reference` | `MessageErrorException` | `409 Conflict`; body is the exception message. (`409` is semantically wrong for a validation failure — `400` would be correct; see DEF-09) |

### 6.6 `DivingDataController` — `DivingDataControllerTest`

| ID | Request | Scenario | Expected result |
|---|---|---|---|
| DC-01 | `POST /api/diving-data` | Success | `200 OK`; body is the entity returned by the service, with `agencyName` and `level` echoed |
| DC-02 | `POST /api/diving-data` | Delegation | `divingDataService.saveDivingData` called exactly once with the deserialised entity |

---

## 7. Defects and Observations Found During Test Design

These were identified by reading the production code while specifying the cases above. Each
one changes what a test can legitimately assert, so they are listed as part of the test basis.

| ID | Severity | Location | Finding |
|---|---|---|---|
| DEF-01 | Medium | `entity/Customer.java:96` | The boolean accessor is named `diver()`, not `isDiver()` / `getDiver()`. It is not a JavaBean getter, so Jackson and any bean-mapping utility will not see the property through the getter. It currently serialises only because `@JsonProperty("isDiver")` sits on the private field. |
| DEF-02 | **High** | `service/CustomerService.java:45` | `createCustomer` calls `customerDTO.getDivingData().stream()` with no null guard, while the `reference` list two lines below *is* guarded with `Optional.ofNullable`. A create request without a `divingData` array throws `NullPointerException` → HTTP 500. Case CS-07 pins this. |
| DEF-03 | Medium | `controller/ProductController.java:35-42` | The `/api/products` mapping builds a `ProductDTO` but never copies `name`, so every product in the response has `"name": null`. Case PC-02 pins this. |
| DEF-04 | Medium | `security/JwtService.java:44-51` | `isTokenValid` calls `extractUsername` first, which parses the token. For an expired token jjwt throws `ExpiredJwtException` before `isTokenExpired` is ever reached, so the method throws instead of returning `false`. Callers expecting a boolean will get an unhandled exception. Case JWT-04 pins this. |
| DEF-05 | Low | `controller/CustomerController.java:55` | The `customer == null` branch is dead code: `CustomerService.getCustomerById` throws `RuntimeException` when the record is missing, so a `null` never reaches the controller. The real behaviour is an unhandled 500, not a 404. |
| DEF-06 | Low | `service/ProductService.java:30` | Validation rejects only `null`, not blank strings. `""` is accepted as a product name. Case PS-07 pins this. |
| DEF-07 | Low | `service/OrdersService.java:147` | `CustomerOrdersHistoryDTO.totalItems` is populated with `orders.size()` — the number of *orders*, not the number of line items. The field name is misleading. |
| DEF-08 | Medium | `service/ReferenceService.java:17-27` | `addReference` never associates the new `Reference` with a `Customer`, and `Reference.customer` is a `@ManyToOne` on `customer_id`. It also builds the entity *before* validating the name. Rows created through this endpoint are orphaned. |
| DEF-09 | Low | `controller/ReferenceController.java:38` | An empty-name validation error is returned as `409 Conflict`. `400 Bad Request` is the correct status; `409` should be reserved for duplicates. |
| DEF-10 | **High** | `service/OrdersService.java:60` | `createOrders` calls `prodService.createProduct(itemDTO.getProductDTO())` for every line item, which **inserts a new product row per order line** instead of resolving an existing product by id. `addProductToOrders` does the opposite and correctly calls `getProductById`. This duplicates the catalogue on every order. |
| DEF-11 | Low | `service/OrdersService.java:97-105` | Four `System.out.println` debug statements left in `addProductToOrders`. Should be a logger or removed. |
| DEF-12 | Low | `service/CustomerService.java:28`, `service/ProductService.java:17` | `@Autowired` on a `final` field that is also set by the constructor. The annotation is redundant and, on a `final` field, meaningless. |
| DEF-13 | Low | `controller/OrdersController.java:23` | Annotated `@Controller` rather than `@RestController`. It works only because every handler returns `ResponseEntity`, which `HttpEntityMethodProcessor` handles without `@ResponseBody`. Inconsistent with the other controllers and fragile if a handler is ever changed to return a plain object. |

---

## 8. Traceability Summary

| Production class | Test class | Cases | Public methods covered |
|---|---|---|---|
| `CustomerService` | `CustomerServiceTest` | CS-01 … CS-20 | 7 / 7 |
| `ProductService` | `ProductServiceTest` | PS-01 … PS-13 | 4 / 4 |
| `OrdersService` | `OrdersServiceTest` | OS-01 … OS-17 | 6 / 6 |
| `ReferenceService` | `ReferenceServiceTest` | RS-01 … RS-04 | 1 / 1 |
| `DivingDataService` | `DivingDataServiceTest` | DS-01 … DS-02 | 1 / 1 |
| `UserService` | `UserServiceTest` | US-01 … US-06 | 2 / 2 |
| `JwtService` | `JwtServiceTest` | JWT-01 … JWT-05 | 4 / 4 |
| `CustomUserDetailsService` | `CustomUserDetailsServiceTest` | UDS-01 … UDS-03 | 1 / 1 |
| `AuthenticatedUserProvider` | `AuthenticatedUserProviderTest` | AUP-01 … AUP-05 | 1 / 1 |
| `AuthController` | `AuthControllerTest` | AC-01 … AC-04 | 2 / 2 |
| `CustomerController` | `CustomerControllerTest` | CC-01 … CC-07 | 4 / 4 |
| `ProductController` | `ProductControllerTest` | PC-01 … PC-04 | 2 / 2 |
| `OrdersController` | `OrdersControllerTest` | OC-01 … OC-09 | 5 / 5 |
| `ReferenceController` | `ReferenceControllerTest` | RC-01 … RC-02 | 1 / 1 |
| `DivingDataController` | `DivingDataControllerTest` | DC-01 … DC-02 | 1 / 1 |

**Total: 104 specified cases across 15 test classes.**
