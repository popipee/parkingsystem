package com.parkit.parkingsystem.constants;

/**
 * A class with all the Request Statement that could be made to the database.
 */
public class DataBaseConstants {
  public static final String GET_PARKING_SPOT = "select * from parking "
      + "where PARKING_NUMBER = ? limit 1";
  public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking "
      + "where AVAILABLE = true and TYPE = ?";
  public static final String UPDATE_PARKING_SPOT = "update parking set available = ? "
      + "where PARKING_NUMBER = ?";
  public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, "
      + "PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
  public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? "
      + "where ID=?";
  public static final String UPDATE_TICKET_INTIME = "update ticket set IN_TIME=? "
      + "where ID=?";
  public static final String UPDATE_TICKET_OUTTIME = "update ticket set OUT_TIME=? where ID=?";
  //Get a ticket with a limitation of 1
  public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, "
      + "t.OUT_TIME, p.TYPE from ticket t,parking p "
      + "where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? "
      + "order by t.IN_TIME DESC  limit 1";
  public static final String GET_TICKETS = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, "
      + "t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and "
      + "t.VEHICLE_REG_NUMBER=? order by t.IN_TIME DESC";
}