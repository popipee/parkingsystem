package com.parkit.parkingsystem.constants;

/**
 * A class with all the Request Statement that could be made to the database.
 */
public class DataBaseConstants {
  /**
   * SQL Query that loads all the parking Spots in a result Set.
   */
  public static final String GET_PARKING_SPOT = "select * from parking "
      + "where PARKING_NUMBER = ? limit 1";

  /**
   * SQL Query that load the nex available parking spot.
   */
  public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking "
      + "where AVAILABLE = true and TYPE = ?";

  /**
   * SQL Query that modifies the availability of a parking Spot.
   */
  public static final String UPDATE_PARKING_SPOT = "update parking set available = ? "
      + "where PARKING_NUMBER = ?";

  /**
   * SQL Query that creates a new ticket in database.
   */
  public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, "
      + "PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";

  /**
   * SQL Query that updates a ticket in database.
   */
  public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? "
      + "where ID=?";

  /**
   * SQL Query that gets tickets list of a single registration number by descending incoming Time
   * (from recent to old).
   */
  public static final String GET_TICKETS = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, "
      + "t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and "
      + "t.VEHICLE_REG_NUMBER=? order by t.IN_TIME DESC";
}
