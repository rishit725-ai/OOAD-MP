# Design Principles — Implementation Evidence

## 1. Single Responsibility Principle (SRP)

> "A class should have only one reason to change."

### Where Applied

| Class | Sole Responsibility |
|-------|-------------------|
| `Incident` | Stores incident data only |
| `IncidentService` | Business logic for incidents only |
| `IncidentRepository` | Database access for incidents only |
| `TaskAssignmentService` | Team-to-incident assignment only |
| `NotificationService` | Notification retrieval/management only |
| `ResponseTeamFactory` | Team object creation only |
| `CommandCenterObserver` | Persisting incident events to command history only |
| `NotificationObserver` | Creating notification records only |
| `AssignTeamCommand` | Encapsulating the assign-team action only |
| `CommandInvoker` | Executing commands and managing undo stack only |

### Evidence
- `IncidentService` does NOT create notifications (that's `NotificationObserver`'s job)
- `NotificationService` does NOT trigger events (that's `IncidentEventPublisher`'s job)
- `ResponseTeamFactory` does NOT save to DB (that's the repository's job)

---

## 2. Open–Closed Principle (OCP)

> "Open for extension, closed for modification."

### Where Applied: Strategy Pattern
Adding a new task assignment algorithm requires ONLY creating a new class:
```java
@Component("skillMatchStrategy")
public class SkillMatchStrategy implements TaskAssignmentStrategy {
    @Override
    public ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams) {
        // New algorithm here
    }
}
```
Zero changes to `TaskAssignmentService`, `TaskController`, or any existing strategy class.

### Where Applied: Observer Pattern
New observers (e.g., email alerts) require ONLY:
```java
@Component
public class EmailObserver implements IncidentObserver {
    @Override
    public void onIncidentEvent(Incident incident, String eventType) { ... }
}
// Then register in DataInitializer:
eventPublisher.subscribe(emailObserver);
```

### Where Applied: Factory Pattern
Adding a new team type (e.g., `HazmatTeam`) requires ONLY:
1. New entity class extending `ResponseTeam`
2. New enum value in `TeamType`
3. New `case` in `ResponseTeamFactory.createTeam()`
Existing team creation logic unchanged.

---

## 3. Liskov Substitution Principle (LSP)

> "Objects of a subclass must be substitutable for objects of the parent class without breaking the program."

### Where Applied: ResponseTeam Hierarchy

```
ResponseTeam (abstract)
├── MedicalTeam   — @DiscriminatorValue("MEDICAL")
├── FireTeam      — @DiscriminatorValue("FIRE")
├── SearchRescueTeam — @DiscriminatorValue("SEARCH_RESCUE")
└── PoliceTeam    — @DiscriminatorValue("POLICE")
```

All strategy methods use `ResponseTeam` references:
```java
// SeverityBasedStrategy.java
public ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams) {
    return availableTeams.stream()
        .filter(ResponseTeam::isAvailable)  // Works for ALL subclasses
        .max(Comparator.comparingInt(ResponseTeam::getAvailableCapacity))
        .orElse(null);
}
```

The repository returns all subclass instances polymorphically:
```java
ResponseTeamRepository.findAvailableTeams()
// Returns: [MedicalTeam, FireTeam, SearchRescueTeam, PoliceTeam] — all as ResponseTeam
```

Substitution proof: Anywhere a `ResponseTeam` is expected, any subclass works correctly:
- `team.isAvailable()` → correct for all subclasses
- `team.distanceTo(lat, lon)` → correct for all subclasses
- `team.getSpecializationDescription()` → polymorphic, returns subclass-specific description

---

## 4. DRY — Don't Repeat Yourself

> "Every piece of knowledge must have a single, unambiguous, authoritative representation."

### Where Applied

**Haversine distance formula**: Defined once in `ResponseTeam.distanceTo()`, used by `ProximityBasedStrategy`. Never duplicated.

**Team creation logic**: `ResponseTeamFactory.createTeam(dto)` — all team creation goes through one place.

**Observer notification**: `IncidentEventPublisher.publish()` — all event publication goes through one method.

**Bootstrap color mapping**: Each enum (Severity, IncidentStatus, TeamStatus) has a `getBootstrapColor()` method. Templates use `${entity.status.bootstrapColor}` — no duplicated color strings in templates.

**Form/Entity conversion**: `ResponseTeamFactory.toDTO()` and `updateTeamFromDTO()` — one place for form-entity mapping.

---

## 5. Law of Demeter (LoD)

> "A class should talk only to its immediate collaborators — no a.b().c()."

### Compliant Pattern Used Throughout

**In Task.java**:
```java
// VIOLATES LoD (DO NOT use):
// task.getAssignedTeam().getName()

// COMPLIANT (wrapper method):
public String getAssignedTeamName() {
    return assignedTeam != null ? assignedTeam.getName() : "Unassigned";
}
```

**In Equipment.java**:
```java
public String getAssignedTeamName() {
    return assignedTeam != null ? assignedTeam.getName() : "Unassigned";
}
```

**In Controllers**: Controllers only call service methods, never access repositories directly.
```java
// IncidentController only calls IncidentService — never IncidentRepository
incidentService.updateStatus(id, status);  // CORRECT
// incidentRepository.findById(id).get().setStatus(status)  // WRONG — bypasses service
```

**In Facade**:
```java
// Each field is a direct collaborator — no chaining:
strategy.assignTeam(incident, available);  // Not: strategies.get(key).assignTeam(...)
commandInvoker.execute(cmd);               // Not: cmd.getInvoker().run()
```
