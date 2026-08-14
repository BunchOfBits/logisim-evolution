/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.msi;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.std.ttl.AbstractTtlGate;
import com.cburch.logisim.std.ttl.TtlRegisterData;

/**
 * Am2910: Microprogram Controller
 * Model based on <a href="http://bitsavers.org/components/amd/bitslice/1979_AMD_2900family.pdf#page=143">Am2910 datasheet</a>.
 */
public class Am2910 extends AbstractTtlGate {
  /**
   * Unique identifier of the tool, used as reference in project files.
   * Do NOT change as it will prevent project files from loading.
   * Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "Am2910";

  public static final int DELAY = 1;

  // IC pin indices as specified in the datasheet

  // Inputs
  public static final byte D0 = 34;
  public static final byte D1 = 36;
  public static final byte D2 = 38;
  public static final byte D3 = 40;
  public static final byte D4 = 2;
  public static final byte D5 = 4;
  public static final byte D6 = 17;
  public static final byte D7 = 19;
  public static final byte D8 = 21;
  public static final byte D9 = 23;
  public static final byte D10 = 25;
  public static final byte D11 = 27;

  public static final byte RLDn = 15;

  public static final byte CCn = 14;
  public static final byte CCENn = 13;

  public static final byte I0 = 12;
  public static final byte I1 = 11;
  public static final byte I2 = 9;
  public static final byte I3 = 8;

  public static final byte OEn = 29;

  public static final byte CI = 32;

  public static final byte CLK = 31;

  // Outputs
  public static final byte PLn = 6;
  public static final byte MAPn = 7;
  public static final byte VECTn = 5;

  public static final byte Y0 = 33;
  public static final byte Y1 = 35;
  public static final byte Y2 = 37;
  public static final byte Y3 = 39;
  public static final byte Y4 = 1;
  public static final byte Y5 = 3;
  public static final byte Y6 = 18;
  public static final byte Y7 = 20;
  public static final byte Y8 = 22;
  public static final byte Y9 = 24;
  public static final byte Y10 = 26;
  public static final byte Y11 = 28;

  public static final byte FULLn = 16;

  // Bidirectional

  // Power supply
  public static final byte VCC = 10;
  public static final byte GND = 30;

  // Pin groups
  private static final Byte[] I_INPUTS = new Byte[] { I0, I1, I2, I3 };
  private static final Byte[] D_INPUTS = new Byte[] { D0, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11 };
  private static final Byte[] Y_OUTPUTS = new Byte[] { Y0, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8, Y9, Y10, Y11 };

  // Common data values
  private static final BitWidth ADDRESS_WIDTH = BitWidth.create(12);
  private static final Value ZERO_ADDRESS = Value.createKnown(ADDRESS_WIDTH, 0);
  private static final Value UNKNOWN_ADDRESS = Value.createUnknown(ADDRESS_WIDTH);
  private static final Value ERROR_ADDRESS = Value.createError(ADDRESS_WIDTH);

  // TtlRegisterData structure
  // 0..4 - Stack
  // 5    - Stack pointer
  // 6    - μPC
  // 7    - R register
  private static final int NR_OF_REG = 8;
  private static final int SP_REG_IX = 5;
  private static final int μPC_REG_IX = 6;
  private static final int R_REG_IX = 7;

  public Am2910() {
    super(
        _ID,
        (byte) 40,
        new byte[] { PLn, MAPn, VECTn, FULLn, Y0, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8, Y9, Y10, Y11 },
        null,
        null,
        new String[] {
            "Y4", "D4", "Y5", "D5", "VECTn", "PLn", "MAPn", "I3", "I2",
            "I1", "I0", "CCENn", "CCn", "RLDn", "FULLn", "D6", "Y6", "D7", "Y7",
            "D8", "Y8", "D9", "Y9", "D10", "Y10", "D11", "Y11", "OEn",
            "CLK", "CI", "Y0", "D0", "Y1", "D1", "Y2", "D2", "Y3", "D3"
        },
        false,
        120,
        new byte[] { VCC },
        new byte[] { GND },
        null);
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
  }

  private record LogicScope(InstanceState state, TtlRegisterData data) {
    LogicScope(InstanceState state) {
      this(state, getData(state));
    }

    /**
     * IC pin indices are datasheet based (1-indexed), but ports are 0-indexed
     *
     * @param dsPinNr datasheet pin number
     * @return port number
     */
    private byte pinNrToPortNr(byte dsPinNr) {
      if (dsPinNr <= VCC) {
        return (byte) (dsPinNr - 1);
      }

      if (dsPinNr <= GND) {
        return (byte) (dsPinNr - 2);
      }

      return (byte) (dsPinNr - 3);
    }

