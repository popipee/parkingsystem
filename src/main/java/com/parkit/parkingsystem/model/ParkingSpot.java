package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * This class is a model used to represent a Parking Spot in the app.
 */
public class ParkingSpot {
  private int number;
  private ParkingType parkingType;
  private boolean isAvailable;

  /**
   * A null constructor.
   */
  public ParkingSpot() {
  }

  /**
   * This is the constructor of a specified parking Spot.
   *
   * @param number      represent the id of the parking spot (usually the parking spot number)
   * @param parkingType is the type for which vehicle the parking spot is reserved
   * @param isAvailable is the boolean availability of the parking spot.
   */
  public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
    this.number = number;
    this.parkingType = parkingType;
    this.isAvailable = isAvailable;
  }

  public int getId() {
    return number;
  }

  public void setId(int number) {
    this.number = number;
  }

  public ParkingType getParkingType() {
    return parkingType;
  }

  public void setParkingType(ParkingType parkingType) {
    this.parkingType = parkingType;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParkingSpot that = (ParkingSpot) o;
    return number == that.number;
  }

  @Override
  public int hashCode() {
    return number;
  }
}
