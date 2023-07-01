package com.lius.kuzi.model.enums;

public enum ClockType {
  A_STOCK_REAL_TIME_PRICE_CLOCK("aStock.realTimePriceClock"),
  ;

  private final String address;

  ClockType(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }
}
