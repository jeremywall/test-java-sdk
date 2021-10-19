package com.jeremywall.testjavsdk;

public class SignatureCalculator {

  private enum ApiCall {
    STATIONS,
    NODES,
    SENSORS,
    SENSOR_ACTIVITY,
    SENSOR_CATALOG;
  }

  public String calculateStationsSignature(
      String apiKey, String apiSecret, long apiRequestTimestamp) {

    return "";
  }
}
