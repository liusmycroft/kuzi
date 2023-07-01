package com.lius.kuzi.ticker;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lius.kuzi.model.Ticker;
import com.lius.kuzi.model.enums.ClockType;
import com.lius.kuzi.model.enums.MsgType;
import com.lius.kuzi.properties.TickerProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import static com.lius.kuzi.constant.CommonConstant.MSG_TYPE;
import static com.lius.kuzi.constant.CommonConstant.PER_SECOND_TIMER;

@Slf4j
public class AStockTradeTimeTicker extends AbstractVerticle {

  private final Ticker ticker;

  public AStockTradeTimeTicker(TickerProperties tickerProperties) {
    ticker = new Ticker("aStockTradeTime");
    ticker.addClock(ClockType.A_STOCK_REAL_TIME_PRICE_CLOCK, tickerProperties.getAStockRealTimePriceClockInterval());
    ticker.resetClock(ClockType.A_STOCK_REAL_TIME_PRICE_CLOCK);
  }

  @Override
  public void start() throws Exception {
    MessageConsumer<String> consumer = vertx.eventBus()
      .consumer(PER_SECOND_TIMER);
    consumer.handler(this::onTick);
  }

  private void onTick(Message<String> ignore) {
    // 如果非交易时间，则不驱动时钟，且reset ticker
    if (!isTradeDay()) {
      return;
    }
    if (!isTradeTime()) {
      return;
    }
    ticker.tickClock();
    if (ticker.isOnTick(ClockType.A_STOCK_REAL_TIME_PRICE_CLOCK)) {
      DeliveryOptions options = new DeliveryOptions();
      options.addHeader(MSG_TYPE, MsgType.TICK.name());
      vertx.eventBus().publish(ClockType.A_STOCK_REAL_TIME_PRICE_CLOCK.getAddress(), new JsonObject(), options);
      ticker.resetClock(ClockType.A_STOCK_REAL_TIME_PRICE_CLOCK);
    }
  }

  private boolean isTradeDay() {
    LocalDate now = LocalDate.now();
    // 如果当天是节假日 or 当天是补班，直接返回结果
    if (now.equals(LocalDate.of(2023, 9, 29))) {
      return false;
    } else if (now.equals(LocalDate.of(2023, 10, 7))) {
      return true;
    }
    DayOfWeek dayOfWeek = now.getDayOfWeek();
    return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
  }

  private boolean isTradeTime() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime morningStart = LocalDate.now().atTime(9, 29, 0);
    LocalDateTime morningEnd = LocalDate.now().atTime(11, 31, 0);
    LocalDateTime afternoonStart = LocalDate.now().atTime(12, 59, 0);
    LocalDateTime afternoonEnd = LocalDate.now().atTime(15, 1, 0);
    return (now.isAfter(morningStart) && now.isBefore(morningEnd)) || (now.isAfter(afternoonStart) && now.isBefore(
      afternoonEnd));
  }
}
