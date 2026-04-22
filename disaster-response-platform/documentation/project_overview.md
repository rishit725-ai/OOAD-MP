# Project Overview — Smart Disaster Response Coordination Platform

## Problem Statement

Design a Disaster Response Coordination Platform that assists authorities in managing rescue teams, equipment, shelters, and real-time incident reports during emergencies. The system assigns tasks dynamically to response teams based on severity, proximity, and resource availability. Field agents update incident status via mobile input (simulated). Command center monitors everything.

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.3 (MVC) |
| View Engine | Thymeleaf 3.x |
| Database | H2 In-Memory (auto-configured) |
| ORM | Spring Data JPA / Hibernate |
| UI | Bootstrap 5.3 + Bootstrap Icons |
| Build Tool | Apache Maven 3.x |

## Major Use Cases (4 — one per team member)

### UC1: Incident Management
- Report new incidents with severity classification (CRITICAL, HIGH, MEDIUM, LOW)
- View, edit, and update incident status through its lifecycle
- Delete resolved incidents
- Auto-notification via Observer pattern on status changes

### UC2: Response Team Management
- Register teams of types: Medical, Fire, Search & Rescue, Police
- Factory pattern creates correct subclass (LSP demonstrated)
- Track team status (Available, Deployed, Standby, Unavailable)
- Monitor capacity and current workload

### UC3: Shelter Management
- Register emergency shelters with capacity and amenities
- Open/close shelters (state transitions)
- Track real-time occupancy with progress bar visualisation
- Flag shelters as FULL automatically when capacity reached

### UC4: Equipment Management
- Track rescue equipment with categories and models
- Assign equipment to response teams
- Release equipment when no longer needed
- Monitor equipment status (Available, In Use, Maintenance, Damaged)

## Minor Use Cases (4)

### UC5: Dynamic Task Assignment (Strategy Pattern)
- Auto-assign best team to incident using selectable strategy
- Manual assignment with strategy logging
- Three strategies: Severity-Based, Proximity-Based, Resource-Based

### UC6: Command Center Monitoring
- Real-time audit log of all system actions (Command Pattern)
- Notification feed auto-populated by Observer pattern
- Mark notifications as read individually or in bulk

### UC7: Status Lifecycle Management
- All entities have defined state machines (drives State Diagrams)
- Status transitions trigger Command pattern entries and Observer notifications

### UC8: Resource Availability Dashboard
- Real-time overview of incidents, teams, shelters, equipment
- Statistics: counts, occupancy percentages, deployment rates
- Quick-action buttons for common operations

## System Architecture

```
Browser (Thymeleaf Views)
        ↓ HTTP Request
Spring MVC Controllers
        ↓ Service calls
   Service Layer
   ├── IncidentService
   ├── TeamService (uses Factory)
   ├── ShelterService
   ├── EquipmentService
   ├── TaskAssignmentService (uses Strategy + Command + Observer)
   └── NotificationService
        ↓ Repository calls
Spring Data JPA Repositories
        ↓ SQL
H2 In-Memory Database
```
