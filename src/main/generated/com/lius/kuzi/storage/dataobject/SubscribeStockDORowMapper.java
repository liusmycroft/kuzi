package com.lius.kuzi.storage.dataobject;

/**
 * Mapper for {@link SubscribeStockDO}.
 * NOTE: This class has been automatically generated from the {@link SubscribeStockDO} original class using Vert.x codegen.
 */
@io.vertx.codegen.annotations.VertxGen
public interface SubscribeStockDORowMapper extends io.vertx.sqlclient.templates.RowMapper<SubscribeStockDO> {

  @io.vertx.codegen.annotations.GenIgnore
  SubscribeStockDORowMapper INSTANCE = new SubscribeStockDORowMapper() { };

  @io.vertx.codegen.annotations.GenIgnore
  java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<SubscribeStockDO>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());

  @io.vertx.codegen.annotations.GenIgnore
  default SubscribeStockDO map(io.vertx.sqlclient.Row row) {
    SubscribeStockDO obj = new SubscribeStockDO();
    Object val;
    int idx;
    return obj;
  }
}
