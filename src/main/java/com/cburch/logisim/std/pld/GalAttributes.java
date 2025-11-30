package com.cburch.logisim.std.pld;

import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.instance.StdAttr;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

class GalAttributes extends AbstractAttributeSet {
  public static final Attribute<Gal22V10FuseMap> ATTR_FUSEMAP = new FuseMapAttribute();
  private String label = "";
  private Object labelLoc = Direction.NORTH;
  private Font labelFont = StdAttr.DEFAULT_LABEL_FONT;
  private Gal22V10FuseMap fuseMap = new Gal22V10FuseMap();

  private static final java.util.List<Attribute<?>> attributes =
      Arrays.asList(
          ATTR_FUSEMAP,
          StdAttr.LABEL,
          StdAttr.LABEL_LOC,
          StdAttr.LABEL_FONT);

  @Override
  protected void copyInto(AbstractAttributeSet destObj) {
    GalAttributes dest = (GalAttributes) destObj;

    dest.fuseMap = (Gal22V10FuseMap) fuseMap.clone();
    dest.label = this.label;
    dest.labelLoc = this.labelLoc;
    dest.labelFont = this.labelFont;
  }

  @Override
  public List<Attribute<?>> getAttributes() {
    return attributes;
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
      fuseMap = (Gal22V10FuseMap) value;
    }

    fireAttributeValueChanged(attr, value, null);
  }
}
