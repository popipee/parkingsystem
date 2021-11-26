package com.parkit.parkingsystem.util;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class get user typing through the shell interface.
 */
public class InputReaderUtil {

  private static final Logger logger = LogManager.getLogger("InputReaderUtil");
  private static Scanner scan = new Scanner(System.in);

  /**
   * This method read the user selection regarding a list of choice thrown by the shell.
   *
   * @return the integer selected by the user.
   */
  public int readSelection() {
    try {
      int input = Integer.parseInt(scan.nextLine());
      return input;
    } catch (Exception e) {
      logger.error("Error while reading user input from Shell", e);
      System.out.println("Error reading input. Please enter valid number for proceeding further");
      return -1;
    }
  }

  /**
   * This method is used to read the registration number given by the user to the shell.
   *
   * @return the registration number typed by the user through the shell.
   * @throws Exception if something goes wrong during the process.
   */
  public String readVehicleRegistrationNumber() throws Exception {
    try {
      String vehicleRegNumber = scan.nextLine();
      if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
        throw new IllegalArgumentException("Invalid input provided");
      }
      return vehicleRegNumber;
    } catch (Exception e) {
      logger.error("Error while reading user input from Shell", e);
      System.out.println(
          "Error reading input. Please enter a valid string for vehicle registration number");
      throw e;
    }
  }


}
