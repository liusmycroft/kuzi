package com.lius.kuzi.dispatcher;

import java.util.Collections;
import java.util.stream.Collectors;

import com.lius.kuzi.storage.dataobject.SubscriberDO;
import com.lius.kuzi.storage.dataobject.SubscriberDORowMapper;
import com.lius.kuzi.util.RepoUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import lombok.extern.slf4j.Slf4j;

import static com.lius.kuzi.constant.CommonConstant.DISPATCHER;

@Slf4j
public class HttpDispatcher extends AbstractVerticle {

  private final RepoUtil repoUtil;
  private WebClient client;

  private static final String GET_SUBSCRIBER_SQL = "SELECT * FROM subscriber WHERE data_type = #{dataType}";

  public HttpDispatcher(RepoUtil repoUtil) {
    this.repoUtil = repoUtil;
  }

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer(DISPATCHER, this::consume);
    WebClientOptions options = new WebClientOptions().setKeepAlive(false);
    this.client = WebClient.create(vertx, options);
  }

  private void consume(Message<JsonObject> message) {
    JsonObject body = message.body();
    String dataType = body.getString("dataType");
    String traceId = body.getString("traceId");

    repoUtil.getAll(Collections.singletonMap("dataType", dataType), GET_SUBSCRIBER_SQL,
        SubscriberDORowMapper.INSTANCE)
      .compose(list -> Future.join(list.stream()
        .map(SubscriberDO::getUrl)
        .map(url -> client.post(url)
          .expect(ResponsePredicate.JSON)
          .expect(ResponsePredicate.SC_SUCCESS)
          .sendBuffer(body.toBuffer())
          .onSuccess(x -> log.info("deliver success, traceId: {}", traceId))
          .onFailure(r -> log.error("deliver failed, traceId: {}, err: {}", traceId, r.toString()))
        )
        .collect(Collectors.toList())))
      .onSuccess(x -> log.info("deliver to all finish, traceId: {}", traceId))
      .onFailure(r -> log.error("deliver to all subscriber fail, traceId: {}, err: {}", traceId, r.toString()));
  }
}
