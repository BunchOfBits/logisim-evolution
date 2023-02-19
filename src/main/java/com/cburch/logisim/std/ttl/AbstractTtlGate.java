/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.ttl;

import static com.cburch.logisim.std.Strings.S;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.designrulecheck.CorrectLabel;
import com.cburch.logisim.fpga.hdlgenerator.HdlGeneratorFactory;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public abstract class AbstractTtlGate extends InstanceFactory {
  protected static final int PIN_WIDTH = 10;
  protected static final int PIN_HEIGHT = 7;
  private int height = 60;
  protected final byte nrOfPins;
  private final String name;
  private byte numberOfGatesToDraw = 0;
  protected String[] portNames = null;
  private final HashSet<Byte> outputPorts = new HashSet<>();
  private final HashSet<Byte> unusedPins = new HashSet<>();
  private final HashSet<Byte> powerPins = new HashSet<>();
  private final HashSet<Byte> groundPins = new HashSet<>();

  /**
   * @param name = name to display in the center of the TTl
   * @param pins = the total number of pins (GND and VCC included)
   * @param outputPorts = an array with the indexes of the output ports (indexes are the same you
   *     can find on Google searching the TTL you want to add)
   * @param generator = the HDL generator.
   * @param powerPins = an array with the indexes op the power pins (indexes are similar to outputPorts)
   */
  protected AbstractTtlGate(String name, byte pins, byte[] outputPorts, byte[] powerPins, byte[] groundPins, HdlGeneratorFactory generator) {
    super(name, generator);
    setIconName("ttl.gif");
    setAttributes(
        new Attribute[] {
          StdAttr.FACING, TtlLibrary.VCC_GND, TtlLibrary.DRAW_INTERNAL_STRUCTURE, StdAttr.LABEL
        },
        new Object[] {Direction.EAST, false, false, ""});
    setFacingAttribute(StdAttr.FACING);
    this.name = name;
    this.nrOfPins = pins;

    for (byte outputPort : outputPorts) {
      this.outputPorts.add(outputPort);
    }

    for (var pin : powerPins) {
      this.powerPins.add(pin);
    }

    for (var pin : groundPins) {
      this.groundPins.add(pin);
    }
  }

  /**
   * @param name = name to display in the center of the TTl
   * @param pins = the total number of pins (GND and VCC included)
   * @param outputPorts = an array with the indexes of the output ports (indexes are the same you
   *     can find on Google searching the TTL you want to add)
   */
  protected AbstractTtlGate(String name, byte pins, byte[] outputPorts, HdlGeneratorFactory generator) {
    // The bottom-right and top-left pins of TTL chips are usually their power pins.
    this(name, pins, outputPorts, new byte[] { pins }, new byte[] { (byte) (pins / 2) }, generator);
  }

  protected AbstractTtlGate(
      String name,
      byte pins,
      byte[] outputPorts,
      byte[] notUsedPins,
      HdlGeneratorFactory generator) {
    this(name, pins, outputPorts, generator);
    if (notUsedPins == null) return;
    for (byte notUsedPin : notUsedPins) unusedPins.add(notUsedPin);
  }

  /**
   * @param name = name to display in the center of the TTl
   * @param pins = the total number of pins (GND and VCC included)
   * @param outputPorts = an array with the indexes of the output ports (indexes are the same you
   *     can find on Google searching the TTL you want to add)
   * @param drawgates = if true, it calls the paintInternal method many times as the number of
   *     output ports passing the coordinates
   */
  protected AbstractTtlGate(
      String name,
      byte pins,
      byte[] outputPorts,
      boolean drawgates,
      HdlGeneratorFactory generator) {
    this(name, pins, outputPorts, generator);
    this.numberOfGatesToDraw = (byte) (drawgates ? outputPorts.length : 0);
  }

  /**
   * @param name = name to display in the center of the TTl
   * @param pins = the total number of pins (GND and VCC included)
   * @param outputPorts = an array with the indexes of the output ports (indexes are the same you
   *     can find on Google searching the TTL you want to add)
   * @param ttlPortNames = an array of strings which will be tooltips of the corresponding port in
   *     the order you pass
   */
  protected AbstractTtlGate(
      String name,
      byte pins,
      byte[] outputPorts,
      String[] ttlPortNames,
      HdlGeneratorFactory generator) {
    // the ttl name, the total number of pins and an array with the indexes of
    // output ports (indexes are the one you can find on Google), an array of
    // strings which will be tooltips of the corresponding port in order
    this(name, pins, outputPorts, generator);
    this.portNames = ttlPortNames;
  }

  protected AbstractTtlGate(
      String name,
      byte pins,
      byte[] outputPorts,
      byte[] notUsedPins,
      String[] ttlPortNames,
      HdlGeneratorFactory generator) {
    this(name, pins, outputPorts, generator);
    portNames = ttlPortNames;
    if (notUsedPins == null) return;
    for (final var notUsedPin : notUsedPins) unusedPins.add(notUsedPin);
  }

  protected AbstractTtlGate(
      String name,
      byte pins,
      byte[] outputPorts,
      String[] ttlPortNames,
      int height,
      HdlGeneratorFactory generator) {
    // the ttl name, the total number of pins and an array with the indexes of
    // output ports (indexes are the one you can find on Google), an array of
    // strings which will be tooltips of the corresponding port in order
    this(name, pins, outputPorts, generator);
    this.height = height;
    this.portNames = ttlPortNames;
  }

  protected AbstractTtlGate(
          String name,
          byte pins,
          byte[] outputPorts,
          byte[] powerPins,
          byte[] groundPins,
          String[] ttlPortNames,
          int height,
          HdlGeneratorFactory generator) {
    // the ttl name, the total number of pins and an array with the indexes of
    // output ports (indexes are the one you can find on Google), an array of
    // strings which will be tooltips of the corresponding port in order
    this(name, pins, outputPorts, powerPins, groundPins, generator);
    this.height = height;
    this.portNames = ttlPortNames;
  }

  private void computeTextField(Instance instance) {
    final var bds = instance.getBounds();
    final var dir = instance.getAttributeValue(StdAttr.FACING);
    if (dir == Direction.EAST || dir == Direction.WEST)
      instance.setTextField(
          StdAttr.LABEL,
          StdAttr.LABEL_FONT,
          bds.getX() + bds.getWidth() + 3,
          bds.getY() + bds.getHeight() / 2,
          GraphicsUtil.H_LEFT,
          GraphicsUtil.V_CENTER_OVERALL);
    else
      instance.setTextField(
          StdAttr.LABEL,
          StdAttr.LABEL_FONT,
          bds.getX() + bds.getWidth() / 2,
          bds.getY() - 3,
          GraphicsUtil.H_CENTER,
          GraphicsUtil.V_CENTER_OVERALL);
  }

  @Override
  protected void configureNewInstance(Instance instance) {
    instance.addAttributeListener();
    updatePorts(instance);
    computeTextField(instance);
  }

  @Override
  public Bounds getOffsetBounds(AttributeSet attrs) {
    final var dir = attrs.getValue(StdAttr.FACING);
    return Bounds.create(0, -30, this.nrOfPins * 10, height).rotate(Direction.EAST, dir, 0, 0);
  }

  @Override
  protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
    if (attr == StdAttr.FACING) {
      instance.recomputeBounds();
      updatePorts(instance);
      computeTextField(instance);
    } else if (attr == TtlLibrary.VCC_GND) {
      updatePorts(instance);
    }
  }

  static Point getTranslatedTtlXY(InstanceState state, MouseEvent e) {
    var x = 0;
    var y = 0;
    final var loc = state.getInstance().getLocation();
    final var height = state.getInstance().getBounds().getHeight();
    final var width = state.getInstance().getBounds().getWidth();
    final var dir = state.getAttributeValue(StdAttr.FACING);
    if (dir.equals(Direction.EAST)) {
      x = e.getX() - loc.getX();
      y = e.getY() + 30 - loc.getY();
    } else if (dir.equals(Direction.WEST)) {
      x = loc.getX() - e.getX();
      y = height - (e.getY() + (height - 30) - loc.getY());
    } else if (dir.equals(Direction.NORTH)) {
      x = loc.getY() - e.getY();
      y = width - (loc.getX() + (width - 30) - e.getX());
    } else {
      x = e.getY() - loc.getY();
      y = (loc.getX() + 30 - e.getX());
    }
    return new Point(x, y);
  }

  protected void paintBase(InstancePainter painter, boolean drawname, boolean ghost) {
    final var dir = painter.getAttributeValue(StdAttr.FACING);
    final var g = (Graphics2D) painter.getGraphics();
    final var bds = painter.getBounds();
    final var x = bds.getX();
    final var y = bds.getY();
    final var horizontal = (dir == Direction.WEST || dir == Direction.EAST);

    var xp = x;
    var yp = y;
    var width = bds.getWidth();
    var height = bds.getHeight();
    if (!ghost) {
      g.setColor(new Color(AppPreferences.COMPONENT_COLOR.get()));
    }

    // Draw pins
    for (byte i = 0; i < this.nrOfPins; i++) {
      if (i < this.nrOfPins / 2) {
        if (horizontal) {
          xp = i * 20 + (10 - PIN_WIDTH / 2) + x;
        } else {
          yp = i * 20 + (10 - PIN_WIDTH / 2) + y;
        }
      } else {
        if (horizontal) {
          xp = (i - this.nrOfPins / 2) * 20 + (10 - PIN_WIDTH / 2) + x;
          yp = height + y - PIN_HEIGHT;
        } else {
          yp = (i - this.nrOfPins / 2) * 20 + (10 - PIN_WIDTH / 2) + y;
          xp = width + x - PIN_HEIGHT;
        }
      }
      if (horizontal) {
        g.drawRect(xp, yp, PIN_WIDTH, PIN_HEIGHT);
      } else {
        g.drawRect(xp, yp, PIN_HEIGHT, PIN_WIDTH);
      }
    }

    // Draw body
    if (dir == Direction.SOUTH) {
      g.drawRoundRect(x + PIN_HEIGHT, y, bds.getWidth() - PIN_HEIGHT * 2, bds.getHeight(), 10, 10);
      g.drawArc(x + width / 2 - 7, y - 7, 14, 14, 180, 180);
    } else if (dir == Direction.WEST) {
      g.drawRoundRect(x, y + PIN_HEIGHT, bds.getWidth(), bds.getHeight() - PIN_HEIGHT * 2, 10, 10);
      g.drawArc(x + width - 7, y + height / 2 - 7, 14, 14, 90, 180);
    } else if (dir == Direction.NORTH) {
      g.drawRoundRect(x + PIN_HEIGHT, y, bds.getWidth() - PIN_HEIGHT * 2, bds.getHeight(), 10, 10);
      g.drawArc(x + width / 2 - 7, y + height - 7, 14, 14, 0, 180);
    } else { // east
      g.drawRoundRect(x, y + PIN_HEIGHT, bds.getWidth(), bds.getHeight() - PIN_HEIGHT * 2, 10, 10);
      g.drawArc(x - 7, y + height / 2 - 7, 14, 14, 270, 180);
    }

    // Draw label
    g.rotate(Math.toRadians(-dir.toDegrees()), x + width / 2, y + height / 2);
    if (drawname) {
      g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
      GraphicsUtil.drawCenteredText(
          g, this.name, x + bds.getWidth() / 2, y + bds.getHeight() / 2 - 4);
    }

    // Draw power labels
    if (horizontal) {
      xp = x;
      yp = y;
    } else {
      xp = x + (width - height) / 2;
      yp = y + (height - width) / 2;
      width = bds.getHeight();
      height = bds.getWidth();
    }
    g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 7));
    GraphicsUtil.drawCenteredText(g, "Vcc", xp + 10, yp + PIN_HEIGHT + 4);
    GraphicsUtil.drawCenteredText(g, "GND", xp + width - 10, yp + height - PIN_HEIGHT - 7);
  }

  @Override
  public void paintGhost(InstancePainter painter) {
    paintBase(painter, true, true);
  }

  @Override
  public void paintInstance(InstancePainter painter) {
    painter.drawPorts();
    painter.drawLabel();

    if (painter.getAttributeValue(TtlLibrary.DRAW_INTERNAL_STRUCTURE)) {
      paintInternalBase(painter);
    } else {
      final var g = (Graphics2D) painter.getGraphics();
      final var dir = painter.getAttributeValue(StdAttr.FACING);
      final var bds = painter.getBounds();
      final var x = bds.getX();
      final var y = bds.getY();
      final var width = bds.getWidth();
      final var height = bds.getHeight();
      final var horizontal = (dir == Direction.WEST || dir == Direction.EAST);

      var xp = x;
      var yp = y;

      // Draw body
      g.setColor(Color.DARK_GRAY.darker());

      if (horizontal) {
        g.fillRoundRect(xp, yp + PIN_HEIGHT, width, height - PIN_HEIGHT * 2 + 2, 10, 10);
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(xp, yp + PIN_HEIGHT, width, height - PIN_HEIGHT * 2 - 2, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(xp, yp + PIN_HEIGHT, width, height - PIN_HEIGHT * 2 - 2, 10, 10);
        g.drawRoundRect(xp, yp + PIN_HEIGHT, width, height - PIN_HEIGHT * 2 + 2, 10, 10);
      } else {
        g.fillRoundRect(xp + PIN_HEIGHT, yp, width - PIN_HEIGHT * 2, height, 10, 10);
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(xp + PIN_HEIGHT, yp, width - PIN_HEIGHT * 2, height - 4, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(xp + PIN_HEIGHT, yp, width - PIN_HEIGHT * 2, height - 4, 10, 10);
        g.drawRoundRect(xp + PIN_HEIGHT, yp, width - PIN_HEIGHT * 2, height, 10, 10);
      }

      // Draw pin 1 marker
      if (dir == Direction.SOUTH) {
        g.fillArc(xp + width / 2 - 7, yp - 7, 14, 14, 180, 180);
      } else if (dir == Direction.WEST) {
        g.fillArc(xp + width - 7, yp + height / 2 - 7, 14, 14, 90, 180);
      } else if (dir == Direction.NORTH) {
        g.fillArc(xp + width / 2 - 7, yp + height - 11, 14, 14, 0, 180);
      } else { // East
        g.fillArc(xp - 7, yp + height / 2 - 7, 14, 14, 270, 180);
      }

      // Draw pins
      for (byte i = 0; i < this.nrOfPins; i++) {
        if (i < this.nrOfPins / 2) {
          if (horizontal) {
            xp = i * 20 + (10 - PIN_WIDTH / 2) + x;
          } else {
            yp = i * 20 + (10 - PIN_WIDTH / 2) + y;
          }
        } else {
          if (horizontal) {
            xp = (i - this.nrOfPins / 2) * 20 + (10 - PIN_WIDTH / 2) + x;
            yp = height + y - PIN_HEIGHT;
          } else {
            yp = (i - this.nrOfPins / 2) * 20 + (10 - PIN_WIDTH / 2) + y;
            xp = width + x - PIN_HEIGHT;
          }
        }

        // Draw pin
        g.setColor(Color.LIGHT_GRAY);

        if (horizontal) {
          g.fillRect(xp, yp, PIN_WIDTH, PIN_HEIGHT);
          g.setColor(Color.BLACK);
          g.drawRect(xp, yp, PIN_WIDTH, PIN_HEIGHT);
        } else {
          g.fillRect(xp, yp, PIN_HEIGHT, PIN_WIDTH);
          g.setColor(Color.BLACK);
          g.drawRect(xp, yp, PIN_HEIGHT, PIN_WIDTH);
        }
      }

      // Draw name
      g.setColor(Color.LIGHT_GRAY.brighter());
      g.rotate(Math.toRadians(-dir.toDegrees()), x + width / 2, y + height / 2);
      g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
      GraphicsUtil.drawCenteredText(g, this.name, x + width / 2, y + height / 2 - 4);
      g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 7));

      // Draw power pins
      if (horizontal) {
        xp = x;
        yp = y;
      } else {
        xp = x + (width - height) / 2;
        yp = y + (height - width) / 2;
      }

      if (dir == Direction.SOUTH) {
        GraphicsUtil.drawCenteredText(g, "Vcc", xp + 10, yp + PIN_HEIGHT + 4);
        GraphicsUtil.drawCenteredText(g, "GND", xp + height - 14, yp + width - PIN_HEIGHT - 8);
      } else if (dir == Direction.WEST) {
        GraphicsUtil.drawCenteredText(g, "Vcc", xp + 10, yp + PIN_HEIGHT + 6);
        GraphicsUtil.drawCenteredText(g, "GND", xp + width - 10, yp + height - PIN_HEIGHT - 8);
      } else if (dir == Direction.NORTH) {
        GraphicsUtil.drawCenteredText(g, "Vcc", xp + 14, yp + PIN_HEIGHT + 4);
        GraphicsUtil.drawCenteredText(g, "GND", xp + height - 10, yp + width - PIN_HEIGHT - 8);
      } else { // east
        GraphicsUtil.drawCenteredText(g, "Vcc", xp + 10, yp + PIN_HEIGHT + 4);
        GraphicsUtil.drawCenteredText(g, "GND", xp + width - 10, yp + height - PIN_HEIGHT - 10);
      }
    }
  }

  /**
   * @param painter = the instance painter you have to use to create Graphics (Graphics g =
   *     painter.getGraphics())
   * @param x = if drawgates is false or not used, the component's left side; if drawgates is true
   *     it gets the component's width, subtracts 20 (for GND or Vcc) and divides for the number of
   *     outputs for each side, you'll get the x coordinate of the leftmost input -10 before the
   *     last output
   * @param y = the component's upper side
   * @param height = the component's height
   * @param up = true if drawgates is true when drawing the gates in the upper side (introduced this
   *     because can't draw upside down so you have to write what to draw if down and up)
   */
  public abstract void paintInternal(InstancePainter painter, int x, int y, int height, boolean up);

  private void paintInternalBase(InstancePainter painter) {
    final var dir = painter.getAttributeValue(StdAttr.FACING);
    final var bds = painter.getBounds();
    final var vertical = (dir == Direction.SOUTH || dir == Direction.NORTH);

    var x = bds.getX();
    var y = bds.getY();
    var width = bds.getWidth();
    var height = bds.getHeight();

    if (vertical) {
      x += (width - height) / 2;
      y += (height - width) / 2;
      width = bds.getHeight();
      height = bds.getWidth();
    }

    if (this.numberOfGatesToDraw == 0) {
      paintInternal(painter, x, y, height, false);
    } else {
      paintBase(painter, false, false);
      for (byte i = 0; i < this.numberOfGatesToDraw; i++) {
        paintInternal(
            painter,
            x
                + (i < this.numberOfGatesToDraw / 2 ? i : i - this.numberOfGatesToDraw / 2)
                    * ((width - 20) / (this.numberOfGatesToDraw / 2))
                + (i < this.numberOfGatesToDraw / 2 ? 0 : 20),
            y,
            height,
            i >= this.numberOfGatesToDraw / 2);
      }
    }
  }

  /** Here you have to write the logic of your component */
  @Override
  public void propagate(InstanceState state) {
    final var NrOfUnusedPins = unusedPins.size();
    if (state.getAttributeValue(TtlLibrary.VCC_GND)
        && (state.getPortValue(this.nrOfPins - 2 - NrOfUnusedPins) != Value.FALSE
            || state.getPortValue(this.nrOfPins - 1 - NrOfUnusedPins) != Value.TRUE)) {
      var port = 0;
      for (byte i = 1; i <= nrOfPins; i++) {
        if (!unusedPins.contains(i) && (i != (nrOfPins / 2))) {
          if (outputPorts.contains(i)) state.setPort(port, Value.UNKNOWN, 1);
          port++;
        }
      }
    } else propagateTtl(state);
  }

  public abstract void propagateTtl(InstanceState state);

  private void updatePorts(Instance instance) {
    final var bds = instance.getBounds();
    final var width = bds.getWidth();
    final var height = bds.getHeight();
    final var dir = instance.getAttributeValue(StdAttr.FACING);
    final var enableVccGndPorts = instance.getAttributeValue(TtlLibrary.VCC_GND);

    final var ioPorts = new ArrayList<Port>();
    final var powerPorts = new ArrayList<Port>();
    final var groundPorts = new ArrayList<Port>();

    final var names = new LinkedList<String>();

    if (portNames != null) {
      names.addAll(Arrays.asList(portNames));
    }

    for (byte portNr = 0; portNr < this.nrOfPins; portNr++) {
      final var pinNr = (byte) (portNr + 1);

      final var isUnused = unusedPins.contains(pinNr);
      final var isPower = powerPins.contains(pinNr);
      final var isGround = groundPins.contains(pinNr);
      final var isOutput = outputPorts.contains(pinNr);
      final var isInput = !isUnused && !isPower && !isGround && !isOutput;

      final var isLowerRow = (portNr < this.nrOfPins / 2);
      final var isUpperRow = !isLowerRow;

      var dx = 0;
      var dy = 0;

      // set the position
      if (isLowerRow) {
        if (dir == Direction.EAST) {
          dx = portNr * 20 + 10;
          dy = height - 30;
        } else if (dir == Direction.WEST) {
          dx = -10 - 20 * portNr;
          dy = 30 - height;
        } else if (dir == Direction.NORTH) {
          dx = width - 30;
          dy = -10 - 20 * portNr;
        } else { // SOUTH
          dx = 30 - width;
          dy = portNr * 20 + 10;
        }
      }

      if (isUpperRow) {
        if (dir == Direction.EAST) {
          dx = width - (portNr - this.nrOfPins / 2) * 20 - 10;
          dy = -30;
        } else if (dir == Direction.WEST) {
          dx = -width + (portNr - this.nrOfPins / 2) * 20 + 10;
          dy = 30;
        } else if (dir == Direction.NORTH) {
          dx = -30;
          dy = -height + (portNr - this.nrOfPins / 2) * 20 + 10;
        } else { // SOUTH
          dx = 30;
          dy = height - (portNr - this.nrOfPins / 2) * 20 - 10;
        }
      }

      if (isPower) {
        if (enableVccGndPorts) {
          var port = new Port(dx, dy, Port.INPUT, 1);

          port.setToolTip(S.getter("VCCPin", Integer.toString(pinNr)));
          powerPorts.add(port);
        }
      }

      if (isGround) {
        if (enableVccGndPorts) {
          var port = new Port(dx, dy, Port.INPUT, 1);

          port.setToolTip(S.getter("GNDPin", Integer.toString(pinNr)));
          groundPorts.add(port);
        }
      }

      if (isOutput) {
        var port = new Port(dx, dy, Port.OUTPUT, 1);

        if (names.size() == 0) {
          port.setToolTip(S.getter("demultiplexerOutTip", ": " + pinNr));
        } else {
          port.setToolTip(S.getter("demultiplexerOutTip", pinNr + ": " + names.removeFirst()));
        }

        ioPorts.add(port);
      }

      if (isInput) {
        var port = new Port(dx, dy, Port.INPUT, 1);

        if (names.size() == 0) {
          port.setToolTip(S.getter("multiplexerInTip", ": " + pinNr));
        } else {
          port.setToolTip(S.getter("multiplexerInTip", pinNr + ": " + names.removeFirst()));
        }

        ioPorts.add(port);
      }
    }

    /*
     * array port is composed in this order:
     * - lower ports excluding power/ground pins and unused pins
     * - upper ports excluding power/ground pins and unused pins
     * - power pins
     * - ground pins
     */
    final var ports = new ArrayList<Port>();

    ports.addAll(ioPorts);
    ports.addAll(powerPorts);
    ports.addAll(groundPorts);

    instance.setPorts(ports.toArray(new Port[0]));
  }

  @Override
  public final void paintIcon(InstancePainter painter) {
    final var g = (Graphics2D) painter.getGraphics().create();
    g.setColor(Color.DARK_GRAY.brighter());
    GraphicsUtil.switchToWidth(g, AppPreferences.getScaled(1));
    g.fillRoundRect(
        AppPreferences.getScaled(4),
        0,
        AppPreferences.getScaled(8),
        AppPreferences.getScaled(16),
        AppPreferences.getScaled(3),
        AppPreferences.getScaled(3));
    g.setColor(Color.black);
    g.drawRoundRect(
        AppPreferences.getScaled(4),
        0,
        AppPreferences.getScaled(8),
        AppPreferences.getScaled(16),
        AppPreferences.getScaled(3),
        AppPreferences.getScaled(3));
    final var wh1 = AppPreferences.getScaled(3);
    final var wh2 = AppPreferences.getScaled(2);
    for (int y = 0; y < 3; y++) {
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(wh2, AppPreferences.getScaled(y * 5 + 1), wh1, wh1);
      g.fillRect(AppPreferences.getScaled(12), AppPreferences.getScaled(y * 5 + 1), wh1, wh1);
      g.setColor(Color.BLACK);
      g.drawRect(wh2, AppPreferences.getScaled(y * 5 + 1), wh1, wh1);
      g.drawRect(AppPreferences.getScaled(12), AppPreferences.getScaled(y * 5 + 1), wh1, wh1);
    }
    g.drawRoundRect(
        AppPreferences.getScaled(6),
        0,
        AppPreferences.getScaled(6),
        AppPreferences.getScaled(16),
        AppPreferences.getScaled(3),
        AppPreferences.getScaled(3));
    g.dispose();
  }

  @Override
  public String getHDLName(AttributeSet attrs) {
    return CorrectLabel.getCorrectLabel("TTL" + getName()).toUpperCase();
  }
}
