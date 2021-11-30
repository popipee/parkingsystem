package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DataBaseConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to make requests to the database regarding the parking table.
 */
public class ParkingSpotDao {
  private static final Logger logger = LogManager.getLogger("ParkingSpotDao");

  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * This method is used to get the state of a parking spot in the database.
   *
   * @param parkingNumber is the number of the parking spot from which you want to know the state.
   * @return a ParkingSpot object describing the state of a parking spot.
   */
  public ParkingSpot getParkingSpot(Integer parkingNumber) {
    Connection con = null;
    ParkingSpot parkingSpot = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DataBaseConstants.GET_PARKING_SPOT);
      //PARKING_NUMBER, AVAILABLE, TYPE
      ps.setString(1, (parkingNumber.toString()));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        parkingSpot = new ParkingSpot();
        parkingSpot.setId(parkingNumber);
        parkingSpot.setAvailable(rs.getBoolean("AVAILABLE"));
        if (rs.getString("TYPE").equals(ParkingType.CAR.name())) {
          parkingSpot.setParkingType(ParkingType.CAR);
        } else if (rs.getString("TYPE").equals(ParkingType.BIKE.name())) {
          parkingSpot.setParkingType(ParkingType.BIKE);
        } else {
          throw new IllegalArgumentException("Error fetching ParkingType from DB");
        }
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (Exception ex) {
      logger.error("Error in fetching the parkingSpot", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      return parkingSpot;
    }
  }

  /**
   * This method look for the next available parking spot regarding a certain type of vehicle.
   *
   * @param parkingType is the type of vehicle you want to find a free parking spot.
   * @return return an Integer which is the number id of next parking spot available.
   */
  public int getNextAvailableSlot(ParkingType parkingType) {
    Connection con = null;
    int result = -1;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DataBaseConstants.GET_NEXT_PARKING_SPOT);
      ps.setString(1, parkingType.toString());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1);
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
    }
    return result;
  }

  /**
   * This method updates the availability of a parking spot.
   *
   * @param parkingSpot is the parking spot to be updated
   * @return a boolean which is false in case of error
   */
  public boolean updateParking(ParkingSpot parkingSpot) {
    //update the availability fo that parking slot
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DataBaseConstants.UPDATE_PARKING_SPOT);
      ps.setBoolean(1, parkingSpot.isAvailable());
      ps.setInt(2, parkingSpot.getId());
      int updateRowCount = ps.executeUpdate();
      dataBaseConfig.closePreparedStatement(ps);
      return (updateRowCount == 1);
    } catch (Exception ex) {
      logger.error("Error updating parking info", ex);
      return false;
    } finally {
      dataBaseConfig.closeConnection(con);
    }
  }

}
