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
import com.cburch.logisim.fpga.hdlgenerator.HdlGeneratorFactory;
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

  private static class Factory {
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

    private enum PinType {
      Input,
      InOut,
      None
    }

    private static PinType getPinType(int pin) {
      if (IN0_CLK <= pin && pin <= IN11) {
        return PinType.Input;
      } else if (IN_OUT0 <= pin && pin <= IN_OUT9) {
        return PinType.InOut;
      } else {
        return PinType.None;
      }
    }

    private static Point getPoint(int pin) {
      return switch (getPinType(pin)) {
        case Input -> new Point(0, pin * 20);
        case InOut -> new Point(100, (pin - IN_OUT0) * 20);
        default -> null;
      };
    }

    public static PortModel createPortModel(int pin) {
      var pinType = getPinType(pin);
      var point = getPoint(pin);

      if (pinType == PinType.None || point == null) {
        return null;
      }

      var model = new PortModel();

      model.port = new Port(point.x, point.y, pinType == PinType.Input ? Port.INPUT : Port.INOUT, BitWidth.ONE);
      model.pin = pin;

      return model;
    }

    public static PortViewModel createPortViewModel(PortModel model) {
      var pinType = getPinType(model.pin);
      var point = getPoint(model.pin);

      if (pinType == PinType.None || point == null) {
        return null;
      }

      var viewModel = new PortViewModel();

      viewModel.model = model;
      viewModel.dx = point.x;
      viewModel.dy = point.y;

      switch (pinType) {
        case Input:
          viewModel.alignment = GraphicsUtil.H_LEFT;
          viewModel.name = String.format("I %02d", model.pin);
          break;
        case InOut:
          viewModel.alignment = GraphicsUtil.H_RIGHT;
          viewModel.name = String.format("I/O %02d", model.pin);
          break;
      }

      viewModel.portNr = model.pin;

      return viewModel;
    }
  }

  private static class PortModel {
    public Port port;
    public int pin;
  }

  private static class Gal22V10Model implements InstanceData {
    public PortModel[] portModels = new PortModel[] {
        Factory.createPortModel(Factory.IN0_CLK),
        Factory.createPortModel(Factory.IN1),
        Factory.createPortModel(Factory.IN2),
        Factory.createPortModel(Factory.IN3),
        Factory.createPortModel(Factory.IN4),
        Factory.createPortModel(Factory.IN5),
        Factory.createPortModel(Factory.IN6),
        Factory.createPortModel(Factory.IN7),
        Factory.createPortModel(Factory.IN8),
        Factory.createPortModel(Factory.IN9),
        Factory.createPortModel(Factory.IN10),
        Factory.createPortModel(Factory.IN11),

        Factory.createPortModel(Factory.IN_OUT0),
        Factory.createPortModel(Factory.IN_OUT1),
        Factory.createPortModel(Factory.IN_OUT2),
        Factory.createPortModel(Factory.IN_OUT3),
        Factory.createPortModel(Factory.IN_OUT4),
        Factory.createPortModel(Factory.IN_OUT5),
        Factory.createPortModel(Factory.IN_OUT6),
        Factory.createPortModel(Factory.IN_OUT7),
        Factory.createPortModel(Factory.IN_OUT8),
        Factory.createPortModel(Factory.IN_OUT9),
    };

    public FuseMap fuseMap;

    public void propagate(InstanceState state) {
      Value[] inputs = state.getPortValue(0).getAll();

      for (byte i = 0; i < inputs.length / 2; i++) { // reverse array
        Value temp = inputs[i];

        inputs[i] = inputs[inputs.length - i - 1];
        inputs[inputs.length - i - 1] = temp;
      }
    }

    @Override
    public Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException e) {
        return null;
      }
    }
  }

  private static class PortViewModel {
    public PortModel model;
    public int dx;
    public int dy;
    public int alignment;
    public String name;
    public int portNr;

    public void drawPort(InstancePainter painter, PortViewModel pvm) {
      final var g = painter.getGraphics();
      final var bds = painter.getBounds();
      final var x = bds.getX();
      final var y = bds.getY();
      final var left = pvm.alignment == GraphicsUtil.H_LEFT;

      GraphicsUtil.drawText(g, pvm.name, x + pvm.dx + (left ? 3 : -3), y + pvm.dy + 10, pvm.alignment, GraphicsUtil.V_CENTER_OVERALL);
    }
  }

  private static class Gal22V10ViewModel {
    private final String name = S.getter("GAL22V10Component").toString();
    private final Gal22V10Model model;

    public PortViewModel[] portViewModels;

    public Gal22V10ViewModel(Gal22V10Model model) {
      this.model = model;

      portViewModels = Arrays
        .stream(model.portModels)
        .map(Factory::createPortViewModel)
        .toArray(PortViewModel[]::new);
    }

    public void paintInstance(InstancePainter painter, boolean ghost) {
      final var g = painter.getGraphics();
      final var bds = painter.getBounds();
      final var x = bds.getX();
      final var y = bds.getY();
      final var w = bds.getWidth();
      final var h = bds.getHeight();

      if (!ghost) {
        g.setColor(new Color(AppPreferences.COMPONENT_COLOR.get()));
      }

      GraphicsUtil.switchToWidth(g, 2);
      g.drawRect(x, y, w, h);
      drawLabel(painter, x + w / 2, y - 12);

      for (PortViewModel portViewModel : portViewModels) {
        portViewModel.drawPort(painter, portViewModel);
      }

      if (ghost) {
        return;
      }

      for (PortViewModel pvm : portViewModels) {
        painter.drawPort(pvm.portNr);
      }
    }

    private void drawLabel(InstancePainter painter, int x, int y) {
      final var g = painter.getGraphics();
      final var label = painter.getAttributeValue(StdAttr.LABEL);

      g.setFont(painter.getAttributeValue(StdAttr.LABEL_FONT));

      if (label == null || label.isEmpty()) {
        GraphicsUtil.drawCenteredText(g, name, x, y);
      }
    }
  }

  private final Gal22V10ViewModel viewModel;

  static final Attribute<FuseMap> ATTR_FUSE_MAP = new FuseMapAttribute();

  public static final InstanceFactory FACTORY = new Gal22V10();

  public Gal22V10() {
    super(_ID, (HdlGeneratorFactory) null);  // TODO: Add HDL generator and global clock
    setIcon(new ArithmeticIcon("PLD", 3));
    setOffsetBounds(Bounds.create(0, -10, 100, 240));
    viewModel =  new Gal22V10ViewModel(new Gal22V10Model());
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
    Gal22V10Model model = (Gal22V10Model) state.getData();

    if (model == null) {
      model = new Gal22V10Model();
      // if new, fill the content with the saved data
      state.setData(model);
    }

    model.propagate(state);
  }

  @Override
  public void paintGhost(InstancePainter painter) {
    viewModel.paintInstance(painter, true);
  }

  @Override
  public void paintInstance(InstancePainter painter) {
    viewModel.paintInstance(painter, false);
  }

  private void updatePorts(Instance instance) {
    var ports = Arrays
      .stream(viewModel.portViewModels)
      .map(p -> p.model.port)
      .toArray(Port[]::new);

    instance.setPorts(ports);
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
