# Frequently Asked Questions (Evaluation FAQ)

## Q1: Where exactly is the Strategy Pattern?

**A:** In `com.disaster.response.strategy`:
- `TaskAssignmentStrategy` (interface) — defines `assignTeam(Incident, List<ResponseTeam>)`
- `SeverityBasedStrategy` — picks team by incident severity priority
- `ProximityBasedStrategy` — picks nearest team using Haversine formula
- `ResourceBasedStrategy` — picks team with lowest load ratio

**Invoked in**: `TaskAssignmentService.autoAssign()` — the strategy is selected at runtime via a Spring bean map keyed by strategy name. The service never changes when new strategies are added.

---

## Q2: Where exactly is the Observer Pattern?

**A:** In `com.disaster.response.observer`:
- `IncidentObserver` (interface) — `onIncidentEvent(Incident, String)`
- `IncidentEventPublisher` (Subject) — maintains observer list, calls `publish()`
- `CommandCenterObserver` — saves events to `CommandHistory` table
- `NotificationObserver` — creates `Notification` records

**Registration**: `DataInitializer.run()` subscribes both observers on startup.
**Trigger**: `IncidentService.createIncident()`, `IncidentService.updateStatus()`, `TaskAssignmentService.executeAssignment()` all call `eventPublisher.publish()`.

---

## Q3: Where exactly is the Command Pattern?

**A:** In `com.disaster.response.command`:
- `DisasterCommand` (interface) — `execute()`, `undo()`, `getDescription()`
- `AssignTeamCommand` — creates a Task, marks team DEPLOYED; undo cancels task, returns team
- `UpdateIncidentStatusCommand` — changes status, stores previous; undo reverts
- `CommandInvoker` — executes commands, persists to DB, maintains undo stack

**Visible in UI**: Command Center page shows the complete audit log from `CommandHistory` table.

---

## Q4: Where exactly is the Factory Pattern?

**A:** `com.disaster.response.factory.ResponseTeamFactory`:
```java
public ResponseTeam createTeam(TeamFormDTO dto) {
    return switch (dto.getTeamType()) {
        case MEDICAL       -> createMedicalTeam(dto);
        case FIRE          -> createFireTeam(dto);
        case SEARCH_RESCUE -> createSearchRescueTeam(dto);
        case POLICE        -> createPoliceTeam(dto);
    };
}
```
`TeamController` calls `teamService.createTeam(dto)` → `teamFactory.createTeam(dto)` → correct subclass created. Controller never uses `new MedicalTeam(...)` directly.

---

## Q5: Where exactly is the Facade Pattern?

**A:** `com.disaster.response.facade.DisasterResponseFacade`:
- `handleNewIncident(incident, strategyKey)` — saves incident + notifies + assigns team + updates status (4 subsystems, 1 method call)
- `updateIncidentStatus(incident, status)` — command + notification (2 subsystems, 1 call)
- `assignTeamToIncident(incidentId, teamId, strategyKey)` — full assignment pipeline

Used when a single action needs to coordinate multiple subsystems.

---

## Q6: How is LSP demonstrated?

**A:** All four team types (`MedicalTeam`, `FireTeam`, `SearchRescueTeam`, `PoliceTeam`) extend `ResponseTeam` (abstract). Every strategy, repository query, and service method operates on `ResponseTeam` references without knowing the concrete type. Substituting any subclass in place of `ResponseTeam` works without errors — this IS the LSP definition. The Teams list page proves this: all 4 types display correctly through the same Thymeleaf template accessing `team.specializationDescription` which dispatches polymorphically.

---

## Q7: How is SRP demonstrated?

**A:** Each class has exactly one responsibility:
- `IncidentService` — business logic for incidents (not notifications, not assignment)
- `NotificationObserver` — creates notifications (not logs, not emails)
- `CommandInvoker` — executes and undoes commands (not creating them)
- `ResponseTeamFactory` — creates team instances (not saving them)

If you asked "what would cause this class to change?", there would be exactly one answer for each.

---

## Q8: How is OCP demonstrated?

**A:** Three places:
1. **Strategy**: Add `@Component("newStrategy") class NewStrategy implements TaskAssignmentStrategy {}` — zero existing code changes
2. **Observer**: Add `@Component class EmailObserver implements IncidentObserver {}` — zero existing code changes
3. **Factory**: Add new TeamType enum + new subclass + new switch case in factory — existing strategies and observers unchanged

---

## Q9: How is Law of Demeter demonstrated?

**A:** 
- `Task.getAssignedTeamName()` — instead of `task.getAssignedTeam().getName()` (which would violate LoD)
- `Equipment.getAssignedTeamName()` — same pattern
- Controllers call service methods only, never repositories
- Services call their own repository only, not each other's repositories

The rule: "talk to friends, not friends of friends."

---

## Q10: What happens to data on restart?

**A:** H2 is an in-memory database, so all data is lost when the app stops. This is by design for easy demos — the `DataInitializer` repopulates sample data on every startup. For production, switch to PostgreSQL or MySQL by changing `application.properties`.

---

## Q11: How does real-time monitoring work?

**A:** The Command Center page uses a JavaScript `setTimeout(() => location.reload(), 30000)` to refresh every 30 seconds. In production this would use WebSockets (Spring's STOMP support) for true real-time push. The Observer pattern architecture is already WebSocket-ready — the observers would emit to WebSocket sessions instead of saving to DB.

---

## Q12: What is the class hierarchy for ResponseTeam?

**A:** 
```
ResponseTeam (abstract, JPA SINGLE_TABLE)
├── Abstract methods: getSpecializationDescription(), getTeamType()
├── Common methods: isAvailable(), distanceTo(), getAvailableCapacity()
├── MedicalTeam: specialization, hasAmbulance, numberOfDoctors
├── FireTeam: equipmentType, waterTankCapacity, hasHazmatSuit
├── SearchRescueTeam: searchCapability, hasDogs, hasDrone
└── PoliceTeam: jurisdictionArea, numberOfOfficers, hasArmoredVehicle
```
All stored in one `response_teams` table with `team_dtype` discriminator column.
