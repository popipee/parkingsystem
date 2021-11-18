package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorServiceUnderTest;

    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorServiceUnderTest = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("Calculate the fare for a car parked for one hour")
    public void calculateFare_forAOneHourParkedTimeCar_ShouldReturn1TimeTheCarFareRatePerHour(){
        //GIVEN
        LocalDateTime inTime  = LocalDateTime.now();
        inTime = inTime.minusHours(1);
        LocalDateTime outTime  = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorServiceUnderTest.calculateFare(ticket);

        //THEN
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @DisplayName("Calculate the fare for a bike parked for one hour")
    @Test
    public void calculateFare_forAOneHourParkedTimeBike_ShouldReturn1TimeTheCarFareRatePerHour(){
        //GIVEN
        LocalDateTime inTime  = LocalDateTime.now();
        inTime = inTime.minusHours(1);
        LocalDateTime outTime  = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorServiceUnderTest.calculateFare(ticket);

        //THEN
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @DisplayName("Calculate the fare for an unknown locomotion type for one hour")
    @Test
    public void calculateFare_forAOneHourParkedTimeUnknownLocomotionType_ShouldThrowException(){
        //GIVEN
        LocalDateTime inTime  = LocalDateTime.now();
        inTime = inTime.minusHours(1);
        LocalDateTime outTime  = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN

        // THEN
        assertThrows(NullPointerException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
    }

    @DisplayName("Calculate the fare for a bike for an incoming date which is after an outgoing one.")
    @Test
    public void calculate_FareBikeWithFutureInTimeOf1Hour_ShouldReturnIllegalArgumentException(){
        //GIVEN
        LocalDateTime inTime  = LocalDateTime.now();
        inTime = inTime.plusHours(1);
        LocalDateTime outTime  = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN

        //THEN
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
    }

    @DisplayName("Calculate the fare for a bike for duration parked time of 45 min (less than 1 hour)")
    @Test
    public void calculate_FareBikeWithLessThanOneHourParkingTime_ShouldReturn75PercentOfBikeFarePrice(){
        //GIVEN
        LocalDateTime inTime  = LocalDateTime.now();
        inTime = inTime.minusMinutes(45);
        LocalDateTime outTime  = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorServiceUnderTest.calculateFare(ticket);

        //THEN
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @DisplayName("Calculate the fare for a car for duration parked time of 45 min (less than 1 hour)")
    @Test
    public void calculate_FareCarWithLessThanOneHourParkingTime_ShouldReturn75PercentOfBikeFarePrice(){
        //GIVEN
        LocalDateTime inTime = LocalDateTime.now();
        inTime = inTime.minusMinutes(45);//45 minutes parking time should give 3/4th parking fare
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorServiceUnderTest.calculateFare(ticket);

        //THEN
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @DisplayName("Calculate the fare for a car for duration parked time of more than a day")
    @Test
    public void calculate_FareCarWithADayParkingTime_ShouldReturnFareCarPricePerHourTimes24(){
        //GIVEN
        LocalDateTime inTime  = LocalDateTime.now();
        inTime = inTime.minusDays(1);
        LocalDateTime outTime  = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorServiceUnderTest.calculateFare(ticket);

        //THEN
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

}
