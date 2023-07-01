package com.lius.kuzi.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeUtil {

  public static long localDateTimeToMillis(LocalDateTime dateTime) {
    return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
  }
}
