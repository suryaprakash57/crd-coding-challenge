package org.example.respository;

import org.example.domain.Vehicle;
import org.example.domain.VehicleType;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface VehicleRepository {
    void save(Vehicle vehicle);

    void saveAll(Collection<Vehicle> vehicles);

    Set<Vehicle> getVehiclesByType(VehicleType vehicleType);

    Vehicle getVehicleByVehicleId(UUID id);

    void deleteAll();
}
