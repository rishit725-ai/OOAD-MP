# Feature List

## Major Features (4)

### 1. Incident Management
- **Report** new incidents with title, description, severity (CRITICAL/HIGH/MEDIUM/LOW), GPS coordinates
- **View** all incidents in tabular list with colour-coded severity badges
- **Edit** incident details at any time
- **Update Status** through lifecycle: REPORTED → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
- **Delete** incidents (with confirmation)
- **Observer trigger**: every create/update publishes to Command Center and Notifications

### 2. Response Team Management
- **Register** teams of 4 types via Factory Pattern: Medical, Fire, Search & Rescue, Police
- **Subclass-specific fields**: each type shows its own fields (specialization, equipment type, etc.)
- **View** team roster with occupancy progress bars and status badges
- **Edit** team details (type cannot be changed after creation — LSP preserved)
- **Delete** teams

### 3. Shelter Management
- **Register** shelters with capacity, location, GPS, amenities (food/medical/power)
- **Open / Close** shelters with timestamp tracking
- **Occupancy management**: +10/-10 buttons for quick updates
- **Auto-FULL**: status automatically changes to FULL when capacity reached
- **Visual cards** with colour-coded progress bars (green < 70%, yellow < 90%, red >= 90%)

### 4. Equipment Management
- **Register** equipment with category, model, quantity
- **Assign** to response teams (status → IN_USE)
- **Release** from team (status → AVAILABLE)
- **Status tracking**: AVAILABLE, IN_USE, MAINTENANCE, DAMAGED
- **Bulk quantity** tracking with available vs total counts

## Minor Features (4)

### 5. Dynamic Task Assignment (Strategy Pattern)
- **Auto-assign**: system selects best team using chosen algorithm
  - Severity-Based: most capacity for critical, least for low
  - Proximity-Based: Haversine formula calculates nearest team
  - Resource-Based: lowest load ratio = most under-utilised team
- **Manual assign**: operator picks specific team + logs strategy
- **Task history**: all assignments logged with strategy name
- **Task status updates**: ASSIGNED → IN_PROGRESS → COMPLETED

### 6. Command Center Monitoring
- **Real-time audit log**: all Command pattern executions listed chronologically
- **Notification feed**: Observer pattern auto-creates notifications per event
- **Mark as read**: individually or all at once
- **Auto-refresh**: page reloads every 30 seconds

### 7. Status Lifecycle Management (State Machine)
- **Incident lifecycle**: REPORTED → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
- **Team lifecycle**: AVAILABLE ↔ DEPLOYED ↔ STANDBY ↔ UNAVAILABLE
- **Shelter lifecycle**: STANDBY → OPEN ↔ FULL → CLOSED
- **Task lifecycle**: ASSIGNED → IN_PROGRESS → COMPLETED / CANCELLED
- All transitions go through Command pattern (auditable, undoable)

### 8. Dashboard (Command Center Overview)
- **Live counters**: active incidents, available teams, open shelters, equipment
- **Critical badge**: pulsing red animation when CRITICAL incidents active
- **Recent incidents table**: quick view of active emergencies
- **Notification preview**: latest 5 unread notifications
- **Quick actions**: one-click to report incident, assign team, manage shelters
- **Statistics row**: total counts for all entity types
