package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
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
public class ParkingServiceTest {

  private static ParkingService parkingServiceUnderTest;

  @Mock
  private static InputReaderUtil inputReaderUtilMocked;
  @Mock
  private static ParkingSpotDao parkingSpotDaoMocked;
  @Mock
  private static TicketDao ticketDaoMocked;

  private Ticket ticket;
  private ParkingSpot parkingSpot;

  @BeforeEach
  public void setUp() {
    try{
      when(inputReaderUtilMocked.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      ticket = new Ticket();
      ticket.setInTime(LocalDateTime.now().minusHours(1));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");
    }catch (Exception e){
      e.printStackTrace();
      throw new RuntimeException("Failed to set up mocked objects.");
    }
  }

  @DisplayName("Check if incoming vehicles calls for tickets saving")
  @Test
  public void processIncomingVehicle_ShouldSaveTicket() {
    //ARRANGE
    try {
      when(inputReaderUtilMocked.readSelection()).thenReturn(1);
      when(ticketDaoMocked.saveTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDaoMocked.updateParking(any(ParkingSpot.class))).thenReturn(true);
      when(parkingSpotDaoMocked.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

      parkingServiceUnderTest = new ParkingService(inputReaderUtilMocked, parkingSpotDaoMocked, ticketDaoMocked);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up mocked objects.");
    }
    //ACT
    parkingServiceUnderTest.processIncomingVehicle();

    //ASSERT
    verify(parkingSpotDaoMocked, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDaoMocked, Mockito.times(1)).saveTicket(any(Ticket.class));
  }

  @DisplayName("Check if incoming vehicles calls for parking update")
  @Test
  public void processIncomingVehicle_ShouldUpdateParkingSpot() {
    //ARRANGE
    try {
      when(inputReaderUtilMocked.readSelection()).thenReturn(1);
      when(ticketDaoMocked.saveTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDaoMocked.updateParking(any(ParkingSpot.class))).thenReturn(true);
      when(parkingSpotDaoMocked.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

      parkingServiceUnderTest = new ParkingService(inputReaderUtilMocked, parkingSpotDaoMocked, ticketDaoMocked);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up mocked objects.");
    }
    //ACT
    parkingServiceUnderTest.processIncomingVehicle();

    //ASSERT
    verify(parkingSpotDaoMocked, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }

  @DisplayName("Check if exiting vehicles queries ticket")
  @Test
  public void processExitingVehicleTest_ShouldGetTicket() {
    //ARRANGE
    try {
      when(ticketDaoMocked.getTicket(anyString())).thenReturn(ticket);
      when(ticketDaoMocked.updateTicket(any(Ticket.class))).thenReturn(true);

      when(parkingSpotDaoMocked.updateParking(any(ParkingSpot.class))).thenReturn(true);

      parkingServiceUnderTest = new ParkingService(inputReaderUtilMocked, parkingSpotDaoMocked, ticketDaoMocked);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up mocked objects.");
    }
    //ACT
    parkingServiceUnderTest.processExitingVehicle();

    //ASSERT
    verify(ticketDaoMocked, Mockito.times(1)).getTicket(anyString());
  }

  @DisplayName("Check if exiting vehicles calls for parking spot update")
  @Test
  public void processExitingVehicleTest_ShouldUpdateParkingSpot() {
    //ARRANGE
    try {
      when(ticketDaoMocked.getTicket(anyString())).thenReturn(ticket);
      when(ticketDaoMocked.updateTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDaoMocked.updateParking(any(ParkingSpot.class))).thenReturn(true);
      parkingServiceUnderTest = new ParkingService(inputReaderUtilMocked, parkingSpotDaoMocked, ticketDaoMocked);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up mocked objects.");
    }
    //ACT
    parkingServiceUnderTest.processExitingVehicle();

    //ASSERT
    verify(parkingSpotDaoMocked, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }

  @DisplayName("Check if exiting vehicles calls for ticket update")
  @Test
  public void processExitingVehicleTest_ShouldUpdateTicket() {
    //ARRANGE
    try {
      when(ticketDaoMocked.getTicket(anyString())).thenReturn(ticket);
      when(ticketDaoMocked.updateTicket(any(Ticket.class))).thenReturn(true);

      when(parkingSpotDaoMocked.updateParking(any(ParkingSpot.class))).thenReturn(true);

      parkingServiceUnderTest = new ParkingService(inputReaderUtilMocked, parkingSpotDaoMocked, ticketDaoMocked);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up mocked objects.");
    }
    //ACT
    parkingServiceUnderTest.processExitingVehicle();

    //ASSERT
    verify(ticketDaoMocked, Mockito.times(1)).updateTicket(any(Ticket.class));
  }

}
