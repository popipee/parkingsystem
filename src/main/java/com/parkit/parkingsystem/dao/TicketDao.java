package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DataBaseConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to make requests to the database regarding the ticket table.
 */
public class TicketDao {
  private static final Logger logger = LogManager.getLogger("TicketDao");
  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * This method is used to save a new ticket in the ticket's table into the database.
   *
   * @param ticket is the ticket object to be saved. Could be found in the model package.
   * @return a boolean which is true if the operation went well.
   */
  public boolean saveTicket(Ticket ticket) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DataBaseConstants.SAVE_TICKET);
      //PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
      ps.setTimestamp(5, (ticket.getOutTime() == null) ? null :
          (Timestamp.valueOf(ticket.getOutTime())));
      return ps.execute();
    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
      return false;
    }
  }

  /**
   * This class retrieve a tickets from the database thanks to
   * a vehicle registration number. This ticket is always the one with the latest inTime date.
   *
   * @param vehicleRegNumber is the registration number of the vehicle
   *                         you want to get the last ticket.
   * @return a Ticket Object of the last ticket registered in the database for the specified
   registration number.
   */
  public Ticket getTicket(String vehicleRegNumber) {
    Connection con = null;
    Ticket ticket = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      con = dataBaseConfig.getConnection();
      //Prepared Statement ordered parameters: VEHICULE_REG_NUMBER
      ps = con.prepareStatement(
          DataBaseConstants.GET_TICKETS,
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      ps.setString(1, vehicleRegNumber);
      // Result Statement ordered parameters : PARKING_NUMBER, ID, PRICE, IN_TIME, OUT_TIME, TYPE
      rs = ps.executeQuery();

      // rs.next is used to move the cursor of the result set to the first row.
      // Here the GET_TICKETS will giv us the last ticket so it is okay to read only first row.
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(
            rs.getInt("PARKING_NUMBER"),
            ParkingType.valueOf(rs.getString("TYPE")),
            false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt("ID"));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble("PRICE"));
        ticket.setInTime(rs.getTimestamp("IN_TIME").toLocalDateTime());
        ticket.setOutTime(
            (rs.getTimestamp("OUT_TIME") == null)
                ? null : rs.getTimestamp("OUT_TIME").toLocalDateTime());

        // Check if there are more than one row in the result set which means the vehicleRegNumber
        // has come more than once in the parking
        rs.last();
        if (rs.getRow() > 1) {
          ticket.setAlreadyExists(true);
        } else {
          ticket.setAlreadyExists(false);
        }
      }
    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
      return ticket;
    }
  }

  /**
   * This method updates the outTime and the price ou an existing ticket thanks to its ID.
   *
   * @param ticket is the ticket to be updated
   * @return a boolean which is true if the operation went well.
   */
  public boolean updateTicket(Ticket ticket) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DataBaseConstants.UPDATE_TICKET);
      ps.setDouble(1, ticket.getPrice());
      ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
      ps.setInt(3, ticket.getId());
      ps.execute();
      return true;
    } catch (Exception ex) {
      logger.error("Error saving ticket info", ex);
    } finally {
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return false;
  }

}
