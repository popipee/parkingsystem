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
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDao parkingSpotDAO;
  @Mock
  private static TicketDao ticketDAO;

  @BeforeEach
  private void setUpPerTest() {
    try {
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      Ticket ticket = new Ticket();
      ticket.setInTime(LocalDateTime.now().minusHours(1));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      parkingServiceUnderTest = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects");
    }
  }

  @DisplayName("check if exiting vehicles calls for tickets update and parking spot update")
  @Test
  public void processExitingVehicleTest() {
    //GIVEN

    //WHEN
    parkingServiceUnderTest.processExitingVehicle();

    //THEN
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
  }


  @DisplayName("Check if incoming vehicles calls for tickets update and parking spot update")
  @Test
  public void processIncomingVehicle() {
    //GIVEN

    //WHEN
    parkingServiceUnderTest.processExitingVehicle();

    //THEN
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
  }

}
