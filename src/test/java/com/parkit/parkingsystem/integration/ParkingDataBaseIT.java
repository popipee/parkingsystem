package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private Ticket ticket;
    private ParkingSpot parkingSpot;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries(); //clear database entries to prepare for future sql request

        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot();
    }

    @DisplayName("Test inTime is populated properly in database for a car an parking slot is not available")
    @Test
    @Order(1)
    public void testParkingACar() throws Exception{
        //GIVEN
        //Mocking a car in row 1 and Reg ABCDEF
        when(inputReaderUtil.readSelection()).thenReturn(1); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); //mock reader selection, so it always returns 'ABCDEF' when reading registration number

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());

        //THEN
        assertThat(ticket.getInTime()).isNotNull();
        assertThat(parkingSpot.isAvailable()).isFalse();
    }

    @DisplayName("Test outime and price are properly populated in database and parking slot is free")
    @Test
    @Order(2)
    public void testParkingLotExit() throws Exception{
        //GIVEN
        //Mocking a car in row 1 and Reg ABCDEF
        when(inputReaderUtil.readSelection()).thenReturn(1); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); //mock reader selection, so it always returns 'ABCDEF' when reading registration number

        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

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


    @DisplayName("Multiple cars with recurring users")
    @Test
    private void testMultipleEnteringWithRecurringUser() throws Exception{
        //We Spy on LocalDateTime time to manipulate time int database writing


        //-------------GIVEN----------------
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //---INCOMING---
        //-CAR1
        //Mocking a car in row 1 and Reg ABCDEF
        Mockito.doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber(); //mock reader selection, so it always returns 'ABCDEF' when reading registration number
        Mockito.doReturn(1).when(inputReaderUtil).readSelection(); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        when(LocalDateTime.now()).thenReturn(LocalDateTime.now().minusHours(2));
        //Creating Car
        parkingService.processIncomingVehicle();

        //-CAR2
        //Mocking a car in row 1 and Reg AZERTY
        when(inputReaderUtil.readSelection()).thenReturn(1); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AZERTY"); //mock reader selection, so it always returns 'ABCDEF' when reading registration number
        parkingService.processIncomingVehicle();
        Ticket ticket2 = new Ticket();
        ticket2 = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        //-CAR3
        //Mocking a car in row 1 and Reg UIOP
        when(inputReaderUtil.readSelection()).thenReturn(1); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("UIOP"); //mock reader selection, so it always returns 'ABCDEF' when reading registration number
        parkingService.processIncomingVehicle();
        Ticket ticket3 = new Ticket();
        ticket3 = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        //---EXITING/ENTERING---
        //-CAR1
        //Mocking a car in row 1 and Reg ABCDEF
        Mockito.doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber(); //mock reader selection, so it always returns 'ABCDEF' when reading registration number
        Mockito.doReturn(1).when(inputReaderUtil).readSelection(); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        //Creating Car
        parkingService.processExitingVehicle();
        Thread.sleep(1000);
        parkingService.processIncomingVehicle();
        Thread.sleep(1000);

        //-------------WHEN----------------
        parkingService.processExitingVehicle();
        Thread.sleep(1000);
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        //-------------THEN----------------
        assertThat(ticket.getAlreadyExists()).isTrue();
        assertThat(ticket2.getAlreadyExists()).isFalse();
        assertThat(ticket3.getAlreadyExists()).isFalse();

    }

}
