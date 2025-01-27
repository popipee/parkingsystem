package com.parkit.parkingsystem.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.LocalDateTime;

/**
 * This class is a model used to represent a Ticket in the app.
 */
public class Ticket {
  private int id;
  private ParkingSpot parkingSpot;
  private String vehicleRegNumber;
  private double price;
  private LocalDateTime inTime;
  private LocalDateTime outTime;
  private boolean alreadyExists;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ParkingSpot getParkingSpot() {
    return parkingSpot;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setParkingSpot(ParkingSpot parkingSpot) {
    this.parkingSpot = parkingSpot;
  }

  public String getVehicleRegNumber() {
    return vehicleRegNumber;
  }

  public void setVehicleRegNumber(String vehicleRegNumber) {
    this.vehicleRegNumber = vehicleRegNumber;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public LocalDateTime getInTime() {
    return inTime;
  }

  public void setInTime(LocalDateTime inTime) {
    this.inTime = inTime;
  }

  public LocalDateTime getOutTime() {
    return outTime;
  }

  public void setOutTime(LocalDateTime outTime) {
    this.outTime = outTime;
  }

  public boolean getAlreadyExists() {
    return alreadyExists;
  }

  public void setAlreadyExists(boolean exists) {
    this.alreadyExists = exists;
  }
}
