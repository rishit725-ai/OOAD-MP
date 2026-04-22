# Architecture — MVC Pattern with Spring Boot

## MVC Overview

The platform strictly follows the **Model-View-Controller** architectural pattern enforced by Spring MVC:

```
┌─────────────────────────────────────────────────────────┐
│                        VIEW LAYER                        │
│            Thymeleaf HTML Templates                      │
│   (templates/index.html, incidents/*, teams/*, etc.)    │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP (GET/POST)
┌────────────────────────▼────────────────────────────────┐
│                    CONTROLLER LAYER                      │
│         Spring MVC @Controller classes                   │
│   DashboardController, IncidentController,              │
│   TeamController, ShelterController,                    │
│   EquipmentController, TaskController,                  │
│   CommandCenterController                               │
│                                                          │
│   Responsibilities:                                      │
│   • Map HTTP requests to service methods                │
│   • Populate Model with data for views                  │
│   • Handle redirects and flash messages                 │
└────────────────────────┬────────────────────────────────┘
                         │ Method calls
┌────────────────────────▼────────────────────────────────┐
│                     SERVICE LAYER                        │
│   IncidentService, TeamService, ShelterService,         │
│   EquipmentService, TaskAssignmentService,              │
│   NotificationService                                   │
│                                                          │
│   Design Patterns integrated here:                      │
│   • Strategy → TaskAssignmentService                    │
│   • Observer → IncidentService (publishes events)       │
│   • Command  → TaskAssignmentService (via Invoker)      │
│   • Factory  → TeamService (via ResponseTeamFactory)    │
│   • Facade   → DisasterResponseFacade                   │
└────────────────────────┬────────────────────────────────┘
                         │ Repository calls
┌────────────────────────▼────────────────────────────────┐
│                   REPOSITORY LAYER                       │
│   Spring Data JPA Repositories (interfaces)             │
│   IncidentRepository, ResponseTeamRepository,           │
│   ShelterRepository, EquipmentRepository,               │
│   TaskRepository, NotificationRepository,               │
│   CommandHistoryRepository                              │
└────────────────────────┬────────────────────────────────┘
                         │ JPA/SQL
┌────────────────────────▼────────────────────────────────┐
│                     DATABASE LAYER                       │
│              H2 In-Memory Database                       │
│   Tables: incidents, response_teams, shelters,          │
│           equipment, tasks, notifications,              │
│           command_history                               │
└─────────────────────────────────────────────────────────┘
```

## Model Layer Details

### Entity Hierarchy (JPA SINGLE_TABLE Inheritance)

```
ResponseTeam (@Entity, @Inheritance(SINGLE_TABLE))
├── MedicalTeam (@DiscriminatorValue("MEDICAL"))
│   ├── specialization: String
│   ├── hasAmbulance: Boolean
│   └── numberOfDoctors: Integer
├── FireTeam (@DiscriminatorValue("FIRE"))
│   ├── equipmentType: String
│   ├── waterTankCapacity: Integer
│   └── hasHazmatSuit: Boolean
├── SearchRescueTeam (@DiscriminatorValue("SEARCH_RESCUE"))
│   ├── searchCapability: String
│   ├── hasDogs: Boolean
│   └── hasDrone: Boolean
└── PoliceTeam (@DiscriminatorValue("POLICE"))
    ├── jurisdictionArea: String
    ├── numberOfOfficers: Integer
    └── hasArmoredVehicle: Boolean
```

### Database Schema

```sql
CREATE TABLE incidents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR NOT NULL,
    description TEXT,
    location VARCHAR NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    severity VARCHAR,          -- CRITICAL, HIGH, MEDIUM, LOW
    status VARCHAR,            -- REPORTED, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED
    reported_by VARCHAR,
    reported_at TIMESTAMP,
    updated_at TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE response_teams (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_dtype VARCHAR(20),    -- Discriminator: MEDICAL, FIRE, SEARCH_RESCUE, POLICE
    name VARCHAR NOT NULL,
    location VARCHAR,
    latitude DOUBLE,
    longitude DOUBLE,
    status VARCHAR,            -- AVAILABLE, DEPLOYED, STANDBY, UNAVAILABLE
    capacity INT,
    current_load INT,
    contact_info VARCHAR,
    description VARCHAR,
    -- MedicalTeam fields (nullable for other types)
    specialization VARCHAR,
    has_ambulance BOOLEAN,
    number_of_doctors INT,
    -- FireTeam fields
    equipment_type VARCHAR,
    water_tank_capacity INT,
    has_hazmat_suit BOOLEAN,
    -- SearchRescueTeam fields
    search_capability VARCHAR,
    has_dogs BOOLEAN,
    has_drone BOOLEAN,
    -- PoliceTeam fields
    jurisdiction_area VARCHAR,
    number_of_officers INT,
    has_armored_vehicle BOOLEAN
);

CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT REFERENCES incidents(id),
    assigned_team_id BIGINT REFERENCES response_teams(id),
    status VARCHAR,
    notes TEXT,
    assignment_strategy VARCHAR,
    priority INT,
    assigned_at TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE shelters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR NOT NULL,
    location VARCHAR,
    latitude DOUBLE,
    longitude DOUBLE,
    capacity INT,
    current_occupancy INT,
    status VARCHAR,
    contact_person VARCHAR,
    contact_phone VARCHAR,
    has_food BOOLEAN,
    has_medical BOOLEAN,
    has_power BOOLEAN,
    opened_at TIMESTAMP,
    closed_at TIMESTAMP
);

CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR NOT NULL,
    category VARCHAR,
    model VARCHAR,
    status VARCHAR,
    quantity INT,
    available_quantity INT,
    storage_location VARCHAR,
    assigned_team_id BIGINT REFERENCES response_teams(id),
    description TEXT
);

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR,
    message TEXT,
    type VARCHAR,              -- INFO, WARNING, ALERT, SUCCESS
    created_at TIMESTAMP,
    is_read BOOLEAN,
    incident_id BIGINT REFERENCES incidents(id)
);

CREATE TABLE command_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    command_type VARCHAR,
    description TEXT,
    executed_by VARCHAR,
    executed_at TIMESTAMP,
    undone BOOLEAN,
    related_entity_type VARCHAR,
    related_entity_id BIGINT
);
```

## Request Flow Example

**User submits "Report Incident" form:**

```
POST /incidents/new
    → IncidentController.create(@ModelAttribute Incident)
    → IncidentService.createIncident(incident)
        → incidentRepository.save(incident)          [Persistence]
        → eventPublisher.publish(incident, "CREATED") [Observer Pattern]
            → CommandCenterObserver.onIncidentEvent() → commandHistoryRepository.save()
            → NotificationObserver.onIncidentEvent()  → notificationRepository.save()
    ← return saved Incident
← redirect:/incidents/{id}

GET /incidents/{id}
    → IncidentController.view(id, Model)
    → incidentService.findById(id)
    → taskAssignmentService.getTasksForIncident(id)
    → model.addAttribute("incident", incident)
    → model.addAttribute("tasks", tasks)
    ← return "incidents/view"  [Thymeleaf renders HTML]
```
