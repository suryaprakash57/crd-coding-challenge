package org.example.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Vehicle {
    private final VehicleType type;
    private final UUID id;

    public Vehicle(VehicleType type) {
        this.type = type;
        this.id = UUID.randomUUID();
    }
}
