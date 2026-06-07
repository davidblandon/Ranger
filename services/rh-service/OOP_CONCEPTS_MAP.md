# OOP Concepts Map for RH Service

This file maps the main source files to the object-oriented concepts they use and explains why each choice exists.

## Domain Model

| File | Concepts | Short explanation |
|---|---|---|
| `src/main/java/com/example/rhservice/domain/model/User.java` | Abstraction, inheritance, encapsulation | Abstract base entity for shared user data. `Employee` and `Admin` extend it, so common state is centralized. |
| `src/main/java/com/example/rhservice/domain/model/Employee.java` | Inheritance, association, aggregation | Extends `User`, belongs to one `Shift`, and owns a payroll list as part of the employee aggregate. |
| `src/main/java/com/example/rhservice/domain/model/Admin.java` | Inheritance, specialization | Extends `User` and adds admin-specific permissions. |
| `src/main/java/com/example/rhservice/domain/model/Payroll.java` | Association, encapsulation | Represents a payroll record linked to one employee. |
| `src/main/java/com/example/rhservice/domain/model/Shift.java` | Composition-style value collection | Stores weekly time blocks using embedded values for each day. |
| `src/main/java/com/example/rhservice/domain/model/TimeBlock.java` | Value object, encapsulation | Small embeddable object that represents a time interval. |

## Application Layer

| File | Concepts | Short explanation |
|---|---|---|
| `src/main/java/com/example/rhservice/application/port/in/CrudUseCase.java` | Abstraction, polymorphism | Generic use-case contract used by all application services. |
| `src/main/java/com/example/rhservice/application/port/in/EmployeeService.java` | Abstraction, interface segregation | Specific input port for employee operations. |
| `src/main/java/com/example/rhservice/application/port/in/AdminService.java` | Abstraction, interface segregation | Specific input port for admin operations. |
| `src/main/java/com/example/rhservice/application/port/in/PayrollService.java` | Abstraction, interface segregation | Specific input port for payroll operations. |
| `src/main/java/com/example/rhservice/application/port/in/ShiftService.java` | Abstraction, interface segregation | Specific input port for shift operations. |
| `src/main/java/com/example/rhservice/application/port/out/CrudPersistencePort.java` | Abstraction, dependency inversion | Persistence contract used by services instead of concrete repositories. |
| `src/main/java/com/example/rhservice/application/service/impl/AbstractCrudService.java` | Abstraction, dependency inversion, reuse | Base class that removes duplicated CRUD orchestration across services. |
| `src/main/java/com/example/rhservice/application/service/impl/EmployeeServiceImpl.java` | Polymorphism, dependency injection | Concrete employee service that implements the input port and delegates through the output port. |
| `src/main/java/com/example/rhservice/application/service/impl/AdminServiceImpl.java` | Polymorphism, dependency injection | Concrete admin service that follows the same reusable pattern. |
| `src/main/java/com/example/rhservice/application/service/impl/PayrollServiceImpl.java` | Polymorphism, dependency injection | Concrete payroll service implementation. |
| `src/main/java/com/example/rhservice/application/service/impl/ShiftServiceImpl.java` | Polymorphism, dependency injection | Concrete shift service implementation. |

## Infrastructure Layer

| File | Concepts | Short explanation |
|---|---|---|
| `src/main/java/com/example/rhservice/infrastructure/persistence/adapter/EmployeePersistenceAdapter.java` | Adapter pattern, single responsibility | Translates employee persistence calls to Spring Data JPA. |
| `src/main/java/com/example/rhservice/infrastructure/persistence/adapter/AdminPersistenceAdapter.java` | Adapter pattern, single responsibility | Translates admin persistence calls to Spring Data JPA. |
| `src/main/java/com/example/rhservice/infrastructure/persistence/adapter/PayrollPersistenceAdapter.java` | Adapter pattern, single responsibility | Translates payroll persistence calls to Spring Data JPA. |
| `src/main/java/com/example/rhservice/infrastructure/persistence/adapter/ShiftPersistenceAdapter.java` | Adapter pattern, single responsibility | Translates shift persistence calls to Spring Data JPA. |
| `src/main/java/com/example/rhservice/infrastructure/persistence/jpa/*.java` | Framework integration | Spring Data repositories that hide low-level persistence details. |

## Where the Concepts Appear

- Inheritance: `User` is the base class for `Employee` and `Admin`.
- Abstraction: input ports, output ports, and the abstract CRUD service.
- Encapsulation: fields stay inside the model classes and are accessed through generated getters and setters.
- Single Responsibility: model, service, and adapter layers each do one job.
- Dependency Inversion: services depend on ports, not repositories.
- Adapter Pattern: persistence adapters connect the application ports to Spring Data JPA.
- Polymorphism: the application layer works through interfaces, so implementations can change without changing callers.

## Notes

- Controllers and DTOs are not implemented yet.
- Unit and integration tests are still pending.
- This map reflects the current code base, not the future REST layer.
