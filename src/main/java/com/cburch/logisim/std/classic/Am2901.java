/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.classic;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.std.ttl.AbstractTtlGate;
import com.cburch.logisim.std.ttl.TtlRegisterData;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Arrays;

/**
 * Am2901: 4-bit microprocessor slice
 * Model based on <a href="http://bitsavers.org/components/amd/bitslice/1979_AMD_2900family.pdf">Am2901 datasheet</a>.
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
  public static final byte ZERO = 11;

  public static final byte Y0 = 36;
  public static final byte Y1 = 37;
  public static final byte Y2 = 38;
  public static final byte Y3 = 39;

  // Bidirectional
  public static final byte RAM0 = 9;
  public static final byte RAM3 = 8;

  public static final byte Q0 = 21;
  public static final byte Q3 = 16;

  // Power supply
  public static final byte VCC = 10;
  public static final byte GND = 30;

  private static final Byte[] A_INPUTS = new Byte[] { A0, A1, A2, A3 };
  private static final Byte[] B_INPUTS = new Byte[] { B0, B1, B2, B3 };
  private static final Byte[] D_INPUTS = new Byte[] { D0, D1, D2, D3 };
  private static final Byte[] ALU_SRC = new Byte[] { I0, I1, I2 };
  private static final Byte[] ALU_DST = new Byte[] { I6, I7, I8 };
  private static final Byte[] ALU_FUNC = new Byte[] { I3, I4, I5 };
  private static final Byte[] Y_OUTPUTS = new Byte[] { Y0, Y1, Y2, Y3 };
  private static final Byte[] TTL_OUTPUTS = new Byte[] { Pn, Gn, C4, OVR, F3, ZERO };
  private static final Byte[] SHIFT_OUTPUTS = new Byte[] { RAM0, RAM3, Q0, Q3 };

  private static final BitWidth REGISTER_WIDTH = BitWidth.create(4);
  private static final Value ZERO_DATA = Value.createKnown(REGISTER_WIDTH, 0);
  private static final Value UNKNOWN_DATA = Value.createUnknown(REGISTER_WIDTH);
  private static final Value ERROR_DATA = Value.createError(REGISTER_WIDTH);

  private static final int NR_OF_REG = 16 + 2 + 1;
  private static final int A_REG_IX = 16;
  private static final int B_REG_IX = 17;
  private static final int Q_REG_IX = 18;

  // ALU source operand microcode (see datasheet for names and meaning)
  private static final Value AQ = Value.createKnown(3, 0);
  private static final Value AB = Value.createKnown(3, 1);
  private static final Value ZQ = Value.createKnown(3, 2);
  private static final Value ZB = Value.createKnown(3, 3);
  private static final Value ZA = Value.createKnown(3, 4);
  private static final Value DA = Value.createKnown(3, 5);
  private static final Value DQ = Value.createKnown(3, 6);
  private static final Value DZ = Value.createKnown(3, 7);

  // ALU function microcode (see datasheet for names and meaning)
  private static final Value ADD = Value.createKnown(3, 0);
  private static final Value SUBR = Value.createKnown(3, 1);
  private static final Value SUBS = Value.createKnown(3, 2);
  private static final Value OR = Value.createKnown(3, 3);
  private static final Value AND = Value.createKnown(3, 4);
  private static final Value NOTRS = Value.createKnown(3, 5);
  private static final Value EXOR = Value.createKnown(3, 6);
  private static final Value EXNOR = Value.createKnown(3, 7);

  // ALU destination microcode (see datasheet for names and meaning)
  private static final Value QREG = Value.createKnown(3, 0);
  private static final Value NOP = Value.createKnown(3, 1);
  private static final Value RAMA = Value.createKnown(3, 2);
  private static final Value RAMF = Value.createKnown(3, 3);
  private static final Value RAMQD = Value.createKnown(3, 4);
  private static final Value RAMD = Value.createKnown(3, 5);
  private static final Value RAMQU = Value.createKnown(3, 6);
  private static final Value RAMU = Value.createKnown(3, 7);

  private InstanceState _state;
  private TtlRegisterData _data;

  private static class AluResult {
    public Value f;         // ALU output
    public Value pn;        // Pn
    public Value gn;        // Gn
    public Value carryOut;  // Cn+4
    public Value overflow;  // OVR
    public Value zero;      // F = 0

    private AluResult(Value f, Value pn, Value gn, Value carryOut, Value overflow, Value zero) {
      this.f = f;
      this.pn = pn;
      this.gn = gn;
      this.carryOut = carryOut;
      this.overflow = overflow;
      this.zero = zero;
    }

    public static final AluResult ERROR = new AluResult(ERROR_DATA, Value.ERROR, Value.ERROR, Value.ERROR, Value.ERROR, Value.ERROR);

    public static final AluResult UNKNOWN = new AluResult(UNKNOWN_DATA, Value.UNKNOWN, Value.UNKNOWN, Value.UNKNOWN, Value.UNKNOWN, Value.UNKNOWN);
  }

  public Am2901() {
    super(
            _ID,
            (byte) 40,
            new byte[] { Pn, Gn, C4, OVR, F3, ZERO, Y0, Y1, Y2, Y3, RAM0, RAM3, Q0, Q3 },
            new byte[] { VCC },
            new byte[] { GND },
            new String[] {
              "A3", "A2", "A1", "A0", "I6", "I8", "I7", "RAM3", "RAM0",
              "F=0", "I0", "I1", "I2", "CLK", "Q3", "B0", "B1", "B2", "B3",
              "Q0", "D3", "D2", "D1", "D0", "I3", "I5", "I4", "C0",
              "F3", "Gn", "C4", "OVR", "Pn", "Y0", "Y1", "Y2", "Y3", "OEn"
            },
            120,
            null);
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
  }

  /** IC pin indices are datasheet based (1-indexed), but ports are 0-indexed
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

  /** Gets the current state of the specified pin
   *
   * @param dsPinNr datasheet pin number
   * @return the current state of the specified pin
   */
  private Value getPort(byte dsPinNr) {
    return _state.getPortValue(pinNrToPortNr(dsPinNr));
  }

  /** Sets the specified pin to the specified value
   *
   * @param dsPinNr datasheet pin number
   * @param v the value for the pin
   */
  private void setPort(byte dsPinNr, Value v) {
    _state.setPort(pinNrToPortNr(dsPinNr), v, DELAY);
  }

  /**
   * Gets the instance data
   *
   * @return the instance data
   */
  private TtlRegisterData getData() {
    var data = (TtlRegisterData) _state.getData();

    if (data == null) {
      data = new TtlRegisterData(REGISTER_WIDTH, NR_OF_REG);
      _state.setData(data);
    }

    return data;
  }

  /**
   * Gets a Value from the current state of a set of pins
   *
   * @param pins the input pins, must be less than 32 pins
   * @return the combined binary value
   */
  private Value getBitFieldValue(Byte[] pins) {
    return Value.create((Value[]) Arrays.stream(pins)
          .map(this::getPort)
          .toArray());
  }

  /**
   * Sets the pins to the Value.
   *
   * @param pins the set of output pins.
   * @param v the value to set.
   */
  private void setBitFieldValue(Byte[] pins, Value v) {
    for (var pin : pins) {
      setPort(pin, v.get(pin));
    }
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
   * Gets the value of a register from the register file
   *
   * @param i the index into the register file
   * @return the value of the register
   */
  private Value getRegister(int i) {
    return _data.getValue(i);
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
    _data.setValue(i, v);
  }

  /**
   * Set the ALU flags for the arithmetic microcodes: "ADD", "SUB" and "SUBS".
   *
   * @param result the ALU result.
   * @param p the "propagate" output from the ALU.
   * @param g the "generate" output from the ALU.
   */
  private void setArithmeticFlags(AluResult result, Value p, Value g) {
    final var c0 = getPort(C0); // Already validated
    final var c1 = g.get(0).or(p.get(0).and(c0)); // C1 = G0 + P0C0
    final var c2 = g.get(1).or(p.get(1).and(c1)); // C2 = G1 + P1C1
    final var c3 = g.get(2).or(p.get(2).and(c2)); // C3 = G2 + P2C2
    final var c4 = g.get(3).or(p.get(3).and(c3)); // C4 = G3 + P3C3

    // Z = /(F3 + F2 + F1 + F0)
    result.zero = result.f.get(3)
            .or(result.f.get(2))
            .or(result.f.get(1))
            .or(result.f.get(0))
            .not();
    // Pn = /(P3P2P1P0)
    result.pn = p.get(3)
            .and(p.get(2))
            .and(p.get(1))
            .and(p.get(0))
            .not();
    // Gn = /(G3 + P3G2 + P3P2G1 + P3P2P1G0)
    //    = /(G3 + P3(G2 + P2(G1 + P1(G0))))
    result.gn = g.get(0)
            .and(p.get(1)).or(g.get(1))
            .and(p.get(2)).or(g.get(2))
            .and(p.get(3)).or(g.get(3))
            .not();
    // Cn+4 = C4
    result.carryOut = c4;
    // OVR = C4 ^ C3
    result.overflow = c4.xor(c3);
  }

  /**
   * Set the ALU flags for the logical-or microcode: "OR".
   *
   * @param result the ALU result.
   * @param p the "propagate" output from the ALU.
   * @param g the "generate" output from the ALU.
   */
  private void setOrFlags(AluResult result, Value p, Value g) {
    final var c0 = getPort(C0); // Already validated

    // Z = /(F3 + F2 + F1 + F0)
    result.zero = result.f.get(3)
            .or(result.f.get(2))
            .or(result.f.get(1))
            .or(result.f.get(0))
            .not();
    // Pn = 0
    result.pn = Value.FALSE;
    // Gn = P3P2P1P0
    result.gn = p.get(3)
            .and(p.get(2))
            .and(p.get(1))
            .and(p.get(0));
    // Cn+4 = /(P3P2P1P0) + C0
    //      = /Gn + C0
    result.carryOut = result.gn.not().or(c0);
    // OVR = /(P3P2P1P0) + C0
    //     = Cn+4
    result.overflow = result.carryOut;
  }

  /**
   * Set the ALU flags for the logical-and microcode: "AND" and "NOTRS".
   *
   * @param result the ALU result.
   * @param p the "propagate" output from the ALU.
   * @param g the "generate" output from the ALU.
   */
  private void setAndFlags(AluResult result, Value p, Value g) {
    final var c0 = getPort(C0); // Already validated

    // Z = /(F3 + F2 + F1 + F0)
    result.zero = result.f.get(3)
            .or(result.f.get(2))
            .or(result.f.get(1))
            .or(result.f.get(0))
            .not();
    // Pn = 0
    result.pn = Value.FALSE;
    // Gn = /(G3 + G2 + G1 + G0)
    result.gn = g.get(3)
            .or(g.get(2))
            .or(g.get(1))
            .or(g.get(0))
            .not();
    // Cn+4 = G3 + G2 + G1 + G0 + C0
    //      = /Gn + C0
    result.carryOut = result.gn.not().or(c0);
    // OVR = G3 + G2 + G1 + G0 + C0
    //     = Cn+4
    result.overflow = result.carryOut;
  }

  /**
   * Set the ALU flags for the logical-xor microcodes: "EXOR" and "EXNOR".
   *
   * @param result the ALU result.
   * @param p the "propagate" output from the ALU.
   * @param g the "generate" output from the ALU.
   */
  private void setXorFlags(AluResult result, Value p, Value g) {
    final var c0 = getPort(C0); // Already validated

    // Z = /(F3 + F2 + F1 + F0)
    result.zero = result.f.get(3)
            .or(result.f.get(2))
            .or(result.f.get(1))
            .or(result.f.get(0))
            .not();
    // Pn = G3 + G2 + G1 + G0
    result.pn = g.get(3)
            .or(g.get(2))
            .or(g.get(1))
            .or(g.get(0));
    // Gn = G3 + P3G2 + P3P2G1 + P3P2P1P0
    //    = G3 + P3(G2 + P2(G1 + P1(P0)))
    result.gn = p.get(0)
            .and(p.get(1)).or(g.get(1))
            .and(p.get(2)).or(g.get(2))
            .and(p.get(3)).or(g.get(3));
    // Cn+4 = G3 + P3G2 + P3P2G1 + P3P2P1P0(G0+/C0)
    //      = G3 + P3(G2 + P2(G1 + P1(P0(G0+/C0))))
    result.carryOut = c0.not().or(g.get(0)).and(p.get(0))
            .and(p.get(1)).or(g.get(1))
            .and(p.get(2)).or(g.get(2))
            .and(p.get(3)).or(g.get(3));
    // OVR uses inverted values for P and G
    // OVR = (P2 + G2P1 + G2G1P0 + G2G1G0C0) ^ (P3 + G3P2 + G3G2P1 + G3G2G1P0 + G3G2G1G0C0)
    // A   = (P2 + G2P1 + G2G1P0 + G2G1G0C0)
    //       (P2 + G2(P1 + G1(P0 + G0(C0))))
    // OVR = A ^ (P3 + G3A)
    final var a = c0
            .and(g.get(0).not()).or(p.get(0).not())
            .and(g.get(1).not()).or(p.get(1).not())
            .and(g.get(2).not()).or(p.get(2).not());
    result.overflow = a.xor(a.and(g.get(3).not()).or(p.get(3).not()));
  }

  /**
   * Get the R and S inputs to the ALU from the ALU mux inputs
   *
   * @return an immutable pair with "left" = R operand and "right" = S operand
   */
  private ImmutablePair<Value, Value> getAluOperands() {
    // Get all possible non-constant data inputs for the ALU input muxes: A, B, D and Q
    final var aSelect = getBitFieldValue(A_INPUTS);
    final var bSelect = getBitFieldValue(B_INPUTS);

    final var aData = getRegister(aSelect);
    final var bData = getRegister(bSelect);
    final var dData = getBitFieldValue(D_INPUTS);
    final var qData = getRegister(Q_REG_IX);

    final var aluSource = getBitFieldValue(ALU_SRC);

    // Filter ALU source ERROR values
    if (aluSource.isErrorValue()) {
      return new ImmutablePair<>(ERROR_DATA, ERROR_DATA);
    }

    // Filter ALU source UNKNOWN values
    if (!aluSource.isFullyDefined()) {
      return new ImmutablePair<>(UNKNOWN_DATA, UNKNOWN_DATA);
    }

    var operands = new ImmutablePair<>(ERROR_DATA, ERROR_DATA);

    // ALU source is fully defined now
    if (aluSource == AQ) {
      operands = new ImmutablePair<>(aData, qData);
    } else if (aluSource == AB) {
      operands = new ImmutablePair<>(aData, bData);
    } else if (aluSource == ZQ) {
      operands = new ImmutablePair<>(ZERO_DATA, qData);
    } else if (aluSource == ZB) {
      operands = new ImmutablePair<>(ZERO_DATA, bData);
    } else if (aluSource == ZA) {
      operands = new ImmutablePair<>(ZERO_DATA, aData);
    } else if (aluSource == DA) {
      operands = new ImmutablePair<>(dData, aData);
    } else if (aluSource == DQ) {
      operands = new ImmutablePair<>(dData, qData);
    } else if (aluSource == DZ) {
      operands = new ImmutablePair<>(dData, ZERO_DATA);
    }

    return operands;
  }

  /**
   * Get the ALU result according to the ALU function microcode.
   *
   * @param operands the R and S ALU operands
   * @return the ALU result
   */
  private AluResult getAluResult(ImmutablePair<Value, Value> operands) {
    final var r = operands.getLeft();
    final var s = operands.getRight();
    final var c0 = getPort(C0);
    final var aluFunction = getBitFieldValue(ALU_FUNC);

    // Filter R, S, C0 and ALU function ERROR values
    if (r.isErrorValue() || s.isErrorValue() || c0.isErrorValue() || aluFunction.isErrorValue()) {
      return AluResult.ERROR;
    }

    // Filter R, S, C0 and ALU function UNKNOWN values
    if (!r.isFullyDefined() || !s.isFullyDefined() || !c0.isFullyDefined() || !aluFunction.isFullyDefined()) {
      return AluResult.UNKNOWN;
    }

    var result = AluResult.UNKNOWN;

    // Calculate F (the ALU result) and the result flags
    if (aluFunction == ADD) {
      result.f = Value.createKnown(REGISTER_WIDTH, r.toLongValue() + s.toLongValue() + c0.toLongValue());
      setArithmeticFlags(result, r.or(s), r.and(s));
    } else if (aluFunction == SUBR) {
      result.f = Value.createKnown(REGISTER_WIDTH, s.toLongValue() - r.toLongValue() - c0.not().toLongValue());
      setArithmeticFlags(result, r.not().or(s), r.not().and(s));
    } else if (aluFunction == SUBS) {
      result.f = Value.createKnown(REGISTER_WIDTH, r.toLongValue() - s.toLongValue() - c0.not().toLongValue());
      setArithmeticFlags(result, r.or(s.not()), r.and(s.not()));
    } else if (aluFunction == OR) {
      result.f = r.or(s);
      setOrFlags(result, r.or(s), r.and(s));
    } else if (aluFunction == AND) {
      result.f = r.and(s);
      setAndFlags(result, r.or(s), r.and(s));
    } else if (aluFunction == NOTRS) {
      result.f = r.not().and(s);
      setAndFlags(result, r.not().or(s), r.not().and(s));
    } else if (aluFunction == EXOR) {
      result.f = r.xor(s);
      setXorFlags(result, r.not().or(s), r.not().and(s));
    } else if (aluFunction == EXNOR) {
      result.f = r.xor(s).not();
      setXorFlags(result, r.not().or(s), r.not().and(s));
    }

    return result;
  }

  /**
   * Process the clock inputs to the various registers.
   * A and B latches are triggered when the clock is high.
   * Q is triggered at the rising edge.
   * The register file is triggered when the clock is low.
   *
   * @param f the ALU output.
   */
  private void propagateRegisters(Value f) {
    final var aSelect = getBitFieldValue(A_INPUTS);
    final var bSelect = getBitFieldValue(B_INPUTS);

    // A latch
    if (_data.updateClock(getPort(CLK), A_REG_IX, StdAttr.TRIG_HIGH)) {
      setRegister(A_REG_IX, getRegister(aSelect));
    }

    // B latch
    if (_data.updateClock(getPort(CLK), B_REG_IX, StdAttr.TRIG_HIGH)) {
      setRegister(B_REG_IX, getRegister(bSelect));
    }

    final var aluDest = getBitFieldValue(ALU_DST);

    // Filter ALU destination ERROR and UNKNOWN values
    if (!aluDest.isFullyDefined()) {
      return;
    }

    // Q register
    if (_data.updateClock(getPort(CLK), Q_REG_IX, StdAttr.TRIG_RISING)) {
      if (aluDest == QREG) {
        setRegister(Q_REG_IX, f);
      } else if (aluDest == RAMQD) {
        setRegister(Q_REG_IX, getRegister(Q_REG_IX).shr(getPort(Q3)));
      } else if (aluDest == RAMQU) {
        setRegister(Q_REG_IX, getRegister(Q_REG_IX).shl(getPort(Q0)));
      }
    }

    // Register file
    if (_data.updateClock(getPort(CLK), (int) bSelect.toLongValue(), StdAttr.TRIG_LOW)) {
      if (aluDest == RAMA || aluDest == RAMF) {
        setRegister(bSelect, f);
      } else if (aluDest == RAMQD || aluDest == RAMD) {
        setRegister(bSelect, f.shr(getPort(RAM3)));
      } else if (aluDest == RAMQU || aluDest == RAMU) {
        setRegister(bSelect, f.shl(getPort(RAM0)));
      }
    }
  }

  /**
   * Set the bidirectional lines to high-Z according
   * to the ALU destination microcode.
   */
  private void propagateBufferControl() {
    final var aluDest = getBitFieldValue(ALU_DST);

    if (aluDest.isFullyDefined()) {
      if (aluDest != RAMQD && aluDest != RAMD) {
        setPort(Q0, Value.UNKNOWN);
        setPort(RAM0, Value.UNKNOWN);
      }

      if (aluDest != RAMQU && aluDest != RAMU) {
        setPort(Q3, Value.UNKNOWN);
        setPort(RAM3, Value.UNKNOWN);
      }
    }
  }

  private void propagateTtlOutputs(AluResult aluResult) {
    final var aluSrc = getBitFieldValue(ALU_SRC);
    final var aluFunc = getBitFieldValue(ALU_FUNC);

    if (aluSrc.isErrorValue() || aluFunc.isErrorValue()) {
      setBitFieldValue(TTL_OUTPUTS, Value.createError(BitWidth.create(TTL_OUTPUTS.length)));

      return;
    }

    if (!aluSrc.isFullyDefined() || !aluFunc.isFullyDefined()) {
      setBitFieldValue(TTL_OUTPUTS, Value.createUnknown(BitWidth.create(TTL_OUTPUTS.length)));

      return;
    }

    setPort(Pn, aluResult.pn);
    setPort(Gn, aluResult.gn);
    setPort(C4, aluResult.carryOut);
    setPort(OVR, aluResult.overflow);
    setPort(F3, aluResult.f.get(3));
    setPort(ZERO, aluResult.zero);
  }

  private void propagateShiftOutputs(AluResult aluResult) {
    final var aluDest = getBitFieldValue(ALU_DST);

    if (aluDest.isErrorValue()) {
      setBitFieldValue(SHIFT_OUTPUTS, Value.createError(BitWidth.create(SHIFT_OUTPUTS.length)));

      return;
    }

    if (!aluDest.isFullyDefined()) {
      setBitFieldValue(SHIFT_OUTPUTS, Value.createUnknown(BitWidth.create(SHIFT_OUTPUTS.length)));

      return;
    }

    if (aluDest == RAMQD || aluDest == RAMD) {
      setPort(Q0, getRegister(Q_REG_IX).get(0));
      setPort(RAM0, aluResult.f.get(0));
    }

    if (aluDest == RAMQU || aluDest == RAMU) {
      setPort(Q3, getRegister(Q_REG_IX).get(3));
      setPort(RAM3, aluResult.f.get(3));
    }
  }

  private void propagateYOutputs(AluResult aluResult) {
    final var oen = getPort(OEn);
    final var aluDest = getBitFieldValue(ALU_DST);

    if (oen.isErrorValue() || aluDest.isErrorValue()) {
      setBitFieldValue(Y_OUTPUTS, ERROR_DATA);

      return;
    }

    if (!oen.isFullyDefined() && !aluDest.isFullyDefined()) {
      setBitFieldValue(Y_OUTPUTS, UNKNOWN_DATA);

      return;
    }

    if (oen == Value.FALSE) {
      if (aluDest == RAMA) {
        setBitFieldValue(Y_OUTPUTS, getRegister(A_REG_IX));
      } else {
        setBitFieldValue(Y_OUTPUTS, aluResult.f);
      }
    }
  }

  private void propagateOutputs(AluResult aluResult) {
    propagateTtlOutputs(aluResult);
    propagateShiftOutputs(aluResult);
    propagateYOutputs(aluResult);
  }

  @Override
  public void propagateTtl(InstanceState state) {
    _state = state;
    _data = getData();

    propagateBufferControl();

    final var operands = getAluOperands();
    final var aluResult = getAluResult(operands);

    propagateRegisters(aluResult.f);
    propagateOutputs(aluResult);
  }
}
