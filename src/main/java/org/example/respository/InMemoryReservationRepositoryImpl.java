package org.example.respository;

import org.example.domain.ReservationDetails;
import org.example.errors.InvalidReservationIdException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryReservationRepositoryImpl implements ReservationRepository {

    private final Map<UUID, Set<ReservationDetails>> reservationMapByVehicleUUId;
    private final Map<UUID, ReservationDetails> reservationMapByReservationUUId;

    public InMemoryReservationRepositoryImpl() {
        this.reservationMapByVehicleUUId = new ConcurrentHashMap<>();
        this.reservationMapByReservationUUId = new ConcurrentHashMap<>();
    }

    @Override
    public void save(ReservationDetails reservationDetails) {
        this.reservationMapByVehicleUUId
                .computeIfAbsent(reservationDetails.getVehicle().getId(), k -> new HashSet<>()).add(reservationDetails);
        this.reservationMapByReservationUUId.put(reservationDetails.getId(), reservationDetails);
    }

    @Override
    public ReservationDetails remove(UUID reservationDetailsId) {
        if (reservationMapByReservationUUId.containsKey(reservationDetailsId)) {
            ReservationDetails reservationDetailsToRemove = reservationMapByReservationUUId.remove(reservationDetailsId);
            Set<ReservationDetails> reservationDetailsForVehicle
                    = reservationMapByVehicleUUId.get(reservationDetailsToRemove.getVehicle().getId());
            reservationDetailsForVehicle.remove(reservationDetailsToRemove);
            return reservationDetailsToRemove;
        }
        throw new InvalidReservationIdException(reservationDetailsId.toString());
    }

    @Override
    public boolean isAvailableOnDates(UUID vehicleId, LocalDate fromDate, LocalDate toDate) {
        return !reservationMapByVehicleUUId.containsKey(vehicleId) ||
                checkIfDatesAreAvailable(vehicleId, fromDate, toDate);
    }

    @Override
    public void deleteAll() {
        reservationMapByVehicleUUId.clear();
        reservationMapByReservationUUId.clear();
    }

    /**
     * @return true if given vehicle is not reserved on given dates
     */
    private boolean checkIfDatesAreAvailable(UUID vehicleId, LocalDate fromDate, LocalDate toDate) {
        Set<ReservationDetails> reservationDetails = reservationMapByVehicleUUId.get(vehicleId);
        return reservationDetails.stream()
                .allMatch(details -> {
                    LocalDate reservedFrom = details.getFromDate();
                    LocalDate reservedTo = details.getToDate();
                    return checkIfDateRangesAreExclusive(reservedFrom, reservedTo, fromDate, toDate);
                });
    }

    private static boolean checkIfDateRangesAreExclusive(LocalDate reservedFrom, LocalDate reservedTo,
                                                         LocalDate newFrom, LocalDate newTo) {
        return (reservedTo.isBefore(newFrom) || newTo.isBefore(reservedFrom));
    }
}
