/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.pld;

import com.cburch.logisim.data.*;
import com.cburch.logisim.gui.icons.ArithmeticIcon;
import com.cburch.logisim.instance.*;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static com.cburch.logisim.std.Strings.S;

class Gal22V10 extends InstanceFactory {
  /**
   * Unique identifier of the tool, used as reference in project files. Do NOT change as it will
   * prevent project files from loading.
   *
   * <p>Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "GAL22V10";

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

  static final int[] inputs = new int[] {
      IN0_CLK, IN1, IN2, IN3, IN4, IN5, IN6, IN7, IN8, IN9, IN10, IN11
  };

  static final int[] inout = new int[] {
      IN_OUT0, IN_OUT1, IN_OUT2, IN_OUT3, IN_OUT4, IN_OUT5, IN_OUT6, IN_OUT7,  IN_OUT8, IN_OUT9
  };

  static final Attribute<FuseMap> ATTR_FUSE_MAP = new FuseMapAttribute();

  public static final InstanceFactory FACTORY = new Gal22V10();

  public Gal22V10() {
    super(_ID, S.getter("GAL22V10Component"));  // TODO: Add HDL generator and global clock
    setIcon(new ArithmeticIcon("PLD", 3));
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

    Bounds bds = instance.getBounds();

    instance.setTextField(
        StdAttr.LABEL,
        StdAttr.LABEL_FONT,
        bds.getX() + bds.getWidth() / 2,
        bds.getY() + bds.getHeight() / 3,
        GraphicsUtil.H_CENTER,
        GraphicsUtil.V_CENTER_OVERALL);
  }

  private void updatePorts(Instance instance) {
    Port[] ports = new Port[22];

    for (var i = 0; i < inputs.length; i++) {
      ports[i] = new Port(0, i * 50, Port.INPUT, BitWidth.ONE);
      ports[i].setToolTip(S.getter("input"));
    }

    for (var i = 0; i < inout.length; i++) {
      ports[i] = new Port(50, i * 50, Port.INOUT, BitWidth.ONE);
      ports[i].setToolTip(S.getter("inout"));
    }

    instance.setPorts(ports);
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
    FuseMap data = getFuseMap(state);
    Value[] inputs = state.getPortValue(0).getAll();

    for (byte i = 0; i < inputs.length / 2; i++) { // reverse array
      Value temp = inputs[i];

      inputs[i] = inputs[inputs.length - i - 1];
      inputs[inputs.length - i - 1] = temp;
    }

    // data.setInputsValue(inputs);
    // state.setPort(1, Value.create(data.getOutputValues()), Mem.DELAY);
  }

  @Override
  public void paintInstance(InstancePainter painter) {
    Graphics g = painter.getGraphics();

    g.setColor(new Color(AppPreferences.COMPONENT_COLOR.get()));
    painter.drawRoundBounds(Color.WHITE);

    Bounds bds = painter.getBounds();

    g.setFont(new Font("sans serif", Font.BOLD, 11));

    Object label = painter.getAttributeValue(StdAttr.LABEL);

    if (label == null || label.equals("")) {
      GraphicsUtil.drawCenteredText(
          g, "GAL22V10", bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 3);
    }

    painter.drawPort(0);
    painter.drawPort(1);
    painter.drawLabel();
  }

  private static FuseMap getFuseMap(InstanceState state) {
    FuseMap fuseMap = (FuseMap) state.getData();

    if (fuseMap == null) {
      fuseMap = new FuseMap();
      // if new, fill the content with the saved data
      state.setData(fuseMap);
    }
    return fuseMap;
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

      dest.fuseMap = new FuseMap();  // TODO add copy ctor to copy fuse map ?
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
}
