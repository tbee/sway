package org.tbee.sway;

import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.junit.jupiter.api.Test;

public class SButtonTest extends TestBase {

    SButton sButton;

    @Test
    public void happyTest() throws Exception {

        // GIVEN
        construct(() -> {
        	sButton = SButton.of("test") //
                    .onAction(e -> {
						throw new RuntimeException("oops");
					}) //
                    .name("sButton");
            return TestUtil.inJFrame(sButton, focusMeComponent());
        });

        // WHEN
        frameFixture.button("sButton").click();
        
        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("oops");
        optionPaneFixture.okButton().click();
    }
}
