package com.disaster.response;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Disaster Response Coordination Platform.
 * Uses Spring Boot with MVC architecture and multiple design patterns:
 * - Strategy Pattern: Dynamic task assignment based on severity/proximity/resources
 * - Observer Pattern: Real-time incident update notifications
 * - Command Pattern: Auditable action execution with undo support
 * - Factory Pattern (Creational): Response team instantiation
 * - Facade Pattern (Structural): Simplified emergency response coordination
 */
@SpringBootApplication
public class DisasterResponseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisasterResponseApplication.class, args);
    }
}
