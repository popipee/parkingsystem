package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is the service that processes the incoming and exiting tasks of a vehicle.
 */
public class ParkingService {
  private static final Logger logger = LogManager.getLogger("ParkingService");
  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();
  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDao parkingSpotDao;
  private TicketDao ticketDao;

  /**
   * Constructor for a specified parking Service.
   *
   * @param inputReaderUtil is what the user has written in the shell interface
   * @param parkingSpotDao  is the parking spot concerned by the incoming/exiting vehicle
   * @param ticketDao       is the ticket used for the incoming/exiting vehicle.
   */
  public ParkingService(
      InputReaderUtil inputReaderUtil,
      ParkingSpotDao parkingSpotDao,
      TicketDao ticketDao) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDao = parkingSpotDao;
    this.ticketDao = ticketDao;
  }

  /**
   * This method is used to process the incoming of a vehicle with the current date/time.
   */
  public void processIncomingVehicle() {
    processIncomingVehicleSpecialInTimeDate(LocalDateTime.now());
  }

  /**
   * This method is used to process the incoming of a vehicle with a specific date/time.
   * This class is used for Tests ONLY.
   *
   * @param inTime is the specified inTime you want to process into the ticket.
   */
  public void processIncomingVehicleSpecialInTimeDate(LocalDateTime inTime) {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        parkingSpot.setAvailable(false);
        //allot this parking space and mark its availability as false
        parkingSpotDao.updateParking(parkingSpot);
        inTime = inTime.truncatedTo(ChronoUnit.SECONDS);
        Ticket ticket = new Ticket();
        //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, ALREADY_EXIST
        ticket.setParkingSpot(parkingSpot);
        String vehicleRegNumber = getVehicleRegNumber();
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticketDao.saveTicket(ticket);

        //---- For Recurring users ----
        Ticket ticketExist = new Ticket();
        ticketExist = ticketDao.getTicket(vehicleRegNumber);
        if (ticketExist != null && ticketExist.getAlreadyExists()) {
          System.out.println(
              "-----------------------------------------------------"
                  + "-----------------------------------");
          System.out.println(
              "Welcome back! As a recurring user of our parking lot,"
                  + " you'll benefit from a 5% discount.");
          System.out.println(
              "-----------------------------------------------------"
                  + "-----------------------------------");
        }
        //-----------------------------

        System.out.println("Generated Ticket and saved in DB");
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
        System.out.println(
            "Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle", e);
    }
  }

  private String getVehicleRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  /**
   * This method brings back a ParkingSpot object that is available for an incoming car.
   *
   * @return ParkingSpot Object with availability.
   */
  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehicleType();
      parkingNumber = parkingSpotDao.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      logger.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      logger.error("Error fetching next available parking slot", e);
    }
    return parkingSpot;
  }

  private ParkingType getVehicleType() {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        System.out.println("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /**
   * This method is used to process the exiting of a vehicle with the current date/time.
   */
  public void processExitingVehicle() {
    processExitingVehicleSpecialDate(LocalDateTime.now());
  }

  /**
   * This method is used to process the exiting of a vehicle with a specific date/time.
   * This class is used for Tests ONLY.
   *
   * @param outTime is the specified outTime you want to process into the ticket.
   */
  public void processExitingVehicleSpecialDate(LocalDateTime outTime) {
    try {
      String vehicleRegNumber = getVehicleRegNumber();
      Ticket ticket = ticketDao.getTicket(vehicleRegNumber);
      outTime = outTime.truncatedTo(ChronoUnit.SECONDS);
      ticket.setOutTime(outTime);

      fareCalculatorService.calculateFare(ticket);

      //update parking spot after having updated ticket
      if (ticketDao.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDao.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        System.out.println(
            "Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber()
                + " is:" + outTime);
      } else {
        System.out.println("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle", e);
    }
  }
}
