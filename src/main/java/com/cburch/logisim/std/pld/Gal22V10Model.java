package com.cburch.logisim.std.pld;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import java.util.Arrays;

public class Gal22V10Model {
  private final Value[] registers;

  /**
   * Initializes a new instance.
   */
  public Gal22V10Model() {
    registers = new Value[10];
    Arrays.fill(registers, Value.FALSE);
  }

  /**
   * Get all ports.
   * @return all ports.
   */
  public static Port[] getPorts() {
    return new Port[] {
        Factory.createPort(Factory.IN0_CLK),
        Factory.createPort(Factory.IN1),
        Factory.createPort(Factory.IN2),
        Factory.createPort(Factory.IN3),
        Factory.createPort(Factory.IN4),
        Factory.createPort(Factory.IN5),
        Factory.createPort(Factory.IN6),
        Factory.createPort(Factory.IN7),
        Factory.createPort(Factory.IN8),
        Factory.createPort(Factory.IN9),
        Factory.createPort(Factory.IN10),
        Factory.createPort(Factory.IN11),

        Factory.createPort(Factory.IN_OUT0),
        Factory.createPort(Factory.IN_OUT1),
        Factory.createPort(Factory.IN_OUT2),
        Factory.createPort(Factory.IN_OUT3),
        Factory.createPort(Factory.IN_OUT4),
        Factory.createPort(Factory.IN_OUT5),
        Factory.createPort(Factory.IN_OUT6),
        Factory.createPort(Factory.IN_OUT7),
        Factory.createPort(Factory.IN_OUT8),
        Factory.createPort(Factory.IN_OUT9),
    };
  }

  /**
   * Handle changes in the circuit and propagate the result to the outputs.
   * @param state the instance state.
   */
  public void propagate(InstanceState state) {
    var inputs = state.getPortValue(0).getAll();
    var map = state.getAttributeValue(GalAttributes.ATTR_FUSEMAP);

    for (byte i = 0; i < inputs.length / 2; i++) { // reverse array
      Value temp = inputs[i];

      inputs[i] = inputs[inputs.length - i - 1];
      inputs[inputs.length - i - 1] = temp;
    }
  }
}
