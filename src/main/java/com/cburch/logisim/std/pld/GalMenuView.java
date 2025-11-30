package com.cburch.logisim.std.pld;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.gui.Strings;
import com.cburch.logisim.gui.generic.OptionPane;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.MenuExtender;
import com.cburch.logisim.util.JFileChoosers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static com.cburch.logisim.std.Strings.S;

public class GalMenuView implements ActionListener, MenuExtender {
  private final Gal22V10Factory factory;
  private final GalMenuModel model;
  private final Instance instance;
  private Project proj;
  private Frame frame;
  private CircuitState circState;
  private JMenuItem load;
  private File recent = null;

  GalMenuView(Gal22V10Factory factory, Instance instance, GalMenuModel model) {
    this.factory = factory;
    this.instance = instance;
    this.model = model;
  }

  private static JFileChooser createFileChooser(File lastFile) {
    final var chooser = JFileChoosers.createSelected(lastFile);

    // chooser.addChoosableFileFilter(getFilter(autoFormat));
    chooser.setAcceptAllFileFilterUsed(true);

    return chooser;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();

    if (src == load) {

      final var mem = (instance == null) ? null : (Gal22V10Factory) instance.getFactory();
      //final var recent = getRecent(proj, mem, instance);
      final var chooser = createFileChooser(recent);

      chooser.setDialogTitle(Strings.S.get("ramLoadDialogTitle"));

      final var choice = chooser.showOpenDialog(frame);

      if (choice == JFileChooser.APPROVE_OPTION) {
        final var selectedFile = chooser.getSelectedFile();

        try {
          model.load();
          //mem.setCurrentImage(instance, f);
        } catch (Exception e) {
          OptionPane.showMessageDialog(frame, e.getMessage(), Strings.S.get("ramLoadErrorTitle"), OptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  @Override
  public void configureMenu(JPopupMenu menu, Project proj) {
    this.proj = proj;
    this.frame = proj.getFrame();
    this.circState = proj.getCircuitState();

    Object attrs = instance.getAttributeSet();

    if (attrs instanceof GalAttributes) {
      //((GalAttributes) attrs).setProject(proj);
    }

    var enabled = circState != null;

    load = createItem(enabled, S.get("ramLoadMenuItem"));

    menu.addSeparator();
    menu.add(load);
  }

  private JMenuItem createItem(boolean enabled, String label) {
    final var ret = new JMenuItem(label);

    ret.setEnabled(enabled);
    ret.addActionListener(this);

    return ret;
  }

  private void doLoad() {
    //final var m = factory.getState(instance, circState).getContents();

    //HexFile.open(m, frame, proj, instance);
  }
}
