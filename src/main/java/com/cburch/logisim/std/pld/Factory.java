package com.cburch.logisim.std.pld;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.instance.Port;

import java.awt.*;

public class Factory {
  static final int NUM_PINS = 22;
  static final int NUM_IMPUTS = 12;

  static final int IN0_CLK = 0;
  static final int IN1 = 1;
  static final int IN2 = 2;
  static final int IN3 = 3;
  static final int IN4 = 4;
  static final int IN5 = 5;
  static final int IN6 = 6;
  static final int IN7 = 7;
  static final int IN8 = 8;
  static final int IN9 = 9;
  static final int IN10 = 10;
  static final int IN11 = 11;

  static final int IN_OUT0 = 12;
  static final int IN_OUT1 = 13;
  static final int IN_OUT2 = 14;
  static final int IN_OUT3 = 15;
  static final int IN_OUT4 = 16;
  static final int IN_OUT5 = 17;
  static final int IN_OUT6 = 18;
  static final int IN_OUT7 = 19;
  static final int IN_OUT8 = 20;
  static final int IN_OUT9 = 21;

  public static String getPinType(int pin) {
    if (IN0_CLK <= pin && pin <= IN11) {
      return Port.INPUT;
    }

    return Port.INOUT;
  }

  public static Point getPoint(int pin) {
    if (getPinType(pin).equals(Port.INPUT)) {
      return new Point(0, pin * 20);
    }

    return new Point(100, (pin - IN_OUT0) * 20);
  }

  public static Port createPort(int pin) {
    var pinType = getPinType(pin);
    var point = getPoint(pin);

    return new Port(point.x, point.y, pinType, BitWidth.ONE);
  }

  public static String getLabel(int pin) {
    if (getPinType(pin).equals(Port.INPUT)) {
      return String.format("I %d%s", pin, pin == IN0_CLK ? "/CLK" : "");
    }

    return String.format("I/O %d", pin - NUM_IMPUTS);
  }
}