    /**
     * Gets the current state of the specified pin
     *
     * @param dsPinNr datasheet pin number
     * @return the current state of the specified pin
     */
    private Value getPort(byte dsPinNr) {
      return state.getPortValue(pinNrToPortNr(dsPinNr));
    }

    /**
     * Gets a Value from the current state of a set of pins
     *
     * @param pins the input pins, must be less than 32 pins
     * @return the combined binary value
     */
    private Value getPort(Byte[] pins) {
      var value = Value.createKnown(pins.length, 0);

      for (var i = 0; i < pins.length; i++) {
        var pin = getPort(pins[i]);

        value = value.set(i, pin.isFullyDefined() ? pin : Value.UNKNOWN);
      }

      return value;
    }

    /**
     * Sets the specified pin to the specified value
     *
     * @param dsPinNr datasheet pin number
     * @param v       the value for the pin
     */
    private void setPort(byte dsPinNr, Value v) {
      state.setPort(pinNrToPortNr(dsPinNr), v, DELAY);
    }

    /**
     * Sets the pins to the Value.
     *
     * @param pins the set of output pins.
     * @param v    the value to set.
     */
    private void setPort(Byte[] pins, Value v) {
      for (var i = 0; i < pins.length; i++) {
        setPort(pins[i], v.get(i));
      }
    }

    /**
     * Gets the value of a register from the register file
     *
     * @param i the index into the register file
     * @return the value of the register
     */
    private Value getRegister(int i) {
      return data.getValue(i);
    }

    /**
     * Gets the value of a register from the register file.
     *
     * @param i the index into the register file.
     * @return the value of the register.
     */
    private Value getRegister(Value i) {
      // Filter ERROR bits in i
      if (i.isErrorValue()) {
        return ERROR_ADDRESS;
      }

      var ix = i.toLongValue();

      // Filter UNKNOWN bits in i
      if (ix == -1L) {
        return UNKNOWN_ADDRESS;
      }

      return getRegister((int) ix);
    }

    /**
     * Sets the selected register in the register file to the specified value.
     *
     * @param i the index into the register file.
     * @param v the value to store.
     */
    private void setRegister(Value i, Value v) {
      // Filter ERROR bits in i
      if (!i.isFullyDefined()) {
        return;
      }

      var ix = i.toLongValue();

      setRegister((int) ix, v);
    }

    /**
     * Sets the selected register in the register file to the specified value.
     *
     * @param i the index into the register file.
     * @param v the value to store.
     */
    private void setRegister(int i, Value v) {
      data.setValue(i, v);
    }

    /**
     * Gets the instance data
     *
     * @return the instance data
     */
    private static TtlRegisterData getData(InstanceState state) {
      var data = (TtlRegisterData) state.getData();

      if (data == null) {
        data = new TtlRegisterData(ADDRESS_WIDTH, NR_OF_REG);
        state.setData(data);
      }

      return data;
    }

    public void propagate() {
    }
  }

  @Override
  public void propagateTtl(InstanceState state) {
    new LogicScope(state).propagate();
  }
}
