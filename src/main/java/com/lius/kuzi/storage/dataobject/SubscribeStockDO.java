package com.lius.kuzi.storage.dataobject;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import lombok.Data;

@Data
@DataObject
@RowMapped
public class SubscribeStockDO {
    private Long id;
    private String code;
}
