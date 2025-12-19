/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.pld;

import com.cburch.logisim.LogisimVersion;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.fpga.hdlgenerator.HdlGeneratorFactory;
import com.cburch.logisim.gui.icons.ArithmeticIcon;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.tools.MenuExtender;

class Gal22V10Factory extends InstanceFactory {
  /**
   * Unique identifier of the tool, used as reference in project files. Do NOT change as it will
   * prevent project files from loading.
   *
   * <p>Identifier value MUST be unique string among all tools.
   */
  public static final String _ID = "GAL22V10";
  public static final InstanceFactory FACTORY = new Gal22V10Factory();

  public Gal22V10Factory() {
    super(_ID, (HdlGeneratorFactory) null);  // TODO: Add HDL generator and global clock

    setIcon(new ArithmeticIcon("PLD", 3));
    setOffsetBounds(Bounds.create(0, 0, 100, 240));
  }

  @Override
  public AttributeSet createAttributeSet() {
    return new GalAttributes(new Gal22V10FuseMap());
  }

  @Override
  protected Object getInstanceFeature(Instance instance, Object key) {
    if (key == MenuExtender.class) {
      return new GalMenuView((Gal22V10Factory)FACTORY, instance, new GalMenuModel());
    }

    return super.getInstanceFeature(instance, key);
  }

  @Override
  protected void configureNewInstance(Instance instance) {
    super.configureNewInstance(instance);
    final var attributes = (GalAttributes) instance.getAttributeSet();

    attributes.setValue(GalAttributes.ATTR_FUSEMAP, new Gal22V10FuseMap(instance.getAttributeValue(GalAttributes.ATTR_FUSEMAP).map));
    instance.addAttributeListener();
    updatePorts(instance);
  }

  // TODO: is this necessary ? Why write the default map to file ?
  @Override
  public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {
    if (attr == GalAttributes.ATTR_FUSEMAP) {
      return null;  // TODO: remove in order to prevent writing the default map
    }

    return super.getDefaultAttributeValue(attr, ver);
  }

  @Override
  protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
    if (attr == StdAttr.LABEL || attr == StdAttr.LABEL_LOC) {
      instance.recomputeBounds();
      instance.computeLabelTextField(Instance.AVOID_LEFT | Instance.AVOID_RIGHT);
      updatePorts(instance);
    } else if (attr == GalAttributes.ATTR_FUSEMAP) {
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

    galView.getGalModel().propagate(state);
  }

  @Override
  public void paintInstance(InstancePainter painter) {
    Gal22V10View.paintInstance(painter);
  }

  private void updatePorts(Instance instance) {
    instance.setPorts(Gal22V10Model.getPorts());
  }
}
