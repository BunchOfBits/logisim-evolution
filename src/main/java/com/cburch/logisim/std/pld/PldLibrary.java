/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.pld;

import static com.cburch.logisim.std.Strings.S;

import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import java.util.Arrays;
import java.util.List;

/**
 * PLD Library.
 */
public class PldLibrary extends Library {
  /**
   * Unique identifier of the library, used as reference in project files. Do NOT change as it will
   * prevent project files from loading.
   *
   * <p>Identifier value MUST be unique string among all libraries.
   */
  public static final String _ID = "PldLibrary";

  private List<Tool> tools = null;

  /**
   * Initializes a new instance of PldLibrary.
   */
  public PldLibrary() {
    tools =
        Arrays.asList(
            new Tool[]{
                new AddTool(Gal22V10Factory.FACTORY)
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
