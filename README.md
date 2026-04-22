# Disaster Response Coordination Platform

A web-based platform for managing disaster response operations — incidents, rescue teams, shelters, and equipment — built as an OOAD mini-project.

## Tech Stack

- **Java 17** + **Spring Boot 3.2.3** (MVC)
- **Thymeleaf** for views
- **Spring Data JPA** + **H2** in-memory database
- **Bootstrap 5.3** for UI
- **Maven** for build

## Design Patterns Used

| Pattern | Where |
|---------|-------|
| Strategy | Task assignment (Severity, Proximity, Resource-based) |
| Observer | Notifications on incident status changes |
| Command | Audit log in Command Center |
| Factory | ResponseTeam subclass creation |
| Facade | Unified service entry point |
| State | Status lifecycle for incidents, teams, shelters |

## Design Principles Applied

| Principle | How |
|-----------|-----|
| SRP (Single Responsibility) | Each class has one job — `IncidentService` handles business logic, `NotificationObserver` creates notifications, `ResponseTeamFactory` handles creation |
| LSP (Liskov Substitution) | All `ResponseTeam` subclasses (Medical, Fire, SAR, Police) are used interchangeably via the parent type across all three strategies |
| OCP (Open–Closed) | New strategies or observers require only a new class — zero changes to existing code |
| DRY (Don't Repeat Yourself) | Haversine formula defined once in `ResponseTeam.distanceTo()`; Bootstrap colour mappings centralised in each enum's `getBootstrapColor()`; team creation goes through `ResponseTeamFactory` |
| Law of Demeter | `Task.getAssignedTeamName()` wraps navigation so nothing chains through object graphs; controllers only call services, never repositories |

## Features

- Report and track incidents with severity levels (Critical → Low)
- Manage rescue teams (Medical, Fire, Search & Rescue, Police)
- Dynamic task assignment with swappable strategies
- Shelter occupancy tracking with real-time progress bars
- Equipment assignment and status tracking
- Command Center with audit log and notification feed
- Dashboard with live stats

## Quick Start

**Prerequisites:** Java 17+, Maven 3.8+

```bash
cd disaster-response-platform
mvn spring-boot:run
```

Open `http://localhost:8080` — the app loads with sample data pre-populated.

To build a runnable JAR:

```bash
mvn clean package -DskipTests
java -jar target/disaster-response-1.0.0.jar
```


## Project Structure

```
src/main/java/com/disaster/response/
├── model/        # JPA entities
├── strategy/     # Assignment strategies
├── observer/     # Event/notification system
├── command/      # Audit command pattern
├── factory/      # Team factory
├── service/      # Business logic
└── controller/   # MVC controllers
```
