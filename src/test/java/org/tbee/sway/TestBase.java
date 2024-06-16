package org.tbee.sway;

import org.assertj.swing.core.ComponentLookupScope;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.testing.AssertJSwingTestCaseTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

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
        robot().settings().componentLookupScope(ComponentLookupScope.ALL); // also find hidden components
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



    protected SFrame construct(Callable<SFrame> callable) {
        SFrame frame = GuiActionRunner.execute(() -> callable.call());
        frameFixture = new FrameFixture(robot(), frame);
        frameFixture.show();
        return frame;
    }

    protected SButton focusMeComponent() {
        return SButton.of("focus me").name(FOCUS_ME);
    }
    protected void moveFocus() {
        frameFixture.button(FOCUS_ME).click();
    }

    protected void sleep(int ms) {
        TestUtil.sleep(ms);
    }


    protected void waitFor(Supplier<Boolean> supplier) {
        int retryCount = 100;
        while (!supplier.get() && retryCount-- >= 0) {
            sleep(100);
        }
        if (retryCount < 0) {
            throw new IllegalStateException("waitFor timed out");
        }
    }

    protected void withShiftPressed(Runnable runnable) {
        try {
            frameFixture.pressKey(KeyEvent.VK_SHIFT);
            runnable.run();
        }
        finally {
            frameFixture.releaseKey(KeyEvent.VK_SHIFT);
        }
    }

    protected void withControlPressed(Runnable runnable) {
        try {
            frameFixture.pressKey(KeyEvent.VK_CONTROL);
            runnable.run();
        }
        finally {
            frameFixture.releaseKey(KeyEvent.VK_CONTROL);
        }
    }
}
