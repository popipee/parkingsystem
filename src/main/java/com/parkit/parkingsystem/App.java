package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * App loader.
 */
public class App {
  private static final Logger logger = LogManager.getLogger("App");

  /**
   * Main methods.
   *
   * @param args args for shell
   */
  public static void main(String[] args) {
    logger.info("Initializing Parking System");
    InteractiveShell.loadInterface();
  }
}
