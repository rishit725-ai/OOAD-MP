# Class Diagram — Disaster Response Coordination Platform

## Paste this into draw.io (Extras → Edit Diagram → Mermaid)

```mermaid
classDiagram
    %% ===== MODEL LAYER =====
    class ResponseTeam {
        <<abstract>>
        -Long id
        -String name
        -String location
        -Double latitude
        -Double longitude
        -TeamStatus status
        -Integer capacity
        -Integer currentLoad
        -String contactInfo
        +getSpecializationDescription() String
        +getTeamType() TeamType
        +isAvailable() boolean
        +distanceTo(double, double) double
        +getAvailableCapacity() int
    }

    class MedicalTeam {
        -String specialization
        -Boolean hasAmbulance
        -Integer numberOfDoctors
        +getSpecializationDescription() String
        +getTeamType() TeamType
    }

    class FireTeam {
        -String equipmentType
        -Integer waterTankCapacity
        -Boolean hasHazmatSuit
        +getSpecializationDescription() String
        +getTeamType() TeamType
    }

    class SearchRescueTeam {
        -String searchCapability
        -Boolean hasDogs
        -Boolean hasDrone
        +getSpecializationDescription() String
        +getTeamType() TeamType
    }

    class PoliceTeam {
        -String jurisdictionArea
        -Integer numberOfOfficers
        -Boolean hasArmoredVehicle
        +getSpecializationDescription() String
        +getTeamType() TeamType
    }

    class Incident {
        -Long id
        -String title
        -String description
        -String location
        -Double latitude
        -Double longitude
        -Severity severity
        -IncidentStatus status
        -String reportedBy
        -LocalDateTime reportedAt
        -LocalDateTime resolvedAt
        +isActive() boolean
    }

    class Task {
        -Long id
        -TaskStatus status
        -String notes
        -String assignmentStrategy
        -Integer priority
        -LocalDateTime assignedAt
        -LocalDateTime completedAt
        +getIncidentTitle() String
        +getAssignedTeamName() String
    }

    class Shelter {
        -Long id
        -String name
        -String location
        -Integer capacity
        -Integer currentOccupancy
        -ShelterStatus status
        -Boolean hasFood
        -Boolean hasMedical
        -Boolean hasPower
        +getAvailableSpots() int
        +getOccupancyPercentage() int
        +isFull() boolean
    }

    class Equipment {
        -Long id
        -String name
        -String category
        -String model
        -EquipmentStatus status
        -Integer quantity
        -Integer availableQuantity
        +getAssignedTeamName() String
    }

    class Notification {
        -Long id
        -String title
        -String message
        -String type
        -LocalDateTime createdAt
        -Boolean isRead
        +getBootstrapAlertClass() String
    }

    class CommandHistory {
        -Long id
        -String commandType
        -String description
        -String executedBy
        -LocalDateTime executedAt
        -Boolean undone
    }

    %% ===== STRATEGY PATTERN =====
    class TaskAssignmentStrategy {
        <<interface>>
        +assignTeam(Incident, List) ResponseTeam
        +getStrategyName() String
    }

    class SeverityBasedStrategy {
        +assignTeam(Incident, List) ResponseTeam
        +getStrategyName() String
    }

    class ProximityBasedStrategy {
        +assignTeam(Incident, List) ResponseTeam
        +getStrategyName() String
    }

    class ResourceBasedStrategy {
        +assignTeam(Incident, List) ResponseTeam
        +getStrategyName() String
    }

    %% ===== OBSERVER PATTERN =====
    class IncidentObserver {
        <<interface>>
        +onIncidentEvent(Incident, String) void
        +getObserverName() String
    }

    class IncidentEventPublisher {
        -List observers
        +subscribe(IncidentObserver) void
        +unsubscribe(IncidentObserver) void
        +publish(Incident, String) void
    }

    class CommandCenterObserver {
        +onIncidentEvent(Incident, String) void
        +getObserverName() String
    }

    class NotificationObserver {
        +onIncidentEvent(Incident, String) void
        +getObserverName() String
    }

    %% ===== COMMAND PATTERN =====
    class DisasterCommand {
        <<interface>>
        +execute() void
        +undo() void
        +getDescription() String
        +getCommandType() String
    }

    class AssignTeamCommand {
        +execute() void
        +undo() void
        +getDescription() String
    }

    class UpdateIncidentStatusCommand {
        +execute() void
        +undo() void
        +getDescription() String
    }

    class CommandInvoker {
        -Deque commandHistory
        +execute(DisasterCommand) void
        +undo() String
    }

    %% ===== FACTORY =====
    class ResponseTeamFactory {
        +createTeam(TeamFormDTO) ResponseTeam
        +updateTeamFromDTO(ResponseTeam, TeamFormDTO) void
        +toDTO(ResponseTeam) TeamFormDTO
    }

    %% ===== FACADE =====
    class DisasterResponseFacade {
        +handleNewIncident(Incident, String) ResponseTeam
        +updateIncidentStatus(Incident, IncidentStatus) void
        +assignTeamToIncident(Long, Long, String) Task
        +undoLastCommand() String
    }

    %% ===== RELATIONSHIPS =====
    ResponseTeam <|-- MedicalTeam
    ResponseTeam <|-- FireTeam
    ResponseTeam <|-- SearchRescueTeam
    ResponseTeam <|-- PoliceTeam

    Incident "1" --> "0..*" Task
    Incident "1" --> "0..*" Notification
    Task "0..*" --> "1" ResponseTeam
    Task "0..*" --> "1" Incident
    Equipment "0..*" --> "0..1" ResponseTeam

    TaskAssignmentStrategy <|.. SeverityBasedStrategy
    TaskAssignmentStrategy <|.. ProximityBasedStrategy
    TaskAssignmentStrategy <|.. ResourceBasedStrategy

    IncidentObserver <|.. CommandCenterObserver
    IncidentObserver <|.. NotificationObserver
    IncidentEventPublisher --> IncidentObserver

    DisasterCommand <|.. AssignTeamCommand
    DisasterCommand <|.. UpdateIncidentStatusCommand
    CommandInvoker --> DisasterCommand

    ResponseTeamFactory ..> ResponseTeam
    DisasterResponseFacade --> TaskAssignmentStrategy
    DisasterResponseFacade --> CommandInvoker
    DisasterResponseFacade --> IncidentEventPublisher
```
