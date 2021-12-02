package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterReader;
import java.sql.*;
import java.util.Properties;

public class DataBaseTestConfig extends DataBaseConfig {

  private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

  public Connection getConnection() throws ClassNotFoundException, SQLException {
    Properties properties = new Properties();
    String user = null, pass = null, url = null, driver= null;
    String absoluteLocationOfCredentials = "resources/credentialsTest.properties";
    logger.info("Trying to retrieve credentials from file located in : \n"
        + System.getProperty("user.dir") + "/" + absoluteLocationOfCredentials) ;
    try {
      properties.load(new FileInputStream(absoluteLocationOfCredentials));
      user = properties.getProperty("username");
      pass = properties.getProperty("password");
      url = properties.getProperty("url");
      driver = properties.getProperty("driver");

      logger.info("Create DB connection");
      Class.forName(driver);

    } catch (Exception e){
      e.printStackTrace();
      logger.error("Could not find credentials file to connect to database.");
    }
    finally {
      return DriverManager.getConnection(url, user, pass);
    }
  }

  public void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
        logger.info("Closing DB connection");
      } catch (SQLException e) {
        logger.error("Error while closing connection", e);
      }
    }
  }

  public void closePreparedStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
        logger.info("Closing Prepared Statement");
      } catch (SQLException e) {
        logger.error("Error while closing prepared statement", e);
      }
    }
  }

  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        logger.info("Closing Result Set");
      } catch (SQLException e) {
        logger.error("Error while closing result set", e);
      }
    }
  }
}
