package com.lius.kuzi.model.log;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.lius.kuzi.constant.LogConstants.LOG_METRIC_SEPARATOR;

@Data
@Slf4j
public class CrawlMetric {
  private boolean success;
  private long costTime;
  private long responseSize;
  private String url;
  private String err;
  private String traceId;

  public String printDigest() {
    return success + LOG_METRIC_SEPARATOR
      + costTime + LOG_METRIC_SEPARATOR
      + responseSize + LOG_METRIC_SEPARATOR
      + url + LOG_METRIC_SEPARATOR
      + err + LOG_METRIC_SEPARATOR
      + traceId + LOG_METRIC_SEPARATOR;
  }
}
