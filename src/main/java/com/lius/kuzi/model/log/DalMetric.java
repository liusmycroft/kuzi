package com.lius.kuzi.model.log;

import lombok.Data;

import static com.lius.kuzi.constant.LogConstants.LOG_METRIC_SEPARATOR;

@Data
public class DalMetric {
    private String  daoMethod;
    private long    time;
    private boolean success = true;
    private String  traceId;
    private Throwable  err;
    public String printDigest() {
        return daoMethod + LOG_METRIC_SEPARATOR
            + time + LOG_METRIC_SEPARATOR
            + success + LOG_METRIC_SEPARATOR
            + traceId + LOG_METRIC_SEPARATOR
            + err;
    }
}
