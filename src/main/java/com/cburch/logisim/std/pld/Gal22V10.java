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

public class Gal22V10 extends InstanceFactory {
  public static final String _ID = "GAL22V10";

  public Gal22V10() {
    super(_ID, S.getter("GAL22V10Component"));  // TODO: Add HDL generator and global clock
    setIcon(new ArithmeticIcon("PLD", 3));
    setOffsetBounds(Bounds.create(0, -30, 60, 60));
    setInstanceLogger(Logger.class);
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
          g, "PLA ROM", bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 3);
    }

    painter.drawPort(0);
    painter.drawPort(1);
    painter.drawPort(2, S.get("ramCSLabel"), Direction.SOUTH);
    painter.drawLabel();
  }

  @Override
  public void propagate(InstanceState state) {
    GalFuseMap data = getFuseMap(state);
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
  protected void configureNewInstance(Instance instance) {
    Bounds bds = instance.getBounds();
    instance.addAttributeListener();
    updateports(instance);
    instance.setTextField(
        StdAttr.LABEL,
        StdAttr.LABEL_FONT,
        bds.getX() + bds.getWidth() / 2,
        bds.getY() + bds.getHeight() / 3,
        GraphicsUtil.H_CENTER,
        GraphicsUtil.V_CENTER_OVERALL);
  }

  private void updateports(Instance instance) {
  }

  private static GalFuseMap getFuseMap(InstanceState state) {
    GalFuseMap fuseMap = (GalFuseMap) state.getData();

    if (fuseMap == null) {
      fuseMap = new GalFuseMap();
      // if new, fill the content with the saved data
      state.setData(fuseMap);
    }
    return fuseMap;
  }

  public static class Logger extends InstanceLogger {
    @Override
    public String getLogName(InstanceState state, Object option) {
      return null;
    }

    @Override
    public BitWidth getBitWidth(InstanceState state, Object option) {
      //PlaRomData data = getPlaRomData(state);

      //return BitWidth.create(data.getOutputs());
      return BitWidth.create(1);
    }

    @Override
    public Value getLogValue(InstanceState state, Object option) {
      return state.getPortValue(1);
    }
  }

  private static class FuseMapAttribute extends Attribute<GalFuseMap> {
    public FuseMapAttribute() {
      super("table", S.getter("plaProgram"));
    }

    @Override
    public java.awt.Component getCellEditor(Window source, GalFuseMap fuseMap) {
      /*
      GalFuseMap.EditorDialog dialog = new GalFuseMap.EditorDialog((Frame) source);
      dialog.setValue(fuseMap);

      return dialog;
      */
      return null;
    }

    @Override
    public String toDisplayString(GalFuseMap value) {
      return S.get("plaClickToEdit");
    }

    @Override
    public String toStandardString(GalFuseMap fuseMap) {
      return fuseMap.toStandardString();
    }

    @Override
    public GalFuseMap parse(String str) {
      return GalFuseMap.parse(str);
    }
  }

  private static class GalAttributes extends AbstractAttributeSet {
    public static final Attribute<GalFuseMap> ATTR_FUSEMAP = new FuseMapAttribute();
    private String label = "";
    private Object labelLoc = Direction.NORTH;
    private Font labelFont = StdAttr.DEFAULT_LABEL_FONT;
    private GalFuseMap fuseMap = new GalFuseMap();
    private static final List<Attribute<?>> ATTRIBUTES =
        Arrays.asList(
            ATTR_FUSEMAP,
            StdAttr.LABEL,
            StdAttr.LABEL_LOC,
            StdAttr.LABEL_FONT);

    @Override
    protected void copyInto(AbstractAttributeSet destObj) {
      GalAttributes dest = (GalAttributes) destObj;

      dest.label = this.label;
      dest.labelLoc = this.labelLoc;
      dest.labelFont = this.labelFont;
      dest.fuseMap = new GalFuseMap();  // TODO add copy ctor to copy fuse map ?
    }

    @Override
    public List<Attribute<?>> getAttributes() {
      return ATTRIBUTES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(Attribute<V> attr) {
      if (attr == StdAttr.LABEL) {
        return (V) label;
      }

      if (attr == StdAttr.LABEL_LOC) {
        return (V) labelLoc;
      }

      if (attr == StdAttr.LABEL_FONT) {
        return (V) labelFont;
      }

      if (attr == ATTR_FUSEMAP) {
        return (V) fuseMap;
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
        fuseMap = (GalFuseMap) value;
      }

      fireAttributeValueChanged(attr, value, null);
    }
  }
}
