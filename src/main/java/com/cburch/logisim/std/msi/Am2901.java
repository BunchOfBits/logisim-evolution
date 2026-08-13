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
 * Am2901: 4-bit microprocessor slice
 * Model based on <a href="http://bitsavers.org/components/amd/bitslice/1979_AMD_2900family.pdf#page=9">Am2901 datasheet</a>.
 */
public class Am2901 extends AbstractTtlGate {
  /**
   * Unique identifier of the tool, used as reference in project files.
   * Do NOT change as it will prevent project files from loading.
   * Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "Am2901";

  public static final int DELAY = 1;

  // IC pin indices as specified in the datasheet

  // Inputs
  public static final byte A0 = 4;
  public static final byte A1 = 3;
  public static final byte A2 = 2;
  public static final byte A3 = 1;

  public static final byte B0 = 17;
  public static final byte B1 = 18;
  public static final byte B2 = 19;
  public static final byte B3 = 20;

  public static final byte D0 = 25;
  public static final byte D1 = 24;
  public static final byte D2 = 23;
  public static final byte D3 = 22;

  public static final byte C0 = 29;

  public static final byte I0 = 12;
  public static final byte I1 = 13;
  public static final byte I2 = 14;
  public static final byte I3 = 26;
  public static final byte I4 = 28;
  public static final byte I5 = 27;
  public static final byte I6 = 5;
  public static final byte I7 = 7;
  public static final byte I8 = 6;

  public static final byte OEn = 40;

  public static final byte CLK = 15;

  // Outputs
  public static final byte Pn = 35;
  public static final byte Gn = 32;
  public static final byte C4 = 33;
  public static final byte OVR = 34;
  public static final byte F3 = 31;
  public static final byte ZERO = 11; // OC output

  public static final byte Y0 = 36; // Tri-state output
  public static final byte Y1 = 37; // Tri-state output
  public static final byte Y2 = 38; // Tri-state output
  public static final byte Y3 = 39; // Tri-state output

  // Bidirectional
  public static final byte RAM0 = 9;
  public static final byte RAM3 = 8;

  public static final byte Q0 = 21;
  public static final byte Q3 = 16;

  // Power supply
  public static final byte VCC = 10;
  public static final byte GND = 30;

  // Pin groups
  private static final Byte[] A_INPUTS = new Byte[] { A0, A1, A2, A3 };
  private static final Byte[] B_INPUTS = new Byte[] { B0, B1, B2, B3 };
  private static final Byte[] D_INPUTS = new Byte[] { D0, D1, D2, D3 };
  private static final Byte[] ALU_SRC = new Byte[] { I0, I1, I2 };
  private static final Byte[] ALU_DEST = new Byte[] { I6, I7, I8 };
  private static final Byte[] ALU_FUNC = new Byte[] { I3, I4, I5 };
  private static final Byte[] Y_OUTPUTS = new Byte[] { Y0, Y1, Y2, Y3 };

  // Common data values
  private static final BitWidth REGISTER_WIDTH = BitWidth.create(4);
  private static final Value ZERO_DATA = Value.createKnown(REGISTER_WIDTH, 0);
  private static final Value UNKNOWN_DATA = Value.createUnknown(REGISTER_WIDTH);
  private static final Value ERROR_DATA = Value.createError(REGISTER_WIDTH);

  // TtlRegisterData structure
  private static final int NR_OF_REG = 16 + 2 + 1;
  private static final int A_REG_IX = 16;
  private static final int B_REG_IX = 17;
  private static final int Q_REG_IX = 18;

  // ALU source operand microcode (see datasheet for names and meaning)
  private static final int AQ = 0;
  private static final int AB = 1;
  private static final int ZQ = 2;
  private static final int ZB = 3;
  private static final int ZA = 4;
  private static final int DA = 5;
  private static final int DQ = 6;
  private static final int DZ = 7;

  // ALU function microcode (see datasheet for names and meaning)
  private static final int ADD = 0;
  private static final int SUBR = 1;
  private static final int SUBS = 2;
  private static final int OR = 3;
  private static final int AND = 4;
  private static final int NOTRS = 5;
  private static final int EXOR = 6;
  private static final int EXNOR = 7;

  // ALU destination microcode (see datasheet for names and meaning)
  private static final int QREG = 0;
  private static final int NOP = 1;
  private static final int RAMA = 2;
  private static final int RAMF = 3;
  private static final int RAMQD = 4;
  private static final int RAMD = 5;
  private static final int RAMQU = 6;
  private static final int RAMU = 7;

  public Am2901() {
    super(
        _ID,
        (byte) 40,
        new byte[] { Pn, Gn, C4, OVR, F3, ZERO, Y0, Y1, Y2, Y3 },
        null,
        new byte[] { RAM0, RAM3, Q0, Q3 },
        new String[] {
            "A3", "A2", "A1", "A0", "I6", "I8", "I7", "RAM3", "RAM0",
            "F=0", "I0", "I1", "I2", "CLK", "Q3", "B0", "B1", "B2", "B3",
            "Q0", "D3", "D2", "D1", "D0", "I3", "I5", "I4", "Cn",
            "F3", "Gn", "Cn+4", "OVR", "Pn", "Y0", "Y1", "Y2", "Y3", "OEn"
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
        return ERROR_DATA;
      }

      var ix = i.toLongValue();

      // Filter UNKNOWN bits in i
      if (ix == -1L) {
        return UNKNOWN_DATA;
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
      final var aluFunc = getPort(ALU_FUNC).toLongValue();
      final var aluSrc = getPort(ALU_SRC).toLongValue();
      final var aluDest = getPort(ALU_DEST).toLongValue();

      final var a = getPort(A_INPUTS);
      final var b = getPort(B_INPUTS);
      final var d = getPort(D_INPUTS);

      final var q0 = getPort(Q0);
      final var q3 = getPort(Q3);

      final var ram0 = getPort(RAM0);
      final var ram3 = getPort(RAM3);

      final var cIn = getPort(C0);

      final var oen = getPort(OEn);

      final var clk = getPort(CLK);

      final var aReg = getRegister(A_REG_IX);
      final var bReg = getRegister(B_REG_IX);
      final var qReg = getRegister(Q_REG_IX);

      // ALU source decode
      var rOp = ERROR_DATA;
      var sOp = ERROR_DATA;

      switch ((int) aluSrc) {
        case AQ:
          rOp = aReg;
          sOp = qReg;
          break;
        case AB:
          rOp = aReg;
          sOp = bReg;
          break;
        case ZQ:
          rOp = ZERO_DATA;
          sOp = qReg;
          break;
        case ZB:
          rOp = ZERO_DATA;
          sOp = bReg;
          break;
        case ZA:
          rOp = ZERO_DATA;
          sOp = aReg;
          break;
        case DA:
          rOp = d;
          sOp = aReg;
          break;
        case DQ:
          rOp = d;
          sOp = qReg;
          break;
        case DZ:
          rOp = d;
          sOp = ZERO_DATA;
          break;
      }

      // ALU function decode
      var f = ERROR_DATA;

      switch ((int) aluFunc) {
        case ADD:
          f = Value.createKnown(REGISTER_WIDTH, rOp.toLongValue() + sOp.toLongValue() + cIn.toLongValue());
          break;
        case SUBR:
          f = Value.createKnown(REGISTER_WIDTH, sOp.toLongValue() - rOp.toLongValue() - cIn.not().toLongValue());
          break;
        case SUBS:
          f = Value.createKnown(REGISTER_WIDTH, rOp.toLongValue() - sOp.toLongValue() - cIn.not().toLongValue());
          break;
        case OR:
          f = Value.createKnown(REGISTER_WIDTH, rOp.toLongValue() | sOp.toLongValue());
          break;
        case AND:
          f = Value.createKnown(REGISTER_WIDTH, rOp.toLongValue() & sOp.toLongValue());
          break;
        case NOTRS:
          f = Value.createKnown(REGISTER_WIDTH, ~rOp.toLongValue() & sOp.toLongValue());
          break;
        case EXOR:
          f = Value.createKnown(REGISTER_WIDTH, rOp.toLongValue() ^ sOp.toLongValue());
          break;
        case EXNOR:
          f = Value.createKnown(REGISTER_WIDTH, ~(rOp.toLongValue() ^ sOp.toLongValue()));
          break;
      }

      // CLA function decode
      var p0 = Value.ERROR;
      var p1 = Value.ERROR;
      var p2 = Value.ERROR;
      var p3 = Value.ERROR;
      var g0 = Value.ERROR;
      var g1 = Value.ERROR;
      var g2 = Value.ERROR;
      var g3 = Value.ERROR;

      switch ((int) aluFunc) {
        case ADD:
        case OR:
        case AND:
        case EXNOR:
          p0 = rOp.get(0).or(sOp.get(0));
          p1 = rOp.get(1).or(sOp.get(1));
          p2 = rOp.get(2).or(sOp.get(2));
          p3 = rOp.get(3).or(sOp.get(3));
          g0 = rOp.get(0).and(sOp.get(0));
          g1 = rOp.get(1).and(sOp.get(1));
          g2 = rOp.get(2).and(sOp.get(2));
          g3 = rOp.get(3).and(sOp.get(3));
          break;
        case SUBR:
        case NOTRS:
        case EXOR:
          p0 = rOp.get(0).not().or(sOp.get(0));
          p1 = rOp.get(1).not().or(sOp.get(1));
          p2 = rOp.get(2).not().or(sOp.get(2));
          p3 = rOp.get(3).not().or(sOp.get(3));
          g0 = rOp.get(0).not().and(sOp.get(0));
          g1 = rOp.get(1).not().and(sOp.get(1));
          g2 = rOp.get(2).not().and(sOp.get(2));
          g3 = rOp.get(3).not().and(sOp.get(3));
          break;
        case SUBS:
          p0 = rOp.get(0).or(sOp.get(0).not());
          p1 = rOp.get(1).or(sOp.get(1).not());
          p2 = rOp.get(2).or(sOp.get(2).not());
          p3 = rOp.get(3).or(sOp.get(3).not());
          g0 = rOp.get(0).and(sOp.get(0).not());
          g1 = rOp.get(1).and(sOp.get(1).not());
          g2 = rOp.get(2).and(sOp.get(2).not());
          g3 = rOp.get(3).and(sOp.get(3).not());
          break;
      }

      final var c3 = g2
          .or(p2.and(g1))
          .or(p2.and(p1).and(g0))
          .or(p2.and(p1).and(p0).and(cIn));
      final var c4 = g3.or(p3.and(c3));

      // ALU status bits decode
      var pn = Value.ERROR;
      var gn = Value.ERROR;
      var cn4 = Value.ERROR;
      var ovr = Value.ERROR;

      switch ((int) aluFunc) {
        case ADD:
        case SUBR:
        case SUBS:
          pn = p3.and(p2).and(p1).and(p0).not();
          gn = g3
              .or(p3.and(g2))
              .or(p3.and(p2).and(g1))
              .or(p3.and(p2).and(p1).and(g0))
              .not();
          cn4 = c4;
          ovr = c3.xor(c4).not();
          break;
        case OR:
          pn = Value.FALSE;
          gn = p3.and(p2).and(p1).and(p0);
          cn4 = gn.not().or(cIn);
          ovr = cn4;
          break;
        case AND:
        case NOTRS:
          pn = Value.FALSE;
          gn = g3.or(g2).or(g1).or(g0).not();
          cn4 = gn.not().or(cIn);
          ovr = cn4;
          break;
        case EXOR:
        case EXNOR:
          pn = g3.or(g2).or(g1).or(g0);
          gn = g3
              .or(p3.and(g2))
              .or(p3.and(p2).and(g1))
              .or(p3.and(p2).and(p1).and(p0));
          cn4 = g3
              .or(p3.and(g2))
              .or(p3.and(p2).and(g1))
              .or(p3.and(p2).and(p1).and(p0).and(g0.or(cIn)))
              .not();
          ovr = p2.not()
              .or(g2.not().and(p1.not()))
              .or(g2.not().and(g1.not()).and(p0.not()))
              .or(g2.not().and(g1.not()).and(g0.not()).and(cIn))
              .xor(p3.not()
                  .or(g3.not().and(p2.not()))
                  .or(g3.not().and(g2.not()).and(p1.not()))
                  .or(g3.not().and(g2.not()).and(g1.not()).and(p0.not()))
                  .or(g3.not().and(g2.not()).and(g1.not()).and(g0.not()).and(cIn)))
              .not();
          break;
      }

      // ALU destination decode
      var rfIn = ERROR_DATA;    // Register file input data
      var qrIn = ERROR_DATA;    // Q register input data
      var y = ERROR_DATA;       // Y output data
      var qrEn = Value.FALSE;   // Q register enable
      var rfEn = Value.FALSE;   // Register file write enable
      var ram0En = Value.FALSE; // RAM0 output enable
      var ram3En = Value.FALSE; // RAM3 output enable
      var q0En = Value.FALSE;   // Q0 output enable
      var q3En = Value.FALSE;   // Q3 output enable

      switch ((int) aluDest) {
        case QREG:
          rfIn = ERROR_DATA;
          qrIn = f;
          y = f;
          qrEn = Value.TRUE;
          rfEn = Value.FALSE;
          ram0En = Value.FALSE;
          ram3En = Value.FALSE;
          q0En = Value.FALSE;
          q3En = Value.FALSE;
          break;
        case NOP:
          rfIn = ERROR_DATA;
          qrIn = ERROR_DATA;
          y = f;
          qrEn = Value.FALSE;
          rfEn = Value.FALSE;
          ram0En = Value.FALSE;
          ram3En = Value.FALSE;
          q0En = Value.FALSE;
          q3En = Value.FALSE;
          break;
        case RAMA:
          rfIn = f;
          qrIn = ERROR_DATA;
          y = aReg;
          qrEn = Value.FALSE;
          rfEn = Value.TRUE;
          ram0En = Value.FALSE;
          ram3En = Value.FALSE;
          q0En = Value.FALSE;
          q3En = Value.FALSE;
          break;
        case RAMF:
          rfIn = f;
          qrIn = ERROR_DATA;
          y = f;
          qrEn = Value.FALSE;
          rfEn = Value.TRUE;
          ram0En = Value.FALSE;
          ram3En = Value.FALSE;
          q0En = Value.FALSE;
          q3En = Value.FALSE;
          break;
        case RAMQD:
          rfIn = f.shr(ram3);
          qrIn = qReg.shr(q3);
          y = f;
          qrEn = Value.TRUE;
          rfEn = Value.TRUE;
          ram0En = Value.TRUE;
          ram3En = Value.TRUE;
          q0En = Value.TRUE;
          q3En = Value.TRUE;
          break;
        case RAMD:
          rfIn = f.shr(ram3);
          qrIn = ERROR_DATA;
          y = f;
          qrEn = Value.FALSE;
          rfEn = Value.TRUE;
          ram0En = Value.TRUE;
          ram3En = Value.TRUE;
          q0En = Value.TRUE;
          q3En = Value.FALSE;
          break;
        case RAMQU:
          rfIn = f.shl(ram0);
          qrIn = qReg.shl(q0);
          y = f;
          qrEn = Value.TRUE;
          rfEn = Value.TRUE;
          ram0En = Value.TRUE;
          ram3En = Value.TRUE;
          q0En = Value.TRUE;
          q3En = Value.TRUE;
          break;
        case RAMU:
          rfIn = f.shl(ram0);
          qrIn = ERROR_DATA;
          y = f;
          qrEn = Value.FALSE;
          rfEn = Value.TRUE;
          ram0En = Value.TRUE;
          ram3En = Value.TRUE;
          q0En = Value.FALSE;
          q3En = Value.TRUE;
          break;
      }

      // ALU output
      if (oen == Value.TRUE) {
        setPort(Y_OUTPUTS, UNKNOWN_DATA);
      } else if (oen == Value.FALSE) {
        setPort(Y_OUTPUTS, y);
      } else {
        setPort(Y_OUTPUTS, ERROR_DATA);
      }

      // ALU status flags
      var fEqZero = f.get(3)
          .or(f.get(2))
          .or(f.get(1))
          .or(f.get(0))
          .not();

      setPort(ZERO, fEqZero);
      setPort(F3, f.get(3));
      setPort(C4, cn4);
      setPort(OVR, ovr);

      // Shift register outputs
      if (q0En == Value.TRUE) {
        setPort(Q0, qReg.get(0));
      } else {
        setPort(Q0, Value.UNKNOWN);
      }

      if (q3En == Value.TRUE) {
        setPort(Q3, qReg.get(3));
      } else {
        setPort(Q3, Value.UNKNOWN);
      }

      if (ram0En == Value.TRUE) {
        setPort(RAM0, f.get(0));
      } else {
        setPort(RAM0, Value.UNKNOWN);
      }

      if (ram3En == Value.TRUE) {
        setPort(RAM3, f.get(3));
      } else {
        setPort(RAM3, Value.UNKNOWN);
      }

      // Pn
      setPort(Pn, pn);

      // Gn
      setPort(Gn, gn);

      // A latch
      if (data.updateClock(clk, A_REG_IX, StdAttr.TRIG_HIGH)) {
        setRegister(A_REG_IX, getRegister(a));
      }

      // B latch
      if (data.updateClock(clk, B_REG_IX, StdAttr.TRIG_HIGH)) {
        setRegister(B_REG_IX, getRegister(b));
      }

      // Q register
      if (data.updateClock(clk, Q_REG_IX, StdAttr.TRIG_RISING) && qrEn == Value.TRUE) {
        setRegister(Q_REG_IX, qrIn);
      }

      // Register file
      if (b.isFullyDefined() && data.updateClock(clk, (int)b.toLongValue(), StdAttr.TRIG_LOW) && rfEn == Value.TRUE) {
        setRegister(b, rfIn);
      }
    }
  }

  @Override
  public void propagateTtl(InstanceState state) {
    new LogicScope(state).propagate();
  }
}
