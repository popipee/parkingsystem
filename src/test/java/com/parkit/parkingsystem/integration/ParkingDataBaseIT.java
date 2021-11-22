package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
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
        //Mocking a car in row 1 and Reg ABCDEF
        when(inputReaderUtil.readSelection()).thenReturn(1); //mock reader selection so it always modify ticket id = 1
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); //mock reader selection so it always return ABCDEF when reading registration number
        dataBasePrepareService.clearDataBaseEntries(); //clear database entries to prepare for future sql request

        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot();
    }

    @DisplayName("Test intime is populated properly in database for a car an parking slot is not available")
    @Test
    @Order(1)
    public void testParkingACar() throws Exception{
        //GIVEN
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
        testParkingACar(); //Creation of a car
        Thread.sleep(100);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());

        //THEN
        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getPrice()).isNotNull();
        assertThat(parkingSpot.isAvailable()).isTrue();
    }

    //TODO : 5 percent discount test with DB
//    @DisplayName("Apply a 5 percent discount on an already existing ticket")
//    @Test
//    @Order(3)
//    public void Test_AParkingLotExitForARecurringUser_ShouldReturnAPriceWithA5PercentDiscount() throws Exception{
//        //GIVEN
//        //Car comes in and goes out;
//        testParkingLotExit();
//        //Then it comes again
//        testParkingACar();
//
//        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//        Ticket ticket = new Ticket();
//        ParkingSpot parkingSpot = new ParkingSpot();
//
//
//        //WHEN
//        parkingService.processExitingVehicle();
//        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
//
//
//
//        //THEN
//
//
//    }

}
