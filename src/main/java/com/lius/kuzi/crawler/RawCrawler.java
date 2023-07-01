package com.lius.kuzi.crawler;

import com.lius.kuzi.constant.CommonConstant;
import com.lius.kuzi.model.CmdMsg;
import com.lius.kuzi.model.enums.MsgType;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.extern.slf4j.Slf4j;

import static com.lius.kuzi.constant.CommonConstant.MSG_TYPE;

@Slf4j
public abstract class RawCrawler extends AbstractVerticle {

  private final String name;
  private boolean stop;
  private JsonObject param;
  protected WebClient client;

  public RawCrawler(String name, JsonObject initParam) {
    this.param = initParam;
    this.name = name;
  }

  @Override
  public void start() throws Exception {
    WebClientOptions options = new WebClientOptions().setKeepAlive(false);
    client = WebClient.create(vertx, options);
    vertx.eventBus().consumer(CommonConstant.CRAWLER_MSG_ADDRESS_PREFIX + name, this::handleMsg);
  }

  private void onTick() {
    if (stop) {
      return;
    }
    log.info("receive tick, crawlerName: {}", name);
    tick(param);
  }

  /**
   * 逻辑时钟驱动
   */
  abstract protected void tick(JsonObject param);

  private void handleMsg(Message<JsonObject> message) {
    MsgType msgType = MsgType.valueOf(message.headers().get(MSG_TYPE));
    switch (msgType) {
      case CRAWLER_CMD:
        CmdMsg cmdMsg = message.body().mapTo(CmdMsg.class);
        onCrawlerCmd(cmdMsg);
        break;
      case TICK:
        onTick();
        break;
      default:
        log.warn("unknown msg type: {}", msgType);
    }
  }

  protected void publishData(JsonObject jsonObject) {
    vertx.eventBus().publish(CommonConstant.CRAWLER_ENTRIES_ADDRESS_PREFIX + name, jsonObject);
  }

  private void onCrawlerCmd(CmdMsg cmdMsg) {
    log.info("receive crawler cmd, crawlerName: {}, cmdMsg: {}", name, cmdMsg);
    switch (cmdMsg.getMsgType()) {
      case ENABLE:
        if (stop) {
          stop = false;
        }
        break;
      case DISABLE:
        if (!stop) {
          stop = true;
        }
        break;
      case UPDATE_PARAM:
        param = cmdMsg.getParam();
    }
  }
}
