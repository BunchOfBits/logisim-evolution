package com.cburch.logisim.std.pld;

import com.cburch.logisim.instance.InstanceData;

public class FuseMap implements InstanceData {
  public static final FuseMap EMPTY = new FuseMap();

  public static FuseMap parse(String str) {
    return new FuseMap();
  }

  @Override
  public Object clone() {
    return new FuseMap();
  }

  public String toStandardString() {
    return "";
  }
}