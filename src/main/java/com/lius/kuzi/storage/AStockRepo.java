package com.lius.kuzi.storage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.lius.kuzi.constant.CommonConstant;
import com.lius.kuzi.storage.dataobject.SubscribeStockDO;
import com.lius.kuzi.storage.dataobject.SubscribeStockDORowMapper;
import com.lius.kuzi.util.JsonUtil;
import com.lius.kuzi.util.LogUtil;
import com.lius.kuzi.util.RepoUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

import static com.lius.kuzi.constant.CommonConstant.DISPATCHER;

/**
 * @author liuyixi
 */
@Slf4j
public class AStockRepo extends AbstractVerticle {

  private final RepoUtil repoUtil;
  private final String name;

  private static final String CREATE_REAL_TIME_RECORD_SQL =
    "INSERT INTO astock_real_time (create_time, code, name, latest_price, change_percentage, change_amount,"
      + "turnover, volume, amplitude, high, low, open_price, close_price, quantity_relative_ratio, "
      + "turnover_rate, pe_dynamic, pbr, stock_value, circulated_stock_value, accr, five_min_change_percentage, "
      + "sixty_day_change_percentage, year_to_date_change_percentage) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, "
      + "$9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21, $22, $23)";

  private static final String GET_SUBSCRIBE_STOCK_SQL = "SELECT * FROM astock_subscribe_stock";

  public AStockRepo(JsonObject config, String name) {
    this.repoUtil = new RepoUtil(vertx, config);
    this.name = name;
  }

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer(CommonConstant.CRAWLER_ENTRIES_ADDRESS_PREFIX + name, this::consumeEntries);
  }

  private void consumeEntries(Message<JsonObject> message) {
    JsonObject body = message.body();
    JsonArray data = body.getJsonArray("data");
    Instant time = body.getInstant("timestamp");
    String traceId = body.getString("traceId");
    log.info("A stock real time data arrival, time: {}, traceId: {}", body.getInstant("timestamp"), traceId);
    saveAllToDb(data, time, traceId);
    send(data, time, traceId);
  }

  private void send(JsonArray data, Instant time, String traceId) {
    repoUtil.getAll(Collections.emptyMap(), GET_SUBSCRIBE_STOCK_SQL, SubscribeStockDORowMapper.INSTANCE)
      .compose(subscribeStockList -> {
        List<JsonObject> subscribeStockData = getSubscribeStock(data, subscribeStockList);
        Promise<JsonObject> promise = Promise.promise();
        Promise.promise()
          .complete(JsonObject.of("data", subscribeStockData)
            .put("timestamp", time)
            .put("traceId", traceId)
            .put("dataType", "AStockRealTimeData"));
        return promise.future();
      })
      .onSuccess(x -> vertx.eventBus().publish(DISPATCHER, x))
      .onFailure(r -> log.error("publish to dispatcher fail, err: {}", r.toString()));
  }

  private List<JsonObject> getSubscribeStock(JsonArray data, List<SubscribeStockDO> subscribeStockList) {
    return JsonUtil.arrToList(data)
      .stream()
      .filter(jsonObject -> subscribeStockList.stream().map(SubscribeStockDO::getCode)
        .collect(Collectors.toList()).contains(jsonObject.getString("代码")))
      .collect(Collectors.toList());
  }

  private void saveAllToDb(JsonArray data, Instant time, String traceId) {
    long begin = System.currentTimeMillis();
    repoUtil.executeBatchNoResult(buildTuple(data, time), CREATE_REAL_TIME_RECORD_SQL, r -> {
      if (r.succeeded()) {
        LogUtil.printDalSuccess(traceId, "AStockRepo#saveAllToDb", begin);
      } else {
        LogUtil.printDalError(traceId, "AStockRepo#saveAllToDb", begin, r.cause());
      }
    });
  }

  private List<Tuple> buildTuple(JsonArray list, Instant time) {
    return JsonUtil.arrToList(list)
      .stream()
      .map(data -> Tuple.of(LocalDateTime.ofInstant(time, ZoneId.systemDefault()), data.getString("代码"),
        data.getString("名称"), data.getNumber("最新价"), data.getNumber("涨跌幅"),
        data.getNumber("涨跌额"), data.getNumber("成交量"), data.getNumber("成交额"),
        data.getNumber("振幅"), data.getNumber("最高"), data.getNumber("最低"),
        data.getNumber("今开"), data.getNumber("昨收"), data.getNumber("量比"),
        data.getNumber("换手率"), data.getNumber("市盈率-动态"), data.getNumber("市净率"),
        data.getNumber("总市值"), data.getNumber("流通市值"), data.getNumber("涨速"),
        data.getNumber("5分钟涨跌"), data.getNumber("60日涨跌幅"), data.getNumber("年初至今涨跌幅")))
      .collect(Collectors.toList());
  }
}
