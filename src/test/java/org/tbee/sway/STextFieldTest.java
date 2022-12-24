package org.tbee.sway;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.testing.AssertJSwingTestCaseTemplate;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

public class STextFieldTest extends AssertJSwingTestCaseTemplate {

    // https://stackoverflow.com/questions/46676726/how-to-get-started-testing-java-swing-gui-with-assertj-swing

    static private final String FOCUS_ME = "focusMe";

    private FrameFixture frameFixture;
    private STextField sTextField;

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

    @Test
    public void integerHappyTest() throws Exception {

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals(123, sTextField.getValue());
    }

    @Test
    public void integerFormatErrorTest() throws Exception {

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("For input string: \"abc\"");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("abc", sTextField.getText());
        Assertions.assertEquals(null, sTextField.getValue());
    }


    @Test
    public void stringBindHappyTest() throws Exception {

        // GIVEN
        final Bean1 bean1 = new Bean1();
        construct(() -> {
            sTextField = STextField.ofString().name("sTextField").bind(bean1, Bean1.NAME);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", bean1.getName());

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            bean1.setName("def");
        });

        // THEN
        Assertions.assertEquals("def", sTextField.getText());
        Assertions.assertEquals("def", sTextField.getValue());
    }

    @Test
    public void integerBindHappyTest() throws Exception {

        // GIVEN
        final Bean1 bean1 = new Bean1();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bind(bean1, Bean1.DISTANCE);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals(123, bean1.getDistance());

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            bean1.setDistance(456);
        });

        // THEN
        Assertions.assertEquals("456", sTextField.getText());
        Assertions.assertEquals( 456, sTextField.getValue());
    }

    @Test
    public void integerBindSetterFailureTest() throws Exception {

        // GIVEN
        final Bean1 bean1 = new Bean1();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bind(bean1, Bean1.DISTANCE);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").deleteText().enterText("-12");
        moveFocus();

        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("Age must be >= 0");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("0", sTextField.getText());
        Assertions.assertEquals(0, sTextField.getValue());
    }

    // TBEERNOT TODO BeanBinder tests


    private void construct(Callable<JFrame> callable) {
        JFrame frame = GuiActionRunner.execute(() -> {
            return callable.call();
        });
        frameFixture = new FrameFixture(robot(), frame);
        frameFixture.show();
    }

    private SButton focusMeComponent() {
        return new SButton("focus me").name(FOCUS_ME);
    }
    private void moveFocus() {
        frameFixture.button(FOCUS_ME).click();
    }
}
