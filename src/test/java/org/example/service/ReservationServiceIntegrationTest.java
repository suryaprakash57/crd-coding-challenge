package org.example.service;

import org.example.domain.ReservationDetails;
import org.example.domain.Vehicle;
import org.example.domain.VehicleType;
import org.example.errors.ReservationNotPossibleException;
import org.example.respository.InMemoryReservationRepositoryImpl;
import org.example.respository.InMemoryVehicleRepository;
import org.example.respository.ReservationRepository;
import org.example.respository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceIntegrationTest {

    private ReservationService service;
    private ReservationRepository reservationRepository;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = new InMemoryReservationRepositoryImpl();
        vehicleRepository = new InMemoryVehicleRepository();
        service = new ReservationServiceImpl(reservationRepository, vehicleRepository);
    }

    @AfterEach
    void cleanUp(){
        vehicleRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    @Test
    void shouldReserveByVehicleType() {
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        new Vehicle(VehicleType.VAN),
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        //2 days of reservation
        ReservationDetails result = service.reserveCarByType(typeToTest, fromDate,
                toDate, 100, 4);

        assertEquals(typeToTest, result.getVehicle().getType());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
        assertEquals(48.4, result.getPrice());
    }

    @Test
    void shouldReserveSameDatesMultipleVehicleOfSameType() {
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        new Vehicle(VehicleType.VAN),
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.VAN),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        //2 days of reservation
        ReservationDetails firstReservation = service.reserveCarByType(typeToTest, fromDate,
                toDate, 100, 4);
        ReservationDetails secondReservation = service.reserveCarByType(typeToTest, fromDate,
                toDate, 100, 4);

        assertEquals(typeToTest, firstReservation.getVehicle().getType());
        assertEquals(fromDate, firstReservation.getFromDate());
        assertEquals(toDate, firstReservation.getToDate());
        assertEquals(48.4, firstReservation.getPrice());
        assertEquals(typeToTest, secondReservation.getVehicle().getType());
        assertEquals(fromDate, secondReservation.getFromDate());
        assertEquals(toDate, secondReservation.getToDate());
        assertEquals(48.4, secondReservation.getPrice());
        assertNotEquals(firstReservation.getVehicle(), secondReservation.getVehicle());
    }

    @Test
    void shouldReserveDifferentDatesSameVehicle() {
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        new Vehicle(VehicleType.VAN),
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);
        //2 days of reservation
        ReservationDetails firstReservation = service.reserveCarByType(typeToTest, fromDate,
                toDate, 100, 4);
        LocalDate fromDate2 = LocalDate.now().plusDays(5);
        LocalDate toDate2 = LocalDate.now().plusDays(7); //2 days
        ReservationDetails secondReservation = service.reserveCarByType(typeToTest,
                fromDate2,
                toDate2, 100, 4);

        assertEquals(typeToTest, firstReservation.getVehicle().getType());
        assertEquals(fromDate, firstReservation.getFromDate());
        assertEquals(toDate, firstReservation.getToDate());
        assertEquals(48.4, firstReservation.getPrice());
        assertEquals(typeToTest, secondReservation.getVehicle().getType());
        assertEquals(fromDate2, secondReservation.getFromDate());
        assertEquals(toDate2, secondReservation.getToDate());
        assertEquals(48.4, secondReservation.getPrice());
        assertEquals(firstReservation.getVehicle(), secondReservation.getVehicle());
    }

    @Test
    void shouldReserveByVehicleId() {
        Vehicle van = new Vehicle(VehicleType.VAN);
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        van,
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);
        //2 days of reservation
        ReservationDetails firstReservation = service.reserveCarByVehicleId(van.getId(), fromDate,
                toDate, 100, 4);
        LocalDate fromDate2 = LocalDate.now().plusDays(5);
        LocalDate toDate2 = LocalDate.now().plusDays(7); //2 days
        ReservationDetails secondReservation = service.reserveCarByVehicleId(van.getId(),
                fromDate2,
                toDate2, 100, 4);

        assertEquals(typeToTest, firstReservation.getVehicle().getType());
        assertEquals(fromDate, firstReservation.getFromDate());
        assertEquals(toDate, firstReservation.getToDate());
        assertEquals(48.4, firstReservation.getPrice());
        assertEquals(typeToTest, secondReservation.getVehicle().getType());
        assertEquals(fromDate2, secondReservation.getFromDate());
        assertEquals(toDate2, secondReservation.getToDate());
        assertEquals(48.4, secondReservation.getPrice());
        assertEquals(firstReservation.getVehicle(), secondReservation.getVehicle());
    }

    @Test
    void shouldModifyReservation() {
        Vehicle van = new Vehicle(VehicleType.VAN);
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        van,
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);
        //2 days of reservation
        ReservationDetails firstReservation = service.reserveCarByVehicleId(van.getId(), fromDate,
                toDate, 100, 4);
        LocalDate fromDate2 = LocalDate.now().plusDays(5);
        LocalDate toDate2 = LocalDate.now().plusDays(17); //12 days
        ReservationDetails secondReservation = service.modifyReservation(firstReservation.getId(),
                fromDate2,
                toDate2, 100, 4);

        assertEquals(typeToTest, firstReservation.getVehicle().getType());
        assertEquals(fromDate, firstReservation.getFromDate());
        assertEquals(toDate, firstReservation.getToDate());
        assertEquals(48.4, firstReservation.getPrice());
        assertEquals(typeToTest, secondReservation.getVehicle().getType());
        assertEquals(fromDate2, secondReservation.getFromDate());
        assertEquals(toDate2, secondReservation.getToDate());
        assertEquals(290.4, secondReservation.getPrice());
        assertEquals(firstReservation.getVehicle(), secondReservation.getVehicle());
    }

    @Test
    void shouldCancelReservationAndRebookSameDates() {
        Vehicle van = new Vehicle(VehicleType.VAN);
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        van,
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);
        //2 days of reservation
        ReservationDetails reservationToBeCancelled = service.reserveCarByVehicleId(van.getId(), fromDate,
                toDate, 100, 4);
        service.cancelReservation(reservationToBeCancelled.getId());
        ReservationDetails secondReservation = service.reserveCarByVehicleId(van.getId(),
                fromDate,
                toDate, 100, 4);

        assertEquals(typeToTest, reservationToBeCancelled.getVehicle().getType());
        assertEquals(fromDate, reservationToBeCancelled.getFromDate());
        assertEquals(toDate, reservationToBeCancelled.getToDate());
        assertEquals(48.4, reservationToBeCancelled.getPrice());
        assertEquals(typeToTest, secondReservation.getVehicle().getType());
        assertEquals(fromDate, secondReservation.getFromDate());
        assertEquals(toDate, secondReservation.getToDate());
        assertEquals(48.4, secondReservation.getPrice());
        assertEquals(reservationToBeCancelled.getVehicle(), secondReservation.getVehicle());
    }

    @Test
    void failSameDateReservation() {
        Vehicle van = new Vehicle(VehicleType.VAN);
        vehicleRepository.saveAll(
                Set.of(new Vehicle(VehicleType.SEDAN),
                        van,
                        new Vehicle(VehicleType.SUV),
                        new Vehicle(VehicleType.PICKUP_TRUCK))
        );
        VehicleType typeToTest = VehicleType.VAN;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);
        //2 days of reservation
        ReservationDetails reservationToBeCancelled = service.reserveCarByVehicleId(van.getId(), fromDate,
                toDate, 100, 4);
        assertThrows(ReservationNotPossibleException.class, ()-> service.reserveCarByVehicleId(van.getId(),
                fromDate,
                toDate, 100, 4));
    }
}
