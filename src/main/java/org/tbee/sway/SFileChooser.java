package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class SFileChooser extends JFileChooser implements
        JComponentMixin<SFileChooser> {
    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SFileChooser.class);
    final static private String REMEMBER_DIR_PREFS_ID = "rememberDir";

    private final JComponent owner;

    public SFileChooser(JComponent owner) {
        this.owner = owner;
        setCurrentDirectory(new File(preferences().get(REMEMBER_DIR_PREFS_ID, ".")));
    }

    // ===========================================================================================================================
    // For Mixins

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    // ==============================================
    // LOGIC


    private Preferences preferences() {
        return Preferences.userRoot().node(this.prefs != null ? this.prefs : owner.getClass().getName());
    }

    public void showSaveDialog(Consumer<File> consumer) {
        int option = showSaveDialog(SwingUtilities.windowForComponent(owner)); // use windows so it centers on screen
        if (option == JFileChooser.APPROVE_OPTION){
            File file = getSelectedFile();
            if (file.exists()) {
                if (SConfirmDialog.of(this, "Bevestig", SLabel.of(file + " overschrijven?"))
                        .centerOnScreen()
                        .onOkJustClose()
                        .onCancelJustClose()
                        .showAndWait() != SConfirmDialog.CloseReason.OK) {
                    return;
                }
            }
            preferences().put(REMEMBER_DIR_PREFS_ID, file.getParentFile().getAbsolutePath());
            consumer.accept(file);
        }
    }

    // ==============================================
    // PROPERTIES

    /** Where to store the defaults */
    public void setPrefs(String v) {
        firePropertyChange(PREFS, this.prefs, this.prefs = v);
    }
    public String getPrefs() {
        return prefs;
    }
    private String prefs = null;
    final static public String PREFS = "prefs";
    public SFileChooser prefs(String v) {
        setPrefs(v);
        return this;
    }
    public SFileChooser prefs(Class<?> v) {
        setPrefs(v.getName());
        return this;
    }

    // ==============================================
    // FLUENT API

    public SFileChooser fileFilter(FileFilter v) {
        setFileFilter(v);
        return this;
    }

    public SFileChooser selectedFile(File v) {
        setSelectedFile(v);
        return this;
    }

    static public SFileChooser of(JComponent owner) {
        return new SFileChooser(owner);
    }
}
