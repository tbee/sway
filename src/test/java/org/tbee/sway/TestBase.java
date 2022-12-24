package org.tbee.sway;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.testing.AssertJSwingTestCaseTemplate;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

public class TestBase extends AssertJSwingTestCaseTemplate {

    static private final String FOCUS_ME = "focusMe";

    protected FrameFixture frameFixture;

    @BeforeAll
    public static final void beforeAll() {
        // avoid UI test execution in a headless environment (e.g. when building in CI environment like Jenkins or TravisCI)
        Assertions.assertFalse(GraphicsEnvironment.isHeadless(), "Automated UI Test cannot be executed in headless environment");
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public final void beforeEach() throws Exception {
        this.setUpRobot();
    }

    @AfterEach
    public final void afterEach() throws Exception {
        frameFixture.close();
        this.cleanUp();
    }

    @AfterAll
    public static final void afterClass() {
        FailOnThreadViolationRepaintManager.uninstall();
    }



    protected void construct(Callable<JFrame> callable) {
        JFrame frame = GuiActionRunner.execute(() -> {
            return callable.call();
        });
        frameFixture = new FrameFixture(robot(), frame);
        frameFixture.show();
    }

    protected SButton focusMeComponent() {
        return new SButton("focus me").name(FOCUS_ME);
    }
    protected void moveFocus() {
        frameFixture.button(FOCUS_ME).click();
    }
}
