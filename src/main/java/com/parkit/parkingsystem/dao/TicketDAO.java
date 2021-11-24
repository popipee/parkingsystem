package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (Timestamp.valueOf(ticket.getOutTime())) );
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;

        try {
            con = dataBaseConfig.getConnection();
            //Prepared Statement ordered parameters: VEHICULE_REG_NUMBER
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKETS,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ps.setString(1,vehicleRegNumber);
            //Result Statement ordered parameters : PARKING_NUMBER, ID, PRICE, IN_TIME, OUT_TIME, TYPE
            ResultSet rs = ps.executeQuery();

            //rs.next is used to move the cursor of the result set to the first row. Here the GET_TICKETS will giv us the last ticket so it is okay to read only first row
            if(rs.next()){
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
                ticket.setOutTime((rs.getTimestamp("OUT_TIME") == null )?null:rs.getTimestamp("OUT_TIME").toLocalDateTime());

                //Check if there are more than one row in the result set which means the vehicleRegNumber has come more than once in the parking
                rs.last();
                if(rs.getRow()>1){ticket.setAlreadyExists(true);}
                else{ticket.setAlreadyExists(false);}
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

}
