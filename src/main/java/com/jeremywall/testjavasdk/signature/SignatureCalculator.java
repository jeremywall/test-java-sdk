package com.jeremywall.testjavsdk.signature;

import com.jeremywall.testjavsdk.utils.Constants;
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

  private static final String MAC_ALGORITHM = "HmacSHA256";

  private enum ApiCall {
    STATIONS,
    NODES,
    SENSORS,
    SENSOR_ACTIVITY,
    SENSOR_CATALOG,
    ;
  }

  public String calculateStationsSignature(
      String apiKey, String apiSecret, long apiRequestTimestamp) throws SignatureException {

    HashMap<String, String> parametersToHash = new HashMap<>();
    parametersToHash.put(Constants.Parameters.API_KEY, apiKey);
    parametersToHash.put(
        Constants.Parameters.API_REQUEST_TIMESTAMP, String.valueOf(apiRequestTimestamp));
    return calculateSignature(ApiCall.STATIONS, apiSecret, parametersToHash);
  }

  public String calculateStationsSignature(
      String apiKey, String apiSecret, long apiRequestTimestamp, String stationIds)
      throws SignatureException {

    HashMap<String, String> parametersToHash = new HashMap<>();
    parametersToHash.put(Constants.Parameters.API_KEY, apiKey);
    parametersToHash.put(
        Constants.Parameters.API_REQUEST_TIMESTAMP, String.valueOf(apiRequestTimestamp));
    parametersToHash.put(Constants.Parameters.STATION_IDS, stationIds);
    return calculateSignature(ApiCall.STATIONS, apiSecret, parametersToHash);
  }

  private String calculateSignature(
      ApiCall apiCall, String apiSecret, Map<String, String> parametersToHash)
      throws SignatureException {

    String stringToHash = "";
    if (parametersToHash != null) {
      stringToHash =
          parametersToHash.entrySet().stream()
              .sorted(Comparator.comparing(Map.Entry::getKey))
              .map(entry -> entry.getKey() + entry.getValue())
              .collect(Collectors.joining());
    }

    try {
      Mac mac = Mac.getInstance(MAC_ALGORITHM);
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), MAC_ALGORITHM);
      mac.init(secretKeySpec);
      byte[] apiSignatureBytes = mac.doFinal(stringToHash.getBytes(StandardCharsets.UTF_8));
      String apiSignatureString = convertByteArrayToHexString(apiSignatureBytes);

      return apiSignatureString;
    } catch (NoSuchAlgorithmException ex) {
      throw new SignatureException("MAC Algorithm " + MAC_ALGORITHM + " is not available");
    } catch (InvalidKeyException ex) {
      throw new SignatureException("MAC Algorithm " + MAC_ALGORITHM + " secret key is invalid");
    }
  }

  private String convertByteArrayToHexString(byte[] array) {

    StringBuilder builder = new StringBuilder(array.length * 2);
    for (byte b : array) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }
}
