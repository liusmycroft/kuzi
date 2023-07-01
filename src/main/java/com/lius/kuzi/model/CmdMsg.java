package com.lius.kuzi.model;

import com.lius.kuzi.model.enums.CmdMsgType;
import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class CmdMsg {
  private CmdMsgType msgType;
  private JsonObject param;
}
