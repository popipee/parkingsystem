package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorServiceUnderTest;

  private Ticket ticket;
  private LocalDateTime inTime;
  private LocalDateTime outTime;

  @BeforeAll
  private static void setUp() {
    fareCalculatorServiceUnderTest = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    inTime = LocalDateTime.now();
    outTime = LocalDateTime.now();
    ticket = new Ticket();
    ticket.setOutTime(outTime);

  }


  @Test
  @DisplayName("Calculate the fare for a car parked for one hour")
  public void calculateFare_forAOneHourParkedTimeCar_ShouldReturn1TimeTheCarFareRatePerHour() {
    //GIVEN
    inTime = inTime.minusHours(1);
    ticket.setInTime(inTime);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setParkingSpot(parkingSpot);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);

    //THEN
    assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
  }

  @DisplayName("Calculate the fare for a bike parked for one hour")
  @Test
  public void calculateFare_forAOneHourParkedTimeBike_ShouldReturn1TimeTheCarFareRatePerHour() {
    //GIVEN
    inTime = inTime.minusHours(1);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setParkingSpot(parkingSpot);

    ticket.setInTime(inTime);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);

    //THEN
    assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
  }

  @DisplayName("Calculate the fare for an unknown locomotion type for one hour")
  @Test
  public void calculateFare_forAOneHourParkedTimeUnknownLocomotionType_ShouldThrowException() {
    //GIVEN
    inTime = inTime.minusHours(1);
    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
    ticket.setParkingSpot(parkingSpot);

    //WHEN

    // THEN
    assertThrows(NullPointerException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
  }

  @DisplayName("Calculate the fare for a bike for an incoming date which is after an outgoing one.")
  @Test
  public void calculate_FareBikeWithFutureInTimeOf1Hour_ShouldReturnIllegalArgumentException() {
    //GIVEN
    inTime = inTime.plusHours(1);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);

    //WHEN

    //THEN
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
  }

  @DisplayName("Calculate the fare for a bike for duration parked time of 45 min (less than 1 hour)")
  @Test
  public void calculate_FareBikeWithLessThanOneHourParkingTime_ShouldReturn75PercentOfBikeFarePrice() {
    //GIVEN
    inTime = inTime.minusMinutes(45);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);

    //THEN
    assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

  @DisplayName("Calculate the fare for a car for duration parked time of 45 min (less than 1 hour)")
  @Test
  public void calculate_FareCarWithLessThanOneHourParkingTime_ShouldReturn75PercentOfBikeFarePrice() {
    //GIVEN
    inTime = inTime.minusMinutes(45);//45 minutes parking time should give 3/4th parking fare
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);

    //THEN
    assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @DisplayName("Calculate the fare for a car for duration parked time of more than a day")
  @Test
  public void calculate_FareCarWithADayParkingTime_ShouldReturnFareCarPricePerHourTimes24() {
    //GIVEN
    inTime = inTime.minusDays(1);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);

    //THEN
    assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @DisplayName("As a user, I want to be able to park for 30 minutes or less without having to pay.")
  @Test
  public void calculate_FareForACarWithAParkingTimeOf30Minutes_ShouldReturnAFreeTicketPrice() {
    //GIVEN
    inTime = inTime.minusMinutes(30);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);

    //THEN
    assertThat(ticket.getPrice()).isEqualTo(0);
  }

  @DisplayName("As a user, I want to get a discount of 5% when I use the parking garage regularly.")
  @Test
  public void calculate_fareForACarThatHasAlreadyParkedInParking_ShouldReturnA5PercentDiscountOnBasicPrice() {
    //GIVEN
    inTime = inTime.minusHours(1);
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setAlreadyExists(true);

    //WHEN
    fareCalculatorServiceUnderTest.calculateFare(ticket);


    //THEN
    assertThat(ticket.getPrice()).isEqualTo((Fare.CAR_RATE_PER_HOUR) * (0.95));

  }

}
