package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
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

    @DisplayName("Test outTime and price are properly populated in database and parking slot is free")
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

    @Disabled
    @DisplayName("Testing 5% discount for a recurring car. REAL so it lasts 45min")
    @Test
    @Order(3)
    public void testReal5PercentDiscountEntering() throws Exception{
        //We Spy on LocalDateTime time to manipulate time int database writing

        //-------------GIVEN----------------
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //---INCOMING---
        //-CAR1
        //Mocking a car registered ABCDEF
        Mockito.doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber(); //mock reader selection, so it always returns 'ABCDEF' when reading registration number
        Mockito.doReturn(1).when(inputReaderUtil).readSelection(); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        parkingService.processIncomingVehicle();
        Thread.sleep(1000);

        //---EXITING/ENTERING---
        parkingService.processExitingVehicle();
        Thread.sleep(1000);
        parkingService.processIncomingVehicle();
        Thread.sleep(45*60*1000);

        //-------------WHEN----------------
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        //-------------THEN----------------
        assertThat(ticket.getAlreadyExists()).isTrue();
        assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR * 0.95);

    }

    @DisplayName("Testing 5% discount for a recurring car.")
    @Test
    @Order(4)
    public void testSimulated5PercentDiscountEntering() throws Exception{
        //We Spy on LocalDateTime time to manipulate time int database writing

        //-------------GIVEN----------------
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Ticket ticketNumberTwo = new Ticket();
        //---INCOMING---
        //-CAR1
        //Mocking a car registered ABCDEF
        Mockito.doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber(); //mock reader selection, so it always returns 'ABCDEF' when reading registration number
        Mockito.doReturn(1).when(inputReaderUtil).readSelection(); //mock reader selection, so it always chooses type 1 (i.e. CAR)
        parkingService.processIncomingVehicle();
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        ticket.setInTime(ticket.getInTime().minusMinutes(45));
        ticketDAO.updateTicketInTime(ticket);


        //---EXITING/ENTERING---
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        Thread.sleep(1000);

        parkingService.processIncomingVehicle();

        //---Modifying Database incoming Ticket---
        //--First ticket to be modified (need top modify this one because if not, it would be anterior to last ticket afterwards)

        //--Second Ticket

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
