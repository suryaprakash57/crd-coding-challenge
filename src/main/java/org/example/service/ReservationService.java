package org.example.service;

import org.example.domain.ReservationDetails;
import org.example.domain.VehicleType;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface ReservationService {

    ReservationDetails reserveCarByVehicleId(UUID id, LocalDate from, LocalDate to, int mileage, int licenseYears);

    ReservationDetails reserveCarByType(VehicleType type, LocalDate from, LocalDate to, int mileage, int licenseYears);

    ReservationDetails modifyReservation(UUID reservationId, LocalDate from, LocalDate to, int mileage, int licenseYears);

    ReservationDetails cancelReservation(UUID reservationId);

    Map<VehicleType, Double> getOptions(LocalDate from, LocalDate to, int mileage, int licenseYears);
}