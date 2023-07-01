package com.lius.kuzi;

import com.lius.kuzi.crawler.AStockCrawler;
import com.lius.kuzi.dispatcher.HttpDispatcher;
import com.lius.kuzi.properties.TickerProperties;
import com.lius.kuzi.storage.AStockRepo;
import com.lius.kuzi.ticker.AStockTradeTimeTicker;
import com.lius.kuzi.util.RepoUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  private static final String aStockName = "astock";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    RepoUtil repoUtil = new RepoUtil(vertx, JsonObject.of("host", "127.0.0.1")
      .put("database", "test")
      .put("user", "test")
      .put("password", "test"));

    Future.all(
      deployVerticle(new AStockTradeTimeTicker(new TickerProperties()), new DeploymentOptions()),
      deployVerticle(new AStockCrawler(new JsonObject(), aStockName), new DeploymentOptions()),
      deployVerticle(new AStockRepo(repoUtil, aStockName), new DeploymentOptions()),
      deployVerticle(new HttpDispatcher(repoUtil), new DeploymentOptions())
    ).onComplete(r -> {
      if (r.succeeded()) {
        log.info("deploy all verticle finish");
      } else {
        log.error("deploy verticle failed, err: {}", r.cause().toString());
      }
    });
  }

  private Future<Void> deployVerticle(Verticle verticle, DeploymentOptions options) {
    Promise<Void> promise = Promise.promise();
    if (options.getInstances() == 0) {
      promise.complete();
    } else {
      vertx.deployVerticle(verticle, options, report(promise));
    }
    return promise.future();
  }

  private <T> Handler<AsyncResult<T>> report(Promise<Void> promise) {
    return ar -> {
      if (ar.succeeded()) {
        promise.complete();
      } else {
        ar.cause().printStackTrace();
        promise.fail(ar.cause());
      }
    };
  }
}
