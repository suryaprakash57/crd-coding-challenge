package org.example.service;

import org.example.domain.ReservationDetails;
import org.example.domain.Vehicle;
import org.example.domain.VehicleType;
import org.example.errors.*;
import org.example.respository.ReservationRepository;
import org.example.respository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    private ReservationService service;
    private ReservationRepository reservationRepository;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        service = new ReservationServiceImpl(reservationRepository, vehicleRepository);
    }

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void testReserveByVehicleType(VehicleType type) {
        Map<VehicleType, Double> priceMap = new HashMap<>();
        priceMap.put(VehicleType.SEDAN, 40.0);
        priceMap.put(VehicleType.SUV, 80.0);
        priceMap.put(VehicleType.VAN, 48.4);
        priceMap.put(VehicleType.PICKUP_TRUCK, 60.0);
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        //2 days of reservation
        ReservationDetails result = service.reserveCarByType(type, fromDate,
                toDate, 100, 4);

        assertEquals(vehicle, result.getVehicle());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
        assertEquals(priceMap.get(type), result.getPrice());
    }

    @Test
    void failNonExistentVehicleReservationById() {
        VehicleType type = VehicleType.SEDAN;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(any())).thenThrow(new VehicleNotFoundException(""));
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        //2 days of reservation
        assertThrows(VehicleNotFoundException.class, () -> service.reserveCarByVehicleId(vehicle.getId(), fromDate,
                toDate, 100, 4));
    }

    @Test
    void failNonExistentVehicleReservationByType() {
        when(vehicleRepository.getVehiclesByType(any())).thenReturn(Set.of());
        when(vehicleRepository.getVehicleByVehicleId(any())).thenThrow(new VehicleNotFoundException(""));
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        //2 days of reservation
        assertThrows(ReservationNotPossibleException.class, () -> service.reserveCarByType(VehicleType.SEDAN, fromDate,
                toDate, 100, 4));
    }

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void testReserveByVehicleById(VehicleType type) {
        Map<VehicleType, Double> priceMap = new HashMap<>();
        priceMap.put(VehicleType.SEDAN, 40.0);
        priceMap.put(VehicleType.SUV, 80.0);
        priceMap.put(VehicleType.VAN, 48.4);
        priceMap.put(VehicleType.PICKUP_TRUCK, 60.0);
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        //2 days of reservation
        ReservationDetails result = service.reserveCarByVehicleId(vehicle.getId(), fromDate,
                toDate, 100, 4);

        assertEquals(vehicle, result.getVehicle());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
        assertEquals(priceMap.get(type), result.getPrice());
    }

    @Test
    void testVehicleReservationForSedanGreaterThan10Days() {
        VehicleType type = VehicleType.SEDAN;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);
        //10 days of reservation
        ReservationDetails result = service.reserveCarByType(type, fromDate,
                toDate, 100, 4);

        assertEquals(vehicle, result.getVehicle());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
        assertEquals(150.0, result.getPrice());
    }

    @Test
    void failVehicleReservationForConflictingDates() {
        VehicleType type = VehicleType.SEDAN;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(false);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);
        //10 days of reservation
        assertThrows(ReservationNotPossibleException.class, () -> service.reserveCarByType(type, fromDate,
                toDate, 100, 4));
    }

    @Test
    void failReservationRequestForInvalidDates() {
        VehicleType type = VehicleType.SEDAN;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = LocalDate.now().plusDays(10);
        //negative 10 days difference
        assertThrows(InvalidDateRangeException.class, () -> service.reserveCarByType(type, fromDate,
                toDate, 100, 4));
    }

    @Test
    void failReservationRequestForInvalidMileage() {
        VehicleType type = VehicleType.SEDAN;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);
        //negative 10 days difference
        assertThrows(IllegalMileageException.class, () -> service.reserveCarByType(type, fromDate,
                toDate, -100, 4));
    }

    @Test
    void failReservationRequestForInvalidLicenseYears() {
        VehicleType type = VehicleType.SEDAN;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);
        //negative 10 days difference
        assertThrows(IllegalLicenseYearsException.class, () -> service.reserveCarByType(type, fromDate,
                toDate, 100, -4));
    }

    @Test
    void testVehicleReservationForPickUpForYoungDriver() {
        VehicleType type = VehicleType.PICKUP_TRUCK;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);
        //10 days of reservation
        ReservationDetails result = service.reserveCarByType(type, fromDate,
                toDate, 100, 2);

        assertEquals(vehicle, result.getVehicle());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
        assertEquals(330.0, result.getPrice());
    }

    @Test
    void testGetOptions(){
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        Map<VehicleType, Double> result = service.getOptions(fromDate,
                toDate, 100, 2);

        Iterator<Map.Entry<VehicleType, Double>> iterator = result.entrySet().iterator();
        assertEquals(VehicleType.values().length, result.size());
        Map.Entry<VehicleType, Double> sedan = iterator.next();
        assertEquals(VehicleType.SEDAN, sedan.getKey());
        assertEquals(150.0, sedan.getValue());

        Map.Entry<VehicleType, Double> suv = iterator.next();
        assertEquals(VehicleType.SUV, suv.getKey());
        assertEquals(200.0, suv.getValue());

        Map.Entry<VehicleType, Double> van = iterator.next();
        assertEquals(VehicleType.VAN, van.getKey());
        assertEquals(242.0, van.getValue());

        Map.Entry<VehicleType, Double> pickup = iterator.next();
        assertEquals(VehicleType.PICKUP_TRUCK, pickup.getKey());
        assertEquals(330.0, pickup.getValue());
    }

    @Test
    void testReservationCancellation(){
        ReservationDetails reservationDetails = mock(ReservationDetails.class);
        UUID reservationId = UUID.randomUUID();
        when(reservationRepository.remove(reservationId)).thenReturn(reservationDetails);

        ReservationDetails details = service.cancelReservation(reservationId);

        assertEquals(reservationDetails, details);
    }

    @Test
    void testReservationModification(){
        VehicleType type = VehicleType.PICKUP_TRUCK;
        Vehicle vehicle = new Vehicle(type);
        when(vehicleRepository.getVehiclesByType(type)).thenReturn(Set.of(vehicle));
        when(vehicleRepository.getVehicleByVehicleId(vehicle.getId())).thenReturn(vehicle);
        when(reservationRepository.isAvailableOnDates(any(), any(), any())).thenReturn(true);
        ReservationDetails reservationDetails = mock(ReservationDetails.class);
        when(reservationDetails.getVehicle()).thenReturn(vehicle);
        when(reservationRepository.remove(any())).thenReturn(reservationDetails);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);
        //10 days of reservation
        ReservationDetails result = service.modifyReservation(UUID.randomUUID(), fromDate,
                toDate, 100, 2);

        assertEquals(vehicle, result.getVehicle());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
        assertEquals(330.0, result.getPrice());
    }

    private Set<Vehicle> createVehicles(List<VehicleType> vehicleTypes) {
        return vehicleTypes.stream().map(
                Vehicle::new
        ).collect(Collectors.toSet());
    }

}