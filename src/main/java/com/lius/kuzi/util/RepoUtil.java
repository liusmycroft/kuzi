package com.lius.kuzi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Helper and wrapper class for JDBC repository services.
 *
 * @author Eric Zhao
 */
public class RepoUtil {

  protected final PgPool client;

  public RepoUtil(Vertx vertx, JsonObject config) {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost(config.getString("host"))
      .setDatabase(config.getString("database"))
      .setUser(config.getString("user"))
      .setPassword(config.getString("password"));

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);
    client = PgPool.pool(vertx, connectOptions, poolOptions);
  }

  private Map<String, Object> buildParam(Map<String, String> params) {
    return new HashMap<>(params);
  }

  public void executeNoResult(Tuple tuple, String sql, Handler<AsyncResult<Void>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> connection.preparedQuery(sql)
      .execute(tuple)
      .onComplete(r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      })));
  }

  public void executeBatchNoResult(List<Tuple> tuple, String sql, Handler<AsyncResult<Void>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> connection.preparedQuery(sql)
      .executeBatch(tuple)
      .onComplete(r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      })));
  }

  public <R> void execute(Tuple tuple, String sql, R ret, Handler<AsyncResult<R>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> connection.preparedQuery(sql)
      .execute(tuple)
      .onComplete(r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture(ret));
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      })));
  }

  public <T> Future<T> getOne(Map<String, Object> param, String sql, RowMapper<T> mapper) {
    Promise<T> promise = Promise.promise();
    SqlTemplate
      .forQuery(client, sql)
      .mapTo(mapper)
      .execute(param)
      .onComplete(r -> {
        if (r.succeeded()) {
          List<T> list = rowSetToList(r.result());
          if (CollectionUtils.isEmpty(list)) {
            promise.complete(null);
          } else {
            promise.complete(list.get(0));
          }
        } else {
          promise.fail(r.cause());
        }
      });
    return promise.future();
  }

  public <T> Future<List<T>> getAll(Map<String, String> param, String sql, RowMapper<T> mapper) {
    Promise<List<T>> promise = Promise.promise();
    SqlTemplate
      .forQuery(client, sql)
      .mapTo(mapper)
      .execute(buildParam(param))
      .onComplete(r -> {
        if (r.succeeded()) {
          List<T> list = rowSetToList(r.result());
          if (CollectionUtils.isEmpty(list)) {
            promise.complete(null);
          } else {
            promise.complete(list);
          }
        } else {
          promise.fail(r.cause());
        }
      });
    return promise.future();
  }

  protected <R> Handler<AsyncResult<SqlConnection>> connHandler(Handler<AsyncResult<R>> h1,
    Handler<SqlConnection> h2) {
    return conn -> {
      if (conn.succeeded()) {
        final SqlConnection connection = conn.result();
        h2.handle(connection);
      } else {
        h1.handle(Future.failedFuture(conn.cause()));
      }
    };
  }

  private <T> List<T> rowSetToList(RowSet<T> rowSet) {
    List<T> list = new ArrayList<>();
    rowSet.forEach(list::add);
    return list;
  }

}
