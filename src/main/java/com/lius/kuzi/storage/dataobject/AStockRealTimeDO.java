package com.lius.kuzi.storage.dataobject;

import java.math.BigDecimal;
import java.util.Date;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import lombok.Data;

@Data
@DataObject
@RowMapped
public class AStockRealTimeDO {
    private Long id;
    private Date createTime;
    private String code;
    private String name;
    private BigDecimal latestPrice;
    private BigDecimal changePercentage;
    private BigDecimal changeAmount;
    private BigDecimal turnover;
    private BigDecimal volume;

    private BigDecimal amplitude;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal openPrice;
    private BigDecimal closePrice;

    private BigDecimal quantityRelativeRatio;
    private BigDecimal turnoverRate;
    private BigDecimal peDynamic;
    private BigDecimal pbr;
    private BigDecimal stockValue;

    private BigDecimal circulatedStockValue;
    private BigDecimal accr;
    private BigDecimal fiveMinChangePercentage;
    private BigDecimal sixtyDayChangePercentage;
    private BigDecimal yearToDateChangePercentage;
}
