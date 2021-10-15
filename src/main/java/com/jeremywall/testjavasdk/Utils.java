package com.jeremywall.testjavasdk;

public class Utils {

  public static String getGreeting(String name) {

    if (name == null) {
      return "Hello";
    }
    return "Hello " + name;
  }
}
