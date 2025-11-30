package com.cburch.logisim.std.pld;

import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;

import static com.cburch.logisim.std.Strings.S;

public class Gal22V10View implements InstanceData {
  private final Gal22V10Model galModel;

  public Gal22V10View(Gal22V10Model galModel) {
    this.galModel = galModel;
  }

  public Gal22V10Model getGalModel() {
    return galModel;
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
