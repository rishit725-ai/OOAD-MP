package com.disaster.response.config;

import com.disaster.response.model.*;
import com.disaster.response.observer.CommandCenterObserver;
import com.disaster.response.observer.IncidentEventPublisher;
import com.disaster.response.observer.NotificationObserver;
import com.disaster.response.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Populates sample data on startup for demo purposes.
 * Registers Observer pattern observers.
 * Demonstrates Factory pattern (teams created via concrete constructors here for brevity).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ResponseTeamRepository teamRepository;
    private final IncidentRepository incidentRepository;
    private final ShelterRepository shelterRepository;
    private final EquipmentRepository equipmentRepository;
    private final IncidentEventPublisher eventPublisher;
    private final CommandCenterObserver commandCenterObserver;
    private final NotificationObserver notificationObserver;

    public DataInitializer(ResponseTeamRepository teamRepository,
                           IncidentRepository incidentRepository,
                           ShelterRepository shelterRepository,
                           EquipmentRepository equipmentRepository,
                           IncidentEventPublisher eventPublisher,
                           CommandCenterObserver commandCenterObserver,
                           NotificationObserver notificationObserver) {
        this.teamRepository = teamRepository;
        this.incidentRepository = incidentRepository;
        this.shelterRepository = shelterRepository;
        this.equipmentRepository = equipmentRepository;
        this.eventPublisher = eventPublisher;
        this.commandCenterObserver = commandCenterObserver;
        this.notificationObserver = notificationObserver;
    }

    @Override
    public void run(String... args) {
        // ===== Register Observers (Observer Pattern) =====
        eventPublisher.subscribe(commandCenterObserver);
        eventPublisher.subscribe(notificationObserver);
        log.info("Observers registered: {}", eventPublisher.getObserverCount());

        // ===== Sample Response Teams (LSP — all extend ResponseTeam) =====
        MedicalTeam med1 = new MedicalTeam("Alpha Medical Unit", "City Hospital, Central", 12.9716, 77.5946,
                15, "+91-9876543210", "Primary trauma and emergency medical team",
                "Trauma Care", true, 3);
        med1.setStatus(TeamStatus.AVAILABLE);

        MedicalTeam med2 = new MedicalTeam("Beta Medical Team", "District Hospital, North", 13.0358, 77.5970,
                12, "+91-9876543211", "Specialised burn and critical care unit",
                "Burn Care", false, 2);
        med2.setStatus(TeamStatus.AVAILABLE);

        FireTeam fire1 = new FireTeam("Fire Brigade Alpha", "Central Fire Station", 12.9619, 77.5937,
                20, "+91-9876543212", "Class A and B fire suppression team",
                "Class A/B", 5000, true);
        fire1.setStatus(TeamStatus.AVAILABLE);

        FireTeam fire2 = new FireTeam("Fire Brigade Beta", "South Fire Station", 12.9141, 77.6090,
                18, "+91-9876543213", "Industrial and chemical fire specialists",
                "Class C/D", 8000, true);
        fire2.setStatus(TeamStatus.DEPLOYED);
        fire2.setCurrentLoad(3);

        SearchRescueTeam sar1 = new SearchRescueTeam("Urban SAR Team 1", "Emergency HQ, East", 12.9800, 77.6300,
                12, "+91-9876543214", "Urban search and rescue with K-9 unit",
                "Urban", true, true);
        sar1.setStatus(TeamStatus.AVAILABLE);

        SearchRescueTeam sar2 = new SearchRescueTeam("Mountain Rescue Unit", "Northern Base Camp", 13.1000, 77.5700,
                10, "+91-9876543215", "Mountain terrain search and rescue",
                "Mountain", true, false);
        sar2.setStatus(TeamStatus.STANDBY);

        PoliceTeam police1 = new PoliceTeam("Police Response Unit A", "Central Police Station", 12.9768, 77.5915,
                25, "+91-9876543216", "Crowd control and area security",
                "Central Zone", 12, true);
        police1.setStatus(TeamStatus.AVAILABLE);

        teamRepository.save(med1);
        teamRepository.save(med2);
        teamRepository.save(fire1);
        teamRepository.save(fire2);
        teamRepository.save(sar1);
        teamRepository.save(sar2);
        teamRepository.save(police1);

        // ===== Sample Incidents =====
        Incident i1 = new Incident("Building Collapse - MG Road",
                "6-storey commercial building partially collapsed. Estimated 30+ trapped inside.",
                "MG Road, Bengaluru", 12.9757, 77.6011, Severity.CRITICAL, "Field Agent A1");
        i1.setStatus(IncidentStatus.IN_PROGRESS);
        i1.setReportedAt(LocalDateTime.now().minusHours(2));

        Incident i2 = new Incident("Chemical Plant Fire - Whitefield",
                "Chemical storage unit caught fire. Hazardous fumes spreading.",
                "Whitefield Industrial Area", 12.9698, 77.7500, Severity.HIGH, "Field Agent B2");
        i2.setStatus(IncidentStatus.ASSIGNED);
        i2.setReportedAt(LocalDateTime.now().minusHours(1));

        Incident i3 = new Incident("Flood - Koramangala Low-lying Area",
                "Severe waterlogging. 200+ residents stranded.",
                "Koramangala Sector 5", 12.9352, 77.6245, Severity.HIGH, "Field Agent C3");
        i3.setStatus(IncidentStatus.REPORTED);
        i3.setReportedAt(LocalDateTime.now().minusMinutes(45));

        Incident i4 = new Incident("Road Accident - Outer Ring Road",
                "Multi-vehicle accident. 5 injured, 2 critical.",
                "Outer Ring Road, Marathahalli", 12.9567, 77.7006, Severity.MEDIUM, "Field Agent D4");
        i4.setStatus(IncidentStatus.ASSIGNED);
        i4.setReportedAt(LocalDateTime.now().minusMinutes(30));

        Incident i5 = new Incident("Gas Leak - Indiranagar",
                "Residential LPG pipeline leak reported. Area evacuated.",
                "Indiranagar 100 Feet Road", 12.9784, 77.6408, Severity.MEDIUM, "Field Agent E5");
        i5.setStatus(IncidentStatus.REPORTED);
        i5.setReportedAt(LocalDateTime.now().minusMinutes(15));

        Incident i6 = new Incident("Minor Fire - Shivajinagar Market",
                "Small fire at market stall. Being contained by local personnel.",
                "Shivajinagar Market Complex", 12.9857, 77.5965, Severity.LOW, "Field Agent F6");
        i6.setStatus(IncidentStatus.RESOLVED);
        i6.setReportedAt(LocalDateTime.now().minusHours(3));
        i6.setResolvedAt(LocalDateTime.now().minusHours(2));

        incidentRepository.save(i1);
        incidentRepository.save(i2);
        incidentRepository.save(i3);
        incidentRepository.save(i4);
        incidentRepository.save(i5);
        incidentRepository.save(i6);

        // ===== Sample Shelters =====
        Shelter s1 = new Shelter("City Sports Complex", "Kanteerava Indoor Stadium, Central",
                12.9726, 77.5912, 500, "Mr. Rajan Kumar", "+91-9845012345",
                true, true, true);
        s1.setStatus(ShelterStatus.OPEN);
        s1.setCurrentOccupancy(320);
        s1.setOpenedAt(LocalDateTime.now().minusHours(4));

        Shelter s2 = new Shelter("Government School Shelter", "Govt High School, Koramangala",
                12.9340, 77.6200, 200, "Ms. Priya Singh", "+91-9845012346",
                true, false, true);
        s2.setStatus(ShelterStatus.OPEN);
        s2.setCurrentOccupancy(145);
        s2.setOpenedAt(LocalDateTime.now().minusHours(2));

        Shelter s3 = new Shelter("Community Hall North", "Town Hall, Yelahanka",
                13.1007, 77.5963, 300, "Mr. Anil Sharma", "+91-9845012347",
                false, true, false);
        s3.setStatus(ShelterStatus.STANDBY);
        s3.setCurrentOccupancy(0);

        Shelter s4 = new Shelter("Emergency Relief Camp", "BBMP Grounds, Whitefield",
                12.9698, 77.7497, 400, "Ms. Kavitha Reddy", "+91-9845012348",
                true, true, true);
        s4.setStatus(ShelterStatus.OPEN);
        s4.setCurrentOccupancy(380);
        s4.setOpenedAt(LocalDateTime.now().minusHours(1));

        shelterRepository.save(s1);
        shelterRepository.save(s2);
        shelterRepository.save(s3);
        shelterRepository.save(s4);

        // ===== Sample Equipment =====
        Equipment e1 = new Equipment("Hydraulic Rescue Tool", "Firefighting", "Holmatro HTRM 32",
                3, "Central Warehouse", "Heavy-duty jaws of life for vehicle/building rescue");
        e1.setStatus(EquipmentStatus.IN_USE);
        e1.setAvailableQuantity(1);
        e1.setAssignedTeam(fire1);

        Equipment e2 = new Equipment("Advanced Life Support Ambulance", "Medical", "TATA Winger ALS",
                4, "City Hospital Parking", "Fully equipped ALS ambulance with defibrillator");
        e2.setStatus(EquipmentStatus.AVAILABLE);
        e2.setAvailableQuantity(4);

        Equipment e3 = new Equipment("Search Drone (Thermal)", "Search & Rescue", "DJI Matrice 300 RTK",
                2, "SAR Base Camp", "Thermal imaging drone for survivor detection");
        e3.setStatus(EquipmentStatus.AVAILABLE);
        e3.setAvailableQuantity(2);

        Equipment e4 = new Equipment("Portable Water Pump", "Firefighting", "Godwin CD100M",
                5, "South Fire Station", "High-capacity portable pump for flood/fire");
        e4.setStatus(EquipmentStatus.IN_USE);
        e4.setAvailableQuantity(2);

        Equipment e5 = new Equipment("Emergency Communication Radio", "Communication", "Motorola APX8000",
                20, "Emergency HQ", "Multi-band handheld radio for field communication");
        e5.setStatus(EquipmentStatus.AVAILABLE);
        e5.setAvailableQuantity(20);

        Equipment e6 = new Equipment("Inflatable Rescue Boat", "Search & Rescue", "Zodiac ERB 420",
                3, "Flood Response Unit", "Inflatable boat for water rescue operations");
        e6.setStatus(EquipmentStatus.IN_USE);
        e6.setAvailableQuantity(1);

        equipmentRepository.save(e1);
        equipmentRepository.save(e2);
        equipmentRepository.save(e3);
        equipmentRepository.save(e4);
        equipmentRepository.save(e5);
        equipmentRepository.save(e6);

        log.info("===== Sample data initialised successfully =====");
        log.info("Teams: {}, Incidents: {}, Shelters: {}, Equipment: {}",
                teamRepository.count(), incidentRepository.count(),
                shelterRepository.count(), equipmentRepository.count());
    }
}
