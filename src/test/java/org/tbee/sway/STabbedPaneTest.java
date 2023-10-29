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

    private int pceCount = 0;

    @Test
    public void selectionModeMultiple() throws Exception {

        // GIVEN
        construct(() -> {
            masterSTextField = STextField.ofString().value("master");
            sync1Textfield = STextField.ofString();
            sync1aTextfield = STextField.ofString();
            async1Textfield = STextField.ofString();
            sync2Textfield = STextField.ofString();
            async2Textfield = STextField.ofString();
            sTabbedPane = STabbedPane.<String>of()
                    .name("sTabbedPane")
                    .bindTo(masterSTextField.value$())
                    .addTab("sync1", SHPanel.of(sync1Textfield), (v, c) -> sync1Textfield.setValue("sync1 " + v))
                    .addTab("sync1a", SHPanel.of(sync1aTextfield), (v, c) -> sync1aTextfield.setValue("sync1a " + v))
                    .addTab("async1", SHPanel.of(async1Textfield)
                            , value -> doSomeBackgroundStuff(value)
                            , (result, component) -> async1Textfield.setValue("async1 " + result)
                            , (throwable, component) -> showExceptionInDialog(throwable, masterSTextField))
                    .addTab("subtab", STabbedPane.<String>of()
                            .bindTo(masterSTextField.value$())
                            .addTab("sync2", SHPanel.of(sync2Textfield), (v, c) -> sync2Textfield.setValue("sync2 " + v))
                            .addTab("async2", SHPanel.of(async2Textfield)
                                    , value -> doSomeBackgroundStuff(value)
                                    , (result, component) -> async2Textfield.setValue("async2 " + result))
                    );

            sTabbedPane.addPropertyChangeListener(STabbedPane.LOADED_COMPONENT, evt -> pceCount++);
            return TestUtil.inSFrame(SVPanel.of(masterSTextField, sTabbedPane, focusMeComponent()));
        });

        // GIVEN first tab is shown, so it is lazy loaded. Second is not.
        Assertions.assertEquals("sync1 master", sync1Textfield.getValue());
        Assertions.assertEquals(null, sync1aTextfield.getValue());

        // WHEN click second tab
        pceCount = 0;
        frameFixture.tabbedPane("sTabbedPane").selectTab("sync1a");
        waitFor(() -> pceCount > 0);

        // THEN second tab is lazy loaded
        Assertions.assertEquals("sync1a master", sync1aTextfield.getValue());

        // WHEN click third tab
        pceCount = 0;
        frameFixture.tabbedPane("sTabbedPane").selectTab("async1");
        waitFor(() -> pceCount > 0);

        // THEN third tab is lazy loaded
        Assertions.assertEquals("async1 #master", async1Textfield.getValue());
//        sleep(5000);
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
