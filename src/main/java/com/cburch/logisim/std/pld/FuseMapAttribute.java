package com.cburch.logisim.std.pld;

import com.cburch.logisim.data.Attribute;

import javax.swing.*;
import java.awt.*;

import static com.cburch.logisim.std.Strings.S;

public class FuseMapAttribute extends Attribute<FuseMap> {
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
  public Gal22V10FuseMap parse(String str) {
    return Gal22V10FuseMap.parse(str);
  }

  @Override
  public Component getCellEditor(Window source, FuseMap fuseMap) {
    var editor = new FuseMapCellEditor((Frame)source);

    editor.setValue(fuseMap);

    return editor;
  }
}
