/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.pld;

import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import java.util.Arrays;
import java.util.List;

import static com.cburch.logisim.std.Strings.S;

public class PldLibrary extends Library {
  public static final String _ID = "PldLibrary";

  private List<Tool> tools = null;

  public PldLibrary()
  {
    tools =
        Arrays.asList(
            new Tool[]{
                new AddTool(Gal22V10.FACTORY)
            });
  }

  @Override
  public String getDisplayName() {
    return S.get("PldLibrary");
  }

  @Override
  public List<Tool> getTools() {
    return tools;
  }
}
