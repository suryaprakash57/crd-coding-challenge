package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.ReservationDetails;
import org.example.domain.Vehicle;
import org.example.domain.VehicleType;
import org.example.errors.ReservationNotPossibleException;
import org.example.respository.ReservationRepository;
import org.example.respository.VehicleRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.service.InputValidator.validateInputs;

@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {


    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;


    @Override
    public ReservationDetails reserveCarByVehicleId(UUID vehicleId, LocalDate fromDate, LocalDate toDate, int mileage, int licenseYears) {
        if (reservationRepository.isAvailableOnDates(vehicleId, fromDate, toDate)) {
            ReservationDetails reservationDetails = buildReservationDetails(vehicleId, fromDate, toDate, mileage, licenseYears);
            reservationRepository.save(reservationDetails);
            log.info("Reserved car details:{}", reservationDetails);
            return reservationDetails;
        }
        throw new ReservationNotPossibleException(vehicleId, fromDate, toDate);
    }

    private ReservationDetails buildReservationDetails(UUID vehicleId, LocalDate fromDate, LocalDate toDate, int mileage, int licenseYears) {
        return ReservationDetails.ReservationDetailsBuilder.newInstance()
                .vehicle(vehicleRepository.getVehicleByVehicleId(vehicleId))
                .fromDate(fromDate)
                .toDate(toDate)
                .mileage(mileage)
                .licenseYears(licenseYears)
                .build();
    }

    @Override
    public ReservationDetails reserveCarByType(VehicleType type, LocalDate fromDate, LocalDate toDate, int mileage, int licenseYears) {
        ReservationDetails reservationDetails = vehicleRepository.getVehiclesByType(type)
                .stream()
                .map(Vehicle::getId)
                .filter(vehicleId -> reservationRepository.isAvailableOnDates(vehicleId, fromDate, toDate))
                .findAny()
                .map(vehicleId -> {
                    ReservationDetails details = buildReservationDetails(vehicleId, fromDate, toDate, mileage, licenseYears);
                    reservationRepository.save(details);
                    return details;
                })
                .orElseThrow(() -> new ReservationNotPossibleException(type, fromDate, toDate));
        log.info("Reserved car details:{}", reservationDetails);
        return reservationDetails;
    }

    @Override
    public ReservationDetails modifyReservation(UUID reservationId, LocalDate fromDate, LocalDate toDate, int mileage, int licenseYears) {
        ReservationDetails reservationDetails = reservationRepository.remove(reservationId);
        ReservationDetails modifiedReservationDetails = reserveCarByVehicleId(reservationDetails.getVehicle().getId(), fromDate, toDate, mileage, licenseYears);
        log.info("Old reservation:{} modified to:{}", reservationDetails, modifiedReservationDetails);
        return modifiedReservationDetails;
    }

    @Override
    public ReservationDetails cancelReservation(UUID reservationId) {
        ReservationDetails details = reservationRepository.remove(reservationId);
        log.info("Removed reservation details with id:{}", reservationId);
        return details;
    }

    @Override
    public Map<VehicleType, Double> getOptions(LocalDate from, LocalDate to, int mileage, int licenseYears) {
        validateInputs(from, to, mileage, licenseYears);
        Map<VehicleType, Double> options = getVehicleTypePriceOptions(from, to, mileage, licenseYears);
        log.info("Vehicle options priced by type in un-sorted order:{}", options);
        Map<VehicleType, Double> sortedOptions = sortBasedOnPrice(options);
        log.info("Vehicle options priced by type in sorted order:{}", sortedOptions);
        return sortedOptions;
    }

    private static LinkedHashMap<VehicleType, Double> sortBasedOnPrice(Map<VehicleType, Double> options) {
        return options.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private static Map<VehicleType, Double> getVehicleTypePriceOptions(LocalDate from, LocalDate to, int mileage, int licenseYears) {
        return Arrays.stream(VehicleType.values())
                .collect(Collectors.toMap(type -> type, type ->
                        type.calculatePrice(getDays(from, to), mileage, licenseYears)));
    }

    private static long getDays(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }
}
