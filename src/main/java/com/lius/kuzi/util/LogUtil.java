package com.lius.kuzi.util;

import com.lius.kuzi.model.log.DalMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lius.kuzi.constant.LogConstants.DAL_DIGEST;

public class LogUtil {

    private static final Logger logDalDigest = LoggerFactory.getLogger(DAL_DIGEST);

    public static void printDalError(String traceId, String daoMethod, long time, Throwable throwable) {
        DalMetric dalMetric = new DalMetric();
        dalMetric.setTraceId(traceId);
        dalMetric.setDaoMethod(daoMethod);
        dalMetric.setTime(time);
        dalMetric.setSuccess(false);
        dalMetric.setErr(throwable);
        logDalDigest.error(dalMetric.printDigest());
    }

    public static void printDalSuccess(String traceId, String daoMethod, long time) {
        DalMetric dalMetric = new DalMetric();
        dalMetric.setTraceId(traceId);
        dalMetric.setDaoMethod(daoMethod);
        dalMetric.setTime(time);
        dalMetric.setSuccess(true);
        logDalDigest.info(dalMetric.printDigest());
    }
}
