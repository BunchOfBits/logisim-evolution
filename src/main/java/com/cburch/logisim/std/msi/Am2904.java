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
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.std.ttl.AbstractTtlGate;
import com.cburch.logisim.std.ttl.TtlRegisterData;

/**
 * Am2904: Status and shift control unit
 * Model based on <a href="http://bitsavers.org/components/amd/bitslice/1979_AMD_2900family.pdf#page=92">Am2904 datasheet</a>.
 */
public class Am2904 extends AbstractTtlGate {
  /**
   * Unique identifier of the tool, used as reference in project files.
   * Do NOT change as it will prevent project files from loading.
   * Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "Am2904";

  public static final int DELAY = 1;

  // IC pin indices as specified in the datasheet

  // Inputs
  public static final byte I0 = 18;
  public static final byte I1 = 19;
  public static final byte I2 = 21;
  public static final byte I3 = 6;
  public static final byte I4 = 5;
  public static final byte I5 = 4;
  public static final byte I6 = 3;
  public static final byte I7 = 1;
  public static final byte I8 = 40;
  public static final byte I9 = 39;
  public static final byte I10 = 38;
  public static final byte I11 = 22;
  public static final byte I12 = 23;

  public static final byte CEmsr_n = 7;
  public static final byte EN_n = 13;
  public static final byte EZ_n = 8;
  public static final byte EV_n = 15;
  public static final byte EC_n = 11;

  public static final byte IN = 14;
  public static final byte IZ = 9;
  public static final byte IV = 16;
  public static final byte IC = 12;

  public static final byte CEμ_n = 2;

  public static final byte Cx = 24;

  public static final byte OEct_n = 26;

  public static final byte OEy_n = 20;

  public static final byte SE_n = 37;

  public static final byte CLK = 17;

  // Outputs
  public static final byte CT = 27;

  public static final byte Co = 25;

  // Bidirectional
  public static final byte QIOn = 33;
  public static final byte QIO0 = 34;
  public static final byte SIOn = 35;
  public static final byte SIO0 = 36;

  public static final byte YN = 29;
  public static final byte YZ = 32;
  public static final byte YV = 28;
  public static final byte YC = 31;

  // Power supply
  public static final byte VCC = 10;
  public static final byte GND = 30;

  // Pin groups
  private static final Byte[] I_INPUTS = new Byte[] { I0, I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12 };
  private static final Byte[] SR_OP_INPUTS = new Byte[] { I0, I1, I2, I3, I4, I5 };

  // Common data values
  private static final BitWidth REGISTER_WIDTH = BitWidth.create(4);

  private static final int N_BIT_IX = 3;
  private static final int Z_BIT_IX = 2;
  private static final int V_BIT_IX = 1;
  private static final int C_BIT_IX = 0;

  private static final Value ZERO_REGISTER = Value.createKnown(REGISTER_WIDTH, 0);
  private static final Value ONE_REGISTER = Value.createKnown(REGISTER_WIDTH, 1);
  private static final Value UNKNOWN_REGISTER = Value.createUnknown(REGISTER_WIDTH);
  private static final Value ERROR_REGISTER = Value.createError(REGISTER_WIDTH);

  // TtlRegisterData structure
  // 0 - μSR
  // 1 - MSR
  private static final int NR_OF_REG = 2;
  private static final int μSR_REG_IX = 0;
  private static final int MSR_REG_IX = 1;

  // μSR opcodes
  private static final int μSR_LOAD_MSR = 0_00;
  private static final int μSR_SET = 0_01;
  private static final int μSR_SWAP = 0_02;
  private static final int μSR_RESET = 0_03;

  private static final int μSR_LOAD_RETAIN_V_0 = 0_06;
  private static final int μSR_LOAD_RETAIN_V_1 = 0_07;

  private static final int μSR_LOAD_INVERT_C_0 = 0_30;
  private static final int μSR_LOAD_INVERT_C_1 = 0_31;
  private static final int μSR_LOAD_INVERT_C_2 = 0_50;
  private static final int μSR_LOAD_INVERT_C_3 = 0_51;
  private static final int μSR_LOAD_INVERT_C_4 = 0_70;
  private static final int μSR_LOAD_INVERT_C_5 = 0_71;

  private static final int μSR_CLEAR_N = 0_14;
  private static final int μSR_SET_N = 0_15;
  private static final int μSR_CLEAR_Z = 0_10;
  private static final int μSR_SET_Z = 0_11;
  private static final int μSR_CLEAR_V = 0_16;
  private static final int μSR_SET_V = 0_17;
  private static final int μSR_CLEAR_C = 0_12;
  private static final int μSR_SET_C = 0_13;

  // MSR opcodes
  private static final int MSR_LOAD_Y = 0_00;
  private static final int MSR_SET = 0_01;
  private static final int MSR_SWAP = 0_02;
  private static final int MSR_RESET = 0_03;
  private static final int MSR_INVERT = 0_05;

  private static final int MSR_LOAD_FOR_SHIFT = 0_04;

  private static final int MSR_LOAD_INVERT_C_0 = 0_10;
  private static final int MSR_LOAD_INVERT_C_1 = 0_11;
  private static final int MSR_LOAD_INVERT_C_2 = 0_30;
  private static final int MSR_LOAD_INVERT_C_3 = 0_31;
  private static final int MSR_LOAD_INVERT_C_4 = 0_50;
  private static final int MSR_LOAD_INVERT_C_5 = 0_51;
  private static final int MSR_LOAD_INVERT_C_6 = 0_70;
  private static final int MSR_LOAD_INVERT_C_7 = 0_71;

  public Am2904() {
    super(
        _ID,
        (byte) 40,
        new byte[] { CT, Co },
        null,
        new byte[] { QIOn, QIO0, SIOn, SIO0, YN, YZ, YV, YC },
        new String[] {
            "I7", "CEμ_n", "I6", "I5", "I4", "I3", "CEm_n", "Ez_n", "Iz",
            "Ec_n", "Ic", "En_n", "In", "Eovr_n", "Iovr", "CLK", "I0", "I1", "OEy_n",
            "I2", "I11", "I12", "Cx", "C0", "OEct_n", "CT", "Yovr", "Yn",
            "Yc", "Yz", "QIOn", "QIO0", "SIOn", "SIO0", "SE_n", "I10", "I9", "I8"
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
        return ERROR_REGISTER;
      }

      var ix = i.toLongValue();

      // Filter UNKNOWN bits in i
      if (ix == -1L) {
        return UNKNOWN_REGISTER;
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
        data = new TtlRegisterData(REGISTER_WIDTH, NR_OF_REG);
        state.setData(data);
      }

      return data;
    }

    public void propagate() {
      // Sample inputs
      final var i = getPort(I_INPUTS);
      final var srOp = getPort(SR_OP_INPUTS);
      final var i4 = getPort(I4);
      final var i5 = getPort(I5);

      final var cemsr_n = getPort(CEmsr_n);
      final var en_n = getPort(EN_n);
      final var ez_n = getPort(EZ_n);
      final var ev_n = getPort(EV_n);
      final var ec_n = getPort(EC_n);

      final var in = getPort(IN);
      final var iz = getPort(IZ);
      final var iv = getPort(IV);
      final var ic = getPort(IC);

      final var yn = getPort(YN);
      final var yz = getPort(YZ);
      final var yv = getPort(YV);
      final var yc = getPort(YC);

      final var ceμ_n = getPort(CEμ_n);

      final var cx = getPort(Cx);

      final var oect_n = getPort(OEct_n);

      final var oey_n = getPort(OEy_n);

      final var se_n = getPort(SE_n);

      final var clk = getPort(CLK);

      // Sample registers
      final var μsr = getRegister(μSR_REG_IX);
      final var msr = getRegister(MSR_REG_IX);

      // μSR operations
      var μsrIn = ERROR_REGISTER;

      if (srOp.isFullyDefined()) {
        switch ((int) srOp.toLongValue()) {
          case μSR_LOAD_MSR:
          case μSR_SWAP:
            μsrIn = msr;
            break;
          case μSR_SET:
            μsrIn = ONE_REGISTER;
            break;
          case μSR_RESET:
            μsrIn = ZERO_REGISTER;
            break;
          case μSR_LOAD_RETAIN_V_0:
          case μSR_LOAD_RETAIN_V_1:
            μsrIn = μsr.set(N_BIT_IX, in);
            μsrIn = μsrIn.set(Z_BIT_IX, iz);
            // Retain V bit, so nothing to do for V
            μsrIn = μsrIn.set(C_BIT_IX, ic);
            break;
          case μSR_LOAD_INVERT_C_0:
          case μSR_LOAD_INVERT_C_1:
          case μSR_LOAD_INVERT_C_2:
          case μSR_LOAD_INVERT_C_3:
          case μSR_LOAD_INVERT_C_4:
          case μSR_LOAD_INVERT_C_5:
            μsrIn = μsr.set(N_BIT_IX, in);
            μsrIn = μsrIn.set(Z_BIT_IX, iz);
            μsrIn = μsrIn.set(V_BIT_IX, iv);
            μsrIn = μsrIn.set(C_BIT_IX, ic.not());
            break;
          case μSR_CLEAR_N:
            μsrIn = μsr.set(N_BIT_IX, Value.FALSE);
            break;
          case μSR_SET_N:
            μsrIn = μsr.set(N_BIT_IX, Value.TRUE);
            break;
          case μSR_CLEAR_Z:
            μsrIn = μsr.set(Z_BIT_IX, Value.FALSE);
            break;
          case μSR_SET_Z:
            μsrIn = μsr.set(Z_BIT_IX, Value.TRUE);
            break;
          case μSR_CLEAR_V:
            μsrIn = μsr.set(V_BIT_IX, Value.FALSE);
            break;
          case μSR_SET_V:
            μsrIn = μsr.set(V_BIT_IX, Value.TRUE);
            break;
          case μSR_CLEAR_C:
            μsrIn = μsr.set(C_BIT_IX, Value.FALSE);
            break;
          case μSR_SET_C:
            μsrIn = μsr.set(C_BIT_IX, Value.TRUE);
            break;
          default:
            μsrIn = μsr.set(N_BIT_IX, in);
            μsrIn = μsrIn.set(Z_BIT_IX, iz);
            μsrIn = μsrIn.set(V_BIT_IX, iv);
            μsrIn = μsrIn.set(C_BIT_IX, ic);
            break;
        }
      }

      // MSR operations
      var msrIn = ERROR_REGISTER;

      if (srOp.isFullyDefined()) {
        switch ((int) srOp.toLongValue()) {
          case MSR_LOAD_Y:
            msrIn = msr.set(N_BIT_IX, yn);
            msrIn = msrIn.set(Z_BIT_IX, yz);
            msrIn = msrIn.set(V_BIT_IX, yv);
            msrIn = msrIn.set(C_BIT_IX, yc);
            break;
          case MSR_SET:
            msrIn = ONE_REGISTER;
            break;
          case MSR_SWAP:
            msrIn = μsr;
            break;
          case MSR_RESET:
            msrIn = ZERO_REGISTER;
            break;
          case MSR_INVERT:
            msrIn = msr.not();
            break;
          case MSR_LOAD_FOR_SHIFT:
            msrIn = msr.set(N_BIT_IX, in);
            msrIn = msrIn.set(Z_BIT_IX, iz);
            msrIn = msrIn.set(V_BIT_IX, msr.get(C_BIT_IX));
            msrIn = msrIn.set(C_BIT_IX, msr.get(V_BIT_IX));
            break;
          case MSR_LOAD_INVERT_C_0:
          case MSR_LOAD_INVERT_C_1:
          case MSR_LOAD_INVERT_C_2:
          case MSR_LOAD_INVERT_C_3:
          case MSR_LOAD_INVERT_C_4:
          case MSR_LOAD_INVERT_C_5:
          case MSR_LOAD_INVERT_C_6:
          case MSR_LOAD_INVERT_C_7:
            msrIn = msr.set(N_BIT_IX, in);
            msrIn = msrIn.set(Z_BIT_IX, iz);
            msrIn = msrIn.set(V_BIT_IX, iv);
            msrIn = msrIn.set(C_BIT_IX, ic.not());
            break;
          default:
            msrIn = msr.set(N_BIT_IX, in);
            msrIn = msrIn.set(Z_BIT_IX, iz);
            msrIn = msrIn.set(V_BIT_IX, iv);
            msrIn = msrIn.set(C_BIT_IX, ic);
            break;
        }
      }

      // Y
      var y = Value.ERROR;

      if (oey_n == Value.TRUE) {
        y = Value.UNKNOWN;
      } else if (oey_n == Value.FALSE) {
        if (srOp.isFullyDefined() && srOp.toLongValue() == 0) {
          y = Value.UNKNOWN;
        } else if (i5 == Value.FALSE) {

        }
      }

      // μSR register
      if (data.updateClock(clk, μSR_REG_IX, StdAttr.TRIG_RISING)) {
        setRegister(μSR_REG_IX, μsrIn);
      }
    }
  }

  @Override
  public void propagateTtl(InstanceState state) {
    new LogicScope(state).propagate();
  }
}
