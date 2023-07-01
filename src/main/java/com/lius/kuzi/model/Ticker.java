package com.lius.kuzi.model;

import com.lius.kuzi.model.enums.ClockType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyixi
 */
@Data
public class Ticker {

  private String namespace;
  private long tick;
  private Map<ClockType, Clock> clocks = new HashMap<>();

  public Ticker(String namespace) {
    this.namespace = namespace;
  }

  public void addClock(ClockType clockType, long interval) {
    Clock clock = new Clock();
    if (interval <= 0) {
      throw new IllegalArgumentException("interval cannot smaller or equal than zero");
    }
    clock.setInterval(interval);
    clocks.put(clockType, clock);
  }

  public void modifyClockInterval(ClockType clockType, long interval) {
    if (interval <= 0) {
      throw new IllegalArgumentException("interval cannot smaller or equal than zero");
    }
    Clock clock = clocks.get(clockType);
    if (clock == null) {
      throw new NullPointerException(String.format("%s: clock %s not exists", namespace, clockType));
    }
    clock.setInterval(interval);
  }

  public void tickClock() {
    tick++;
  }

  public void resetClock(ClockType clockType) {
    Clock clock = clocks.get(clockType);
    if (clock == null) {
      throw new NullPointerException(String.format("%s: clock %s not exists", namespace, clockType));
    }
    clock.runAt = tick + clock.interval;
  }

  public boolean isOnTick(ClockType clockType) {
    Clock clock = clocks.get(clockType);
    if (clock == null) {
      throw new NullPointerException(String.format("%s: clock %s not exists", namespace, clockType));
    }
    return clock.runAt <= tick;
  }

  @Data
  public static class Clock {
    private long runAt;
    private long interval;
  }
}
