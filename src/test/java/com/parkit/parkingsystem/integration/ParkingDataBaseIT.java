package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

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

    }


    @AfterAll
    private static void tearDown(){
        //TODO : EFE - what is this ? to be erased ?
    }

    @Order(1)
    @DisplayName("Test intime is populated properly in database for a car")
    @Test
    public void testParkingACar() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Ticket ticket = new Ticket();

        //WHEN
        parkingService.processIncomingVehicle();
        ticket = ticketDAO.getTicket("ABCDEF");

        //THEN
        assertThat(ticket.getInTime()).isNotNull();
        //Could create error if the test last longer than a minute
//        assertThat(ticket.getInTime().getHour()).isEqualTo(LocalDateTime.now().getHour());
//        assertThat(ticket.getInTime().getMinute()).isEqualTo(LocalDateTime.now().getMinute()); //Could create error if the test last longer than a minute

        //check manually that a ticket is actually saved in DB and Parking table is updated with availability
    }

    @Order(2)
    @DisplayName("Test outime and price are populated properly in database for a car")
    @Test
    public void testParkingLotExit() throws Exception{
        //GIVEN
        testParkingACar(); //Creation of a car
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Ticket ticket = new Ticket();

        //WHEN
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket("ABCDEF");

        //THEN
        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getPrice()).isNotNull();
    }

}
