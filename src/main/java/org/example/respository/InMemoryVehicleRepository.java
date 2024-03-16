package org.example.respository;

import org.example.domain.Vehicle;
import org.example.domain.VehicleType;
import org.example.errors.VehicleNotFoundException;
import org.example.errors.VehicleTypeNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<VehicleType, Set<Vehicle>> vehiclesByType;
    private final Map<UUID, Vehicle> vehicleIdToVehicleTypeMap;

    public InMemoryVehicleRepository() {
        this.vehiclesByType = new ConcurrentHashMap<>();
        this.vehicleIdToVehicleTypeMap = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Vehicle vehicle) {
        this.vehiclesByType
                .computeIfAbsent(vehicle.getType(), k -> new HashSet<>()).add(vehicle);
        this.vehicleIdToVehicleTypeMap.put(vehicle.getId(), vehicle);
    }

    @Override
    public void saveAll(Collection<Vehicle> vehicles) {
        vehicles.forEach(vehicle -> {
            this.vehiclesByType
                    .computeIfAbsent(vehicle.getType(), k -> new HashSet<>()).add(vehicle);
            this.vehicleIdToVehicleTypeMap.put(vehicle.getId(), vehicle);
        });
    }

    @Override
    public Set<Vehicle> getVehiclesByType(VehicleType vehicleType) {
        return this.vehiclesByType.get(vehicleType);
    }

    @Override
    public Vehicle getVehicleByVehicleId(UUID id) {
        checkIfVehicleExists(id);
        return this.vehicleIdToVehicleTypeMap.get(id);
    }

    @Override
    public void deleteAll() {
        this.vehiclesByType.clear();
        this.vehicleIdToVehicleTypeMap.clear();
    }

    private void checkIfVehicleExists(UUID id) {
        if (!this.vehicleIdToVehicleTypeMap.containsKey(id)) {
            throw new VehicleNotFoundException(id.toString());
        }
    }

    private void checkIfVehicleTypeExists(UUID id) {
        if (!this.vehicleIdToVehicleTypeMap.containsKey(id)) {
            throw new VehicleTypeNotFoundException(id.toString());
        }
    }
}
