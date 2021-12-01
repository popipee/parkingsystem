package com.parkit.parkingsystem.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that create a connection to a MySQL database
 * and deals with closing statement and result sets.
 */
public class DataBaseConfig {
  /**
   * final object used to log information during process.
   */
  private static final Logger LOGGER = LogManager.getLogger("DataBaseConfig");

  /**
   * Open a connection to the database localhost:3306/prod as root user.
   *
   * @return java.sql.Connection object
   * @throws ClassNotFoundException if the class cannot be located
   * @throws SQLException if a database access error occurs or the url is null
   */
  public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
    Properties properties = new Properties();
    String user = null, pass = null, url = null, driver = null;
    String absoluteLocationOfCredentials = "resources/credentials.properties";
    LOGGER.info("Trying to retrieve credentials from file located in : \n"
        + System.getProperty("user.dir") + "/" + absoluteLocationOfCredentials);
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(absoluteLocationOfCredentials);
      properties.load(fileInputStream);
      user = properties.getProperty("username");
      pass = properties.getProperty("password");
      url = properties.getProperty("url");
      driver = properties.getProperty("driver");
      LOGGER.info("Create DB connection");
      Class.forName(driver);
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Could not find credentials file to connect to database.");
    } finally {
      closeFileInputStream(fileInputStream);
      return DriverManager.getConnection(url, user, pass);
    }
  }

  /**
   * Close a connection to a database.
   *
   * @param con is a java.sql.Connection object created in getConnection() methode
   */
  public void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
        LOGGER.info("Closing DB connection");
      } catch (SQLException e) {
        LOGGER.error("Error while closing connection", e);
      }
    }
  }

  /**
   * Close a statement made to a database.
   *
   * @param ps is the PreparedStatement object to be closed
   */
  public void closePreparedStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
        LOGGER.info("Closing Prepared Statement");
      } catch (SQLException e) {
        LOGGER.error("Error while closing prepared statement", e);
      }
    }
  }

  /**
   * Close a Result Set made by a PreparedStatement.
   *
   * @param rs is the ResultSet to be closed
   */
  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        LOGGER.info("Closing Result Set");
      } catch (SQLException e) {
        LOGGER.error("Error while closing result set", e);
      }
    }
  }

  public void closeFileInputStream(FileInputStream fileInputStream) {
    if (fileInputStream != null) {
      try {
        fileInputStream.close();
        LOGGER.info("Closing File Input Stream");
      } catch (IOException e) {
        LOGGER.error("Error while closing FileInputStream", e);
      }
    }
  }
}
