package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static final DataBaseTestConfig DATA_BASE_TEST_CONFIG = new DataBaseTestConfig();
  private static ParkingSpotDao parkingSpotDAO;
  private static TicketDao ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;
  @Mock
  private static InputReaderUtil inputReaderUtil;
  private Ticket ticket;
  private ParkingSpot parkingSpot;
  private ParkingService parkingService;

  @BeforeAll
  private static void setUp() {
    parkingSpotDAO = new ParkingSpotDao();
    parkingSpotDAO.dataBaseConfig = DATA_BASE_TEST_CONFIG;
    ticketDAO = new TicketDao();
    ticketDAO.dataBaseConfig = DATA_BASE_TEST_CONFIG;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    dataBasePrepareService.clearDataBaseEntries(); //clear database entries to prepare for future sql request
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    //Mocking a vehicle registration number to ABCDEF whatever it is a car or a bike
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    ticket = new Ticket();
    parkingSpot = new ParkingSpot();
  }


  @DisplayName("Test inTime is populated properly in database for a car an parking slot is not available")
  @Test
  public void testParkingACar() throws Exception {
    //GIVEN
    //mock reader selection, so it always chooses type 1 (i.e. CAR)
    when(inputReaderUtil.readSelection()).thenReturn(1);

    //WHEN
    parkingService.processIncomingVehicle();
    ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
    parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());

    //THEN
    assertThat(ticket.getInTime()).isNotNull();
    assertThat(parkingSpot.isAvailable()).isFalse();
  }


  @DisplayName("Test outTime and price are properly populated in database and parking slot is free")
  @Test
  public void testParkingLotExit() throws Exception {
    //GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1); //mock reader selection, so it always chooses type 1 (i.e. CAR)
    testParkingACar();


    //WHEN
    parkingService.processExitingVehicle();
    ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
    parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());

    //THEN
    assertThat(ticket.getOutTime()).isNotNull();
    assertThat(ticket.getPrice()).isNotNull();
    assertThat(ticket.getAlreadyExists()).isFalse();
    assertThat(parkingSpot.isAvailable()).isTrue();
  }

  @Disabled
  @DisplayName("Testing 5% discount for a recurring car. REAL so it lasts 45min")
  @Test
  public void testReal5PercentDiscountEntering() throws Exception {
    //-------------GIVEN----------------
    //---INCOMING---
    //-CAR1
    //Mocking a car registered ABCDEF
    Mockito.doReturn(1).when(inputReaderUtil).readSelection(); //mock reader selection, so it always chooses type 1 (i.e. CAR)
    parkingService.processIncomingVehicle();
    Thread.sleep(1000);

    //---EXITING/ENTERING---
    parkingService.processExitingVehicle();
    Thread.sleep(1000);
    parkingService.processIncomingVehicle();
    Thread.sleep(45 * 60 * 1000);

    //-------------WHEN----------------
    parkingService.processExitingVehicle();
    ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

    //-------------THEN----------------
    assertThat(ticket.getAlreadyExists()).isTrue();
    assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR * 0.95);

  }

  @DisplayName("Testing 5% discount for a recurring car.")
  @Test
  public void testSimulated5PercentDiscountEntering() throws Exception {
    //-------------GIVEN----------------
    Ticket ticketNumberTwo;
    //---INCOMING---
    when(inputReaderUtil.readSelection()).thenReturn(1);
    parkingService.processIncomingVehicleSpecialInTimeDate(LocalDateTime.now().minusMinutes(45));

    //---EXITING/ENTERING---
    parkingService.processExitingVehicle();
    ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
    parkingService.processIncomingVehicle();

    //-------------WHEN----------------
    parkingService.processExitingVehicleSpecialDate(LocalDateTime.now().plusMinutes(45));
    ticketNumberTwo = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

    //-------------THEN----------------
    assertThat(ticket.getAlreadyExists()).isFalse();
    assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    assertThat(ticketNumberTwo.getAlreadyExists()).isTrue();
    assertThat(ticketNumberTwo.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR * 0.95);
  }

}
