package com.lius.kuzi.storage.dataobject;

/**
 * Mapper for {@link AStockRealTimeDO}.
 * NOTE: This class has been automatically generated from the {@link AStockRealTimeDO} original class using Vert.x codegen.
 */
@io.vertx.codegen.annotations.VertxGen
public interface AStockRealTimeDORowMapper extends io.vertx.sqlclient.templates.RowMapper<AStockRealTimeDO> {

  @io.vertx.codegen.annotations.GenIgnore
  AStockRealTimeDORowMapper INSTANCE = new AStockRealTimeDORowMapper() { };

  @io.vertx.codegen.annotations.GenIgnore
  java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<AStockRealTimeDO>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());

  @io.vertx.codegen.annotations.GenIgnore
  default AStockRealTimeDO map(io.vertx.sqlclient.Row row) {
    AStockRealTimeDO obj = new AStockRealTimeDO();
    Object val;
    int idx;
    return obj;
  }
}
