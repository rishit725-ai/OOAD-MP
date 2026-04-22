# Design Patterns — Implementation Guide

## 1. Strategy Pattern (Behavioral) — Task Assignment

**Location**: `com.disaster.response.strategy`

**Intent**: Define a family of algorithms, encapsulate each one, and make them interchangeable without altering clients.

### Interface
```java
// TaskAssignmentStrategy.java
public interface TaskAssignmentStrategy {
    ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams);
    String getStrategyName();
}
```

### Concrete Strategies
| Class | Algorithm |
|-------|-----------|
| `SeverityBasedStrategy` | CRITICAL/HIGH → max capacity team; MEDIUM/LOW → min sufficient |
| `ProximityBasedStrategy` | Haversine distance → nearest team |
| `ResourceBasedStrategy` | Load ratio = currentLoad/capacity → lowest wins |

### How it's Used
```java
// TaskAssignmentService.java
TaskAssignmentStrategy strategy = strategies.get(strategyKey);  // injected Spring bean map
ResponseTeam selected = strategy.assignTeam(incident, availableTeams);
```

### OCP Compliance
To add a new strategy (e.g., `SkillMatchStrategy`), create a new `@Component` implementing `TaskAssignmentStrategy`. Zero existing code changes.

---

## 2. Observer Pattern (Behavioral) — Incident Event Notifications

**Location**: `com.disaster.response.observer`

**Intent**: When an incident changes state, notify all registered observers automatically without tight coupling.

### Components
```
IncidentEventPublisher (Subject)
    ├── subscribers: List<IncidentObserver>
    ├── subscribe(observer)
    ├── unsubscribe(observer)
    └── publish(incident, eventType) → calls all observers

CommandCenterObserver (implements IncidentObserver)
    └── onIncidentEvent() → saves to CommandHistory table

NotificationObserver (implements IncidentObserver)
    └── onIncidentEvent() → saves to Notification table
```

### Registration (DataInitializer.java)
```java
eventPublisher.subscribe(commandCenterObserver);
eventPublisher.subscribe(notificationObserver);
```

### Event Types
- `CREATED` — new incident reported
- `STATUS_UPDATED` — status changed
- `TEAM_ASSIGNED` — team dispatched
- `RESOLVED` — incident resolved

### OCP Compliance
New observers (e.g., SMS gateway, email sender) require only a new class implementing `IncidentObserver`.

---

## 3. Command Pattern (Behavioral) — Auditable Action Execution

**Location**: `com.disaster.response.command`

**Intent**: Encapsulate requests as objects to support undo, logging, and queuing.

### Interface
```java
public interface DisasterCommand {
    void execute();     // Perform action
    void undo();        // Revert action
    String getDescription();  // Audit description
    String getCommandType();  // Short type label
}
```

### Concrete Commands
| Command | execute() | undo() |
|---------|-----------|--------|
| `AssignTeamCommand` | Creates Task, deploys team | Cancels task, returns team |
| `UpdateIncidentStatusCommand` | Sets new status | Reverts to previous status |

### Invoker
```java
// CommandInvoker.java
public void execute(DisasterCommand command) {
    command.execute();
    commandHistoryRepository.save(...);  // Persists to DB
    commandHistory.push(command);        // In-memory for undo
}
```

### SRP Compliance
Each command class has exactly one reason to change (its action type).

---

## 4. Factory Method Pattern (Creational) — Team Creation

**Location**: `com.disaster.response.factory.ResponseTeamFactory`

**Intent**: Define an interface for creating objects, but let subclasses decide which class to instantiate.

### Usage
```java
// ResponseTeamFactory.java
public ResponseTeam createTeam(TeamFormDTO dto) {
    return switch (dto.getTeamType()) {
        case MEDICAL       -> createMedicalTeam(dto);
        case FIRE          -> createFireTeam(dto);
        case SEARCH_RESCUE -> createSearchRescueTeam(dto);
        case POLICE        -> createPoliceTeam(dto);
    };
}
```

### Benefit
Controllers never instantiate `new MedicalTeam(...)` directly. They call `factory.createTeam(dto)` and receive a `ResponseTeam` reference.

### OCP Compliance
Adding `HazmatTeam` requires only: (1) new entity class, (2) new case in factory, (3) new enum value.

---

## 5. Facade Pattern (Structural) — Emergency Response Coordination

**Location**: `com.disaster.response.facade.DisasterResponseFacade`

**Intent**: Provide a unified, simplified interface to a set of complex subsystems.

### What it Hides
```java
// Without Facade (complex):
incidentRepo.save(incident);
eventPublisher.publish(incident, "CREATED");
strategy = strategies.get(key);
team = strategy.assignTeam(incident, available);
cmd = new AssignTeamCommand(incident, team, ...);
invoker.execute(cmd);
statusCmd = new UpdateIncidentStatusCommand(...);
invoker.execute(statusCmd);
eventPublisher.publish(incident, "TEAM_ASSIGNED");

// With Facade (simple):
facade.handleNewIncident(incident, strategyKey);
```

### Law of Demeter Compliance
The facade communicates only with its direct dependencies (injected services and repositories).
No chains like `incident.getTeam().getStatus().isAvailable()`.

---

## Pattern Interaction Diagram

```
User clicks "Report Incident"
        ↓
IncidentController
        ↓ calls
IncidentService.createIncident()
        ↓ calls
IncidentEventPublisher.publish("CREATED")
        ↓ notifies (Observer Pattern)
    ├── CommandCenterObserver → saves CommandHistory
    └── NotificationObserver  → saves Notification

User clicks "Auto-Assign Team"
        ↓
TaskController
        ↓ calls
TaskAssignmentService.autoAssign(incidentId, strategyKey)
        ↓ selects (Strategy Pattern)
SeverityBasedStrategy.assignTeam()
        ↓ wraps in (Command Pattern)
AssignTeamCommand.execute()
        ↓
CommandInvoker.execute() → persists audit log
        ↓ publishes (Observer Pattern)
IncidentEventPublisher → notifies observers
```
