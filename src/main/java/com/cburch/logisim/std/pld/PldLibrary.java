/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.pld;

import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import java.util.List;

import static com.cburch.logisim.std.Strings.S;

public class PldLibrary extends Library {
  public static final String _ID = "PldLibrary";

  private static final FactoryDescription[] DESCRIPTIONS = {
      new FactoryDescription(Gal22V10.class, S.getter("GAL22V10")),
  };

  private List<Tool> tools = null;

  @Override
  public String getDisplayName() {
    return S.get("PldLibrary");
  }

  @Override
  public List<Tool> getTools() {
    if (tools == null) {
      tools = FactoryDescription.getTools(com.cburch.logisim.std.pld.PldLibrary.class, DESCRIPTIONS);
    }

    return tools;
  }
}
