package com.cburch.logisim.std.pld;

import com.cburch.logisim.util.JInputDialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FuseMapCellEditor extends JDialog implements JInputDialog {
  private final JFileChooser fileChooser;
  private Gal22V10FuseMap fuseMap;

  public FuseMapCellEditor(Frame parent) {
    super(parent, true);

    fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("JEDEC files", "jed"));
    fileChooser.addActionListener(e -> {
      if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
        fuseMap.copyFrom(Gal22V10FuseMap.parse(fileChooser.getSelectedFile()));
      }

      // Close the dialog and allow the table to update
      setVisible(false);
      dispose();
    });

    add(fileChooser);
    pack();
  }

  @Override
  public Object getValue() {
    return fuseMap;
  }

  @Override
  public void setValue(Object value) {
    if (!(value instanceof Gal22V10FuseMap fuseMap)) { return; }

    this.fuseMap = fuseMap;
  }
}
