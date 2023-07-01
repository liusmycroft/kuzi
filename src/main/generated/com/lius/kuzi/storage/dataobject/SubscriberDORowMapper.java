package com.lius.kuzi.storage.dataobject;

/**
 * Mapper for {@link SubscriberDO}.
 * NOTE: This class has been automatically generated from the {@link SubscriberDO} original class using Vert.x codegen.
 */
@io.vertx.codegen.annotations.VertxGen
public interface SubscriberDORowMapper extends io.vertx.sqlclient.templates.RowMapper<SubscriberDO> {

  @io.vertx.codegen.annotations.GenIgnore
  SubscriberDORowMapper INSTANCE = new SubscriberDORowMapper() { };

  @io.vertx.codegen.annotations.GenIgnore
  java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<SubscriberDO>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());

  @io.vertx.codegen.annotations.GenIgnore
  default SubscriberDO map(io.vertx.sqlclient.Row row) {
    SubscriberDO obj = new SubscriberDO();
    Object val;
    int idx;
    return obj;
  }
}
