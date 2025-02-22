/*
 * This file is part of logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with logisim-evolution. If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Marcin Orlowski (http://MarcinOrlowski.com), 2021.
 */

package com.cburch.logisim.std.ttl;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;

/**
 * TTL 74x76 dual JK flip-flops with preset and clear
 * Model based on <a href="https://www.ti.com/lit/ds/symlink/sn5476.pdf">7476 datasheet</a>.
 */
public class Ttl7476 extends AbstractTtlGate {
  /**
   * Unique identifier of the tool, used as reference in project files.
   * Do NOT change as it will prevent project files from loading.
   * Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "7476";

  public static final int DELAY = 1;

  // IC pin indices as specified in the datasheet

  // Inputs
  public static final byte[] CLK = new byte[] {1, 6};
  public static final byte[] nPRE = new byte[] {2, 7};
  public static final byte[] nCLR = new byte[] {3, 8};
  public static final byte[] J = new byte[] {4, 9};
  public static final byte[] K = new byte[] {16, 12};

  // Outputs
  public static final byte[] Q = new byte[] {15, 11};
  public static final byte[] nQ = new byte[] {14, 10};

  // Power supply
  public static final byte GND = 13;
  public static final byte VCC = 5;

  private InstanceState _state;

  public Ttl7476() {
    super(
        _ID,
        (byte) 16,
        new byte[]{
            Q[0], nQ[0], Q[1], nQ[1]
        },
        new String[]{
            "CLK1", "nPRE1", "nCLR1", "J1", "CLK2", "nPRE2", "nCLR2",
            "J2", "nQ2", "Q2", "K2", "nQ1", "Q1", "K1"
        },
        null);
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
    Drawgates.paintPortNames(painter, x, y, height, portNames);
  }

  /**
   * IC pin indices are datasheet based (1-indexed), but ports are 0-indexed
   *
   * @param dsPinNr datasheet pin number
   * @return port number
   */
  protected byte pinNrToPortNr(byte dsPinNr) {
    return (byte) ((dsPinNr <= GND) ? dsPinNr - 1 : dsPinNr - 2);
  }

  /**
   * Gets the current state of the specified pin
   *
   * @param dsPinNr datasheet pin number
   * @return the current value of the specified port
   */
  private Value getPort(byte dsPinNr) {
    return _state.getPortValue(pinNrToPortNr(dsPinNr));
  }

  /**
   * Sets the specified pin to the specified level
   *
   * @param dsPinNr datasheet pin number
   * @param v       the value for the pin
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
      data = new TtlRegisterData(BitWidth.ONE, 2);
      _state.setData(data);
    }

    return data;
  }

  /**
   * Predicate which is true when the clock was triggered
   *
   * @return true when the clock was triggered
   */
  private boolean isTriggered(int ff) {
    return getData().updateClock(getPort(CLK[ff]), StdAttr.TRIG_RISING);
  }

  /**
   * Update the state of a flip-flop
   *
   * @param ff the index number for the flip-flop
   */
  private void propagateFlipFlop(int ff) {
    var next = getData().getValue(ff);

    if (getPort(nPRE[ff]) == Value.FALSE) {
      next = Value.TRUE;
    } else if (getPort(nCLR[ff]) == Value.TRUE) {
      next = Value.FALSE;
    } else if (isTriggered(ff)) {
      var j = getPort(J[ff]) == Value.TRUE;
      var k = getPort(K[ff]) == Value.TRUE;
      var q = getData().getValue(ff) == Value.TRUE;

      next = (j && !q) || (!k && q) ? Value.TRUE : Value.FALSE;
    }

    getData().setValue(ff, next);
  }

  /**
   * Update the outputs of a flip-flop
   *
   * @param ff the index number for the flip-flop
   */
  private void propagateOutputs(int ff) {
    if (getPort(nPRE[ff]) == Value.FALSE && getPort(nCLR[ff]) == Value.FALSE) {
      setPort(Q[ff], Value.TRUE);
      setPort(nQ[ff], Value.TRUE);
    } else {
      var q = getData().getValue(ff);

      setPort(Q[ff], q);
      setPort(nQ[ff], q.not());
    }
  }

  @Override
  public void propagateTtl(InstanceState state) {
    _state = state;

    for (var ff = 0; ff < Q.length; ff++) {
      propagateFlipFlop(ff);
      propagateOutputs(ff);
    }
  }
}
