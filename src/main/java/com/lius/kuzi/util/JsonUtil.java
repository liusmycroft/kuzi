package com.lius.kuzi.util;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JsonUtil {
    public static List<JsonObject> arrToList(JsonArray array) {
        return array.stream()
            .map(x -> (JsonObject)x)
            .collect(Collectors.toList());
    }
}
