/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.classic;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import java.util.List;

import static com.cburch.logisim.std.Strings.S;

public class ClassicLibrary extends Library {
  /**
   * Unique identifier of the library, used as reference in project files. Do NOT change as it will
   * prevent project files from loading.
   *
   * <p>Identifier value MUST be unique string among all libraries.
   */
  public static final String _ID = "Classic";

  private static final FactoryDescription[] DESCRIPTIONS = {
    new FactoryDescription(Am2901.class, S.getter("Am2901"), "ttl.gif"),
  };

  static final Attribute<Boolean> VCC_GND =
          Attributes.forBoolean("VccGndPorts", S.getter("VccGndPorts"));

  private List<Tool> tools = null;

  @Override
  public String getDisplayName() {
    return S.get("classicLibrary");
  }

  @Override
  public List<? extends Tool> getTools() {
    if (tools == null) {
      tools = FactoryDescription.getTools(ClassicLibrary.class, DESCRIPTIONS);
    }
    return tools;
  }
}
