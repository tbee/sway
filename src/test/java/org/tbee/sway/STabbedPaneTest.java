package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.util.ExceptionUtil;

import javax.swing.JOptionPane;

public class STabbedPaneTest extends TestBase {

    private STextField<String> masterSTextField;

    private STextField<String> sync1Textfield;
    private STextField<String> sync1aTextfield;
    private STextField<String> async1Textfield;
    private STextField<String> sync2Textfield;
    private STextField<String> async2Textfield;

    private STabbedPane<String> sTabbedPane;
    private SFrame sFrame;

    private int tabLoadedCount = 0;

    @Test
    public void selectionModeMultiple() throws Exception {

        // GIVEN
        sFrame = construct(() -> {
            masterSTextField = STextField.ofString().value("master");
            sync1Textfield = STextField.ofString();
            sync1aTextfield = STextField.ofString();
            async1Textfield = STextField.ofString();
            sync2Textfield = STextField.ofString();
            async2Textfield = STextField.ofString();
            sTabbedPane = STabbedPane.<String>of()
                    .name("sTabbedPane")
                    .withPropertyChangeListener(STabbedPane.LOADED_COMPONENT, evt -> tabLoadedCount++)
                    .bindTo(masterSTextField.value$())
                    .addTab("sync1", SHPanel.of(sync1Textfield), (v, c) -> sync1Textfield.setValue("sync1 " + v))
                    .addTab("sync1a", SHPanel.of(sync1aTextfield), (v, c) -> sync1aTextfield.setValue("sync1a " + v))
                    .addTab("async1", SHPanel.of(async1Textfield)
                            , value -> doSomeBackgroundStuff(value)
                            , (result, component) -> async1Textfield.setValue("async1 " + result)
                            , (throwable, component) -> showExceptionInDialog(throwable, masterSTextField))
                    .addTab("subtab", STabbedPane.<String>of()
                            .name("subTabbedPane")
                            .withPropertyChangeListener(STabbedPane.LOADED_COMPONENT, evt -> tabLoadedCount++)
                            .bindTo(masterSTextField.value$()) // or use: (v, c) -> c.setValue(v)
                            .addTab("sync2", SHPanel.of(sync2Textfield), (v, c) -> sync2Textfield.setValue("sync2 " + v))
                            .addTab("async2", SHPanel.of(async2Textfield)
                                    , value -> doSomeBackgroundStuff(value)
                                    , (result, component) -> async2Textfield.setValue("async2 " + result))
                    );
            return TestUtil.inSFrame(SVPanel.of(masterSTextField, sTabbedPane, focusMeComponent()));
        });

        // GIVEN first tab is shown per default, it is already lazy loaded. The rest is not.
        Assertions.assertEquals("sync1 master", sync1Textfield.getValue());
        Assertions.assertNull(sync1aTextfield.getValue());
        Assertions.assertNull(async1Textfield.getValue());
        // AND the first tab of the sub tabbed pane is shown per default as well, as far as that tabbed pane is concerned
        Assertions.assertEquals("sync2 master", sync2Textfield.getValue());
        Assertions.assertNull(async2Textfield.getValue());

        // WHEN clicking the second sync tab
        tabLoadedCount = 0;
        frameFixture.tabbedPane("sTabbedPane").selectTab("sync1a");
        // THEN second tab is lazy loaded
        waitFor(() -> tabLoadedCount > 0);
        Assertions.assertEquals("sync1a master", sync1aTextfield.getValue());

        // WHEN clicking the async tab
        tabLoadedCount = 0;
        frameFixture.tabbedPane("sTabbedPane").selectTab("async1");
        // THEN there should be an overlay
        Assertions.assertEquals(1, sFrame.getOverlayPane().getComponentCount());
        // AND third tab is lazy loaded
        waitFor(() -> tabLoadedCount > 0);
        Assertions.assertEquals("async1 #master", async1Textfield.getValue());

        // WHEN clicking the subtab
        frameFixture.tabbedPane("sTabbedPane").selectTab("subtab");
        // THEN noting of interest happens

        // WHEN clicking the async tab in the subtab
        tabLoadedCount = 0;
        frameFixture.tabbedPane("subTabbedPane").selectTab("async2");
        // THEN there should be an overlay
        Assertions.assertEquals(1, sFrame.getOverlayPane().getComponentCount());
        // AND third tab is lazy loaded
        waitFor(() -> tabLoadedCount > 0);
        Assertions.assertEquals("async2 #master", async2Textfield.getValue());
    }

    private String doSomeBackgroundStuff(String value) {
        if (value.contains("exc")) throw new RuntimeException("oops");
        sleep(1000);
        return "#" + value;
    }

    private void showExceptionInDialog(Throwable throwable, STextField<String> masterSTextField) {
        JOptionPane.showMessageDialog(masterSTextField, ExceptionUtil.determineMessage(throwable), "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
