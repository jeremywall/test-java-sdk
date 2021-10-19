package com.jeremywall.testjavsdk;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureCalculator {

  private enum ApiCall {
    STATIONS,
    NODES,
    SENSORS,
    SENSOR_ACTIVITY,
    SENSOR_CATALOG,
    ;
  }

  public String calculateStationsSignature(
      String apiKey, String apiSecret, long apiRequestTimestamp)
      throws NoSuchAlgorithmException, InvalidKeyException {

    HashMap<String, String> parametersToHash = new HashMap<>();
    parametersToHash.put("api-key", apiKey);
    parametersToHash.put("t", String.valueOf(apiRequestTimestamp));
    return calculateSignature(ApiCall.STATIONS, apiSecret, parametersToHash);
  }

  public String calculateStationsSignature(
      String apiKey, String apiSecret, long apiRequestTimestamp, String stationIds)
      throws NoSuchAlgorithmException, InvalidKeyException {

    HashMap<String, String> parametersToHash = new HashMap<>();
    parametersToHash.put("api-key", apiKey);
    parametersToHash.put("t", String.valueOf(apiRequestTimestamp));
    parametersToHash.put("station-ids", stationIds);
    return calculateSignature(ApiCall.STATIONS, apiSecret, parametersToHash);
  }

  private String calculateSignature(
      ApiCall apiCall, String apiSecret, Map<String, String> parametersToHash)
      throws NoSuchAlgorithmException, InvalidKeyException {

    String stringToHash =
        parametersToHash.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(entry -> entry.getKey() + entry.getValue())
            .collect(Collectors.joining());

    Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    hmacSHA256.init(secretKeySpec);
    byte[] apiSignatureBytes = hmacSHA256.doFinal(stringToHash.getBytes(StandardCharsets.UTF_8));
    String apiSignatureString = convertByteArrayToHexString(apiSignatureBytes);

    return apiSignatureString;
  }

  private String convertByteArrayToHexString(byte[] array) {

    StringBuilder builder = new StringBuilder(array.length * 2);
    for (byte b : array) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }
}
