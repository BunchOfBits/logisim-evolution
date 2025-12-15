package com.cburch.logisim.std.pld;

import com.cburch.logisim.util.JInputDialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FuseMapCellEditor extends JDialog implements JInputDialog {
  static private String mruPath;
  private final JFileChooser fileChooser;
  private Gal22V10FuseMap fuseMap;

  public FuseMapCellEditor(Frame parent) {
    super(parent, true);

    fileChooser = new JFileChooser(mruPath);
    fileChooser.setFileFilter(new FileNameExtensionFilter("JEDEC files", "jed"));
    fileChooser.addActionListener(e -> {
      if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
        var file = fileChooser.getSelectedFile();

        mruPath = file.getParentFile().getAbsolutePath();
        fuseMap.copyFrom(Gal22V10FuseMap.parse(file));
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
    if (!(value instanceof Gal22V10FuseMap other)) { return; }

    fuseMap = other;
  }
}
