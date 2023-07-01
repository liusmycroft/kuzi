package com.lius.kuzi.crawler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lius.kuzi.constant.AkToolsUrlConstant;
import com.lius.kuzi.model.log.CrawlMetric;
import com.lius.kuzi.util.TimeUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuyixi
 */
@Slf4j
public class AStockCrawler extends RawCrawler {

    public AStockCrawler(JsonObject initParam, String name) {
        super(name, initParam);
    }

    @Override
    protected void tick(JsonObject param) {
        String traceId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        client.get(AkToolsUrlConstant.PORT, AkToolsUrlConstant.URL, AkToolsUrlConstant.A_STOCK_REALTIME_URL)
            .expect(ResponsePredicate.JSON)
            .expect(ResponsePredicate.SC_SUCCESS)
            .send()
            .onSuccess(x -> handleSuccess(x, now, traceId))
            .onFailure(x -> handleFail(x, now, traceId));
    }

    private void handleSuccess(HttpResponse<Buffer> response, LocalDateTime dateTime, String traceId) {
        CrawlMetric msg = new CrawlMetric();
        msg.setUrl(AkToolsUrlConstant.A_STOCK_REALTIME_URL);
        msg.setSuccess(true);
        msg.setResponseSize(response.body().length());
        msg.setCostTime(System.currentTimeMillis() - TimeUtil.localDateTimeToMillis(dateTime));
        msg.setTraceId(traceId);
        log.info(msg.printDigest());

        List<JsonObject> data = response.bodyAsJsonArray()
            .stream()
            .map(x -> (JsonObject)x)
            .collect(Collectors.toList());
        publishData(JsonObject.of("data", data)
            .put("timestamp", dateTime.toInstant(ZoneOffset.of("+8")))
            .put("traceId", traceId));
    }

    private void handleFail(Throwable err, LocalDateTime dateTime, String traceId) {
        CrawlMetric msg = new CrawlMetric();
        msg.setUrl(AkToolsUrlConstant.A_STOCK_REALTIME_URL);
        msg.setSuccess(false);
        msg.setCostTime(System.currentTimeMillis() - TimeUtil.localDateTimeToMillis(dateTime));
        msg.setErr(err.toString());
        msg.setTraceId(traceId);
        log.error(msg.printDigest());
    }
}
