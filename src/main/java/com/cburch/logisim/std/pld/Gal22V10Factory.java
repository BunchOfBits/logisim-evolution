/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.pld;

import static com.cburch.logisim.std.Strings.S;

import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.hdlgenerator.HdlGeneratorFactory;
import com.cburch.logisim.gui.icons.ArithmeticIcon;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

class Gal22V10Factory extends InstanceFactory {
  private static class Factory {
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

  private static class Gal22V10Model {
    private final Value[] registers;

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

    public Gal22V10Model() {
      registers = new Value[10];
      Arrays.fill(registers, Value.FALSE);
    }

    public void propagate(InstanceState state) {
      Value[] inputs = state.getPortValue(0).getAll();

      for (byte i = 0; i < inputs.length / 2; i++) { // reverse array
        Value temp = inputs[i];

        inputs[i] = inputs[inputs.length - i - 1];
        inputs[inputs.length - i - 1] = temp;
      }
    }
  }

  private static class Gal22V10View implements InstanceData {
    private final Gal22V10Model galModel;

    public Gal22V10View(Gal22V10Model galModel) {
      this.galModel = galModel;
    }

    public static void paintInstance(InstancePainter painter) {
      final var g = painter.getGraphics();
      final var bds = painter.getBounds();
      final var x = bds.getX();
      final var y = bds.getY();
      final var w = bds.getWidth();
      final var h = bds.getHeight();

      g.setColor(new Color(AppPreferences.COMPONENT_COLOR.get()));

      for (var pin = 0; pin < Factory.NUM_PINS; pin++) {
        var point = Factory.getPoint(pin);
        var dx = (int) point.getX();
        var dy = (int) point.getY();
        var isInput = Factory.getPinType(pin).equals(Port.INPUT);
        var alignment = isInput ? GraphicsUtil.H_LEFT : GraphicsUtil.H_RIGHT;
        var label = Factory.getLabel(pin);
        var labelX = x + dx + (isInput ? 4 : -4);
        var labelY = y + dy + 10;

        GraphicsUtil.drawText(g, label, labelX, labelY, alignment, GraphicsUtil.V_CENTER_OVERALL);
      }

      GraphicsUtil.switchToWidth(g, 2);
      g.drawRect(x, y, w, h);
      drawLabel(painter, x + w / 2, y - 12);

      for (var i = 0; i < Factory.NUM_PINS; i++) {
        painter.drawPort(i);
      }
    }

    private static void drawLabel(InstancePainter painter, int x, int y) {
      final var g = painter.getGraphics();
      final var label = painter.getAttributeValue(StdAttr.LABEL);
      final var name = S.getter("GAL22V10Component").toString();

      g.setFont(painter.getAttributeValue(StdAttr.LABEL_FONT));

      if (label == null || label.isEmpty()) {
        GraphicsUtil.drawCenteredText(g, name, x, y);
      }
    }

    @Override
    public Gal22V10View clone() {
      try {
        return (Gal22V10View) super.clone();
      } catch (CloneNotSupportedException e) {
        return null;
      }
    }
  }

  private static class FuseMapAttribute extends Attribute<FuseMap> {
    public FuseMapAttribute() {
      super("map", S.getter("fuseMap"));
    }

    @Override
    public String toDisplayString(FuseMap value) {
      return S.get("pldClickToEdit");
    }

    @Override
    public String toStandardString(FuseMap fuseMap) {
      return fuseMap.toStandardString();
    }

    @Override
    public FuseMap parse(String str) {
      return FuseMap.parse(str);
    }
  }

  private static class GalAttributes extends AbstractAttributeSet {
    public static final Attribute<FuseMap> ATTR_FUSEMAP = new FuseMapAttribute();
    private String label = "";
    private Object labelLoc = Direction.NORTH;
    private Font labelFont = StdAttr.DEFAULT_LABEL_FONT;
    private FuseMap fuseMap = new FuseMap();
    private static final List<Attribute<?>> ATTRIBUTES =
        Arrays.asList(
            ATTR_FUSEMAP,
            StdAttr.LABEL,
            StdAttr.LABEL_LOC,
            StdAttr.LABEL_FONT);

    @Override
    protected void copyInto(AbstractAttributeSet destObj) {
      GalAttributes dest = (GalAttributes) destObj;

      dest.fuseMap = new FuseMap();  // TODO: add copy ctor to copy fuse map ?
      dest.label = this.label;
      dest.labelLoc = this.labelLoc;
      dest.labelFont = this.labelFont;
    }

    @Override
    public List<Attribute<?>> getAttributes() {
      return ATTRIBUTES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(Attribute<V> attr) {
      if (attr == ATTR_FUSEMAP) {
        return (V) fuseMap;
      } else if (attr == StdAttr.LABEL) {
        return (V) label;
      } else if (attr == StdAttr.LABEL_LOC) {
        return (V) labelLoc;
      } else if (attr == StdAttr.LABEL_FONT) {
        return (V) labelFont;
      }

      return null;
    }

    @Override
    public <V> void setValue(Attribute<V> attr, V value) {
      if (attr == StdAttr.LABEL) {
        label = (String) value;
      } else if (attr == StdAttr.LABEL_LOC) {
        labelLoc = value;
      } else if (attr == StdAttr.LABEL_FONT) {
        labelFont = (Font) value;
      } else if (attr == ATTR_FUSEMAP) {
        fuseMap = (FuseMap) value;
      }

      fireAttributeValueChanged(attr, value, null);
    }
  }

  /**
   * Unique identifier of the tool, used as reference in project files. Do NOT change as it will
   * prevent project files from loading.
   *
   * <p>Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "GAL22V10";
  public static final InstanceFactory FACTORY = new Gal22V10Factory();

  static final Attribute<FuseMap> ATTR_FUSE_MAP = new FuseMapAttribute();

  public Gal22V10Factory() {
    super(_ID, (HdlGeneratorFactory) null);  // TODO: Add HDL generator and global clock

    setIcon(new ArithmeticIcon("PLD", 3));
    setOffsetBounds(Bounds.create(0, 0, 100, 240));
  }

  @Override
  public AttributeSet createAttributeSet() {
    return new GalAttributes();
  }

  @Override
  protected void configureNewInstance(Instance instance) {
    super.configureNewInstance(instance);
    instance.addAttributeListener();
    updatePorts(instance);
  }

  @Override
  protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
    if (attr == StdAttr.LABEL || attr == StdAttr.LABEL_LOC) {
      instance.recomputeBounds();
      instance.computeLabelTextField(Instance.AVOID_LEFT | Instance.AVOID_RIGHT);
      updatePorts(instance);
    } else if (attr == ATTR_FUSE_MAP) {
      instance.fireInvalidated();
    }
  }

  @Override
  public void propagate(InstanceState state) {
    Gal22V10View galView = (Gal22V10View) state.getData();

    if (galView == null) {
      galView = new Gal22V10View(new Gal22V10Model());

      state.setData(galView);
    }

    galView.galModel.propagate(state);
  }

  @Override
  public void paintInstance(InstancePainter painter) {
    Gal22V10View.paintInstance(painter);
  }

  private void updatePorts(Instance instance) {
    var ports = Gal22V10Model.getPorts();

    instance.setPorts(ports);
  }
}
