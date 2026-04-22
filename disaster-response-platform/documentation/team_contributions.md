# Team Contributions

## Team: PESU — Disaster Response Coordination Platform

| Member | Use Cases Owned | Design Pattern | Design Principle |
|--------|----------------|----------------|-----------------|
| 455 | UC1 (Incident Management), UC6 (Command Center) | Observer Pattern | SRP |
| T | UC2 (Team Management) | Factory Pattern | LSP |
| 480 | UC3 (Shelter Management) | Facade Pattern | OCP |
| 462 | UC4 (Equipment Management), UC5 (Task Assignment) | Strategy + Command Patterns | DRY + Law of Demeter |

---

## 455 — Incident Management + Command Center

### Major Use Case: Incident Management (UC1)
**Files owned:**
- `model/Incident.java` — Entity with severity, status, location
- `model/IncidentStatus.java` — Status enum (REPORTED → CLOSED)
- `model/Severity.java` — Severity enum (CRITICAL/HIGH/MEDIUM/LOW)
- `service/IncidentService.java` — CRUD + status management
- `controller/IncidentController.java` — MVC controller
- `repository/IncidentRepository.java` — JPA queries
- `templates/incidents/` — All incident views

### Minor Use Case: Command Center (UC6)
**Files owned:**
- `model/CommandHistory.java` — Audit record entity
- `controller/CommandCenterController.java` — Command Center MVC controller
- `repository/CommandHistoryRepository.java`
- `templates/command-center/index.html`

### Design Pattern Owned: Observer Pattern
- `observer/IncidentObserver.java` — Interface
- `observer/IncidentEventPublisher.java` — Subject/Publisher
- `observer/CommandCenterObserver.java` — Concrete observer

### Design Principle: SRP
Every class in this system has exactly one reason to change. Evidence in `IncidentService` (business logic only), `CommandCenterObserver` (audit logging only), `NotificationObserver` (notification creation only).

---

## T — Team Management

### Major Use Case: Team Management (UC2)
**Files owned:**
- `model/ResponseTeam.java` — Abstract base entity
- `model/MedicalTeam.java` — Medical subclass
- `model/FireTeam.java` — Fire brigade subclass
- `model/SearchRescueTeam.java` — SAR subclass
- `model/PoliceTeam.java` — Police subclass
- `model/TeamStatus.java`, `model/TeamType.java`
- `dto/TeamFormDTO.java` — Form binding DTO
- `service/TeamService.java`
- `controller/TeamController.java`
- `repository/ResponseTeamRepository.java`
- `templates/teams/` — All team views

### Design Pattern Owned: Factory Method Pattern
- `factory/ResponseTeamFactory.java` — Creates correct subclass from DTO

### Design Principle: LSP
All ResponseTeam subclasses are fully substitutable for ResponseTeam. Strategies, services, and repositories treat all team types identically through the parent reference.

---

## 480 — Shelter Management

### Major Use Case: Shelter Management (UC3)
**Files owned:**
- `model/Shelter.java` — Shelter entity with occupancy tracking
- `model/ShelterStatus.java` — Status enum
- `service/ShelterService.java` — Open/close/occupancy management
- `controller/ShelterController.java`
- `repository/ShelterRepository.java`
- `templates/shelters/` — Card-grid views with occupancy bars

### Minor Use Case: Dashboard (UC8)
- `controller/DashboardController.java`
- `templates/index.html`

### Design Pattern Owned: Facade Pattern
- `facade/DisasterResponseFacade.java` — Unified interface to all subsystems

### Design Principle: OCP
New assignment strategies, new observer types, and new team types can all be added without modifying existing classes. Demonstrated through Strategy, Observer, and Factory patterns.

---

## 462 — Equipment + Task Assignment

### Major Use Case: Equipment Management (UC4)
**Files owned:**
- `model/Equipment.java` — Equipment entity
- `model/EquipmentStatus.java` — Status enum
- `service/EquipmentService.java` — Assign/release/status management
- `controller/EquipmentController.java`
- `repository/EquipmentRepository.java`
- `templates/equipment/` — Equipment list and form

### Minor Use Case: Task Assignment (UC5)
**Files owned:**
- `model/Task.java`, `model/TaskStatus.java`
- `service/TaskAssignmentService.java`
- `controller/TaskController.java`
- `repository/TaskRepository.java`
- `templates/tasks/` — Assignment forms

### Design Patterns Owned: Strategy Pattern + Command Pattern
**Strategy:**
- `strategy/TaskAssignmentStrategy.java`
- `strategy/SeverityBasedStrategy.java`
- `strategy/ProximityBasedStrategy.java`
- `strategy/ResourceBasedStrategy.java`

**Command:**
- `command/DisasterCommand.java`
- `command/AssignTeamCommand.java`
- `command/UpdateIncidentStatusCommand.java`
- `command/CommandInvoker.java`

### Design Principles: DRY + Law of Demeter
**DRY**: Haversine formula defined once in `ResponseTeam.distanceTo()`. Bootstrap color mappings in enums, not in templates. Factory centralises team creation.
**LoD**: `Task.getAssignedTeamName()` wraps team access. Controllers only call services, not repositories.

---

## Shared Infrastructure

The following files were jointly implemented as shared infrastructure:

- `DisasterResponseApplication.java`
- `config/DataInitializer.java`
- `observer/NotificationObserver.java`
- `model/Notification.java`
- `service/NotificationService.java`
- `repository/NotificationRepository.java`
- `templates/fragments/navbar.html`
- `static/css/style.css`
- `application.properties`
- `pom.xml`
