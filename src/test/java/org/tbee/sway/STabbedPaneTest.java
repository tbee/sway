package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.util.ExceptionUtil;

import javax.swing.JOptionPane;

public class STabbedPaneTest extends TestBase {

    STextField<String> masterSTextField;

    STextField<String> sync1Textfield;
    STextField<String> sync1aTextfield;
    STextField<String> async1Textfield;
    STextField<String> sync2Textfield;
    STextField<String> async2Textfield;

    private STabbedPane<String> sTabbedPane;

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
            return TestUtil.inSFrame(SVPanel.of(masterSTextField, sTabbedPane, focusMeComponent()));
        });

        // GIVEN first tab is shown, so it is lazy loaded. Second is not.
        Assertions.assertEquals("sync1 master", sync1Textfield.getValue());
        Assertions.assertEquals(null, sync1aTextfield.getValue());

        // WHEN click second tab
        frameFixture.tabbedPane("sTabbedPane").selectTab("sync1a");
        sleep(1000); // TBEERNOT wait for tab to be loaded, can we do this without a sleep?

        // THEN second tab is lazy loaded
        Assertions.assertEquals("sync1a master", sync1aTextfield.getValue());

        // WHEN click third tab
        frameFixture.tabbedPane("sTabbedPane").selectTab("async1");
        sleep(2000); // TBEERNOT wait for tab to be loaded, this is releated to the sleep in the background action, but still can we wait for the tab to be loaded

        // THEN third tab is lazy loaded
        Assertions.assertEquals("async1 #master", async1Textfield.getValue());
        sleep(5000);
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
