package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SButtonGroupTest extends TestBase {

    @Test
    public void happyJToggleButtonTest() throws Exception {

        // GIVEN
        var ref = new AtomicReference<SButtonGroup<Integer>>();
        construct(() -> {
            var sButtonGroup = SButtonGroup.<Integer>of() //
                    .add(1, SToggleButton.of("1")) //
                    .add(2, SToggleButton.of("2")) //
                    .add(3, SToggleButton.of("3"));
            sButtonGroup.getButtons().forEach(b -> b.setName("b" + b.getText())); // to make them findable
            ref.set(sButtonGroup);
            return TestUtil.inSFrame(sButtonGroup.getButtons());
        });
        SButtonGroup<Integer> sButtonGroup = ref.get();

        // THEN
        Assertions.assertEquals(null, sButtonGroup.getValue());

        // WHEN click on 2
        frameFixture.toggleButton("b2").click();
        // THEN
        Assertions.assertEquals(2, sButtonGroup.getValue());

        // WHEN click on 3
        frameFixture.toggleButton("b3").click();
        // THEN
        Assertions.assertEquals(3, sButtonGroup.getValue());

        // WHEN click on 3 again
        frameFixture.toggleButton("b3").click();
        // THEN
        Assertions.assertEquals(3, sButtonGroup.getValue());

        // WHEN change value programmatically to 1
        SwingUtilities.invokeAndWait(() -> sButtonGroup.setValue(1));
        // THEN
        Assertions.assertEquals(1, sButtonGroup.getValue());
    }

    @Test
    public void happyBindingTest() throws Exception {

        // GIVEN
        City city = City.of().distance(1);
        var ref = new AtomicReference<SButtonGroup<Integer>>();
        construct(() -> {
            var sButtonGroup = new SButtonGroup<Integer>() //
                    .add(1, SToggleButton.of("1")) //
                    .add(2, SToggleButton.of("2")) //
                    .add(3, SToggleButton.of("3")) //
                    .bindTo(city.distance$());
            sButtonGroup.getButtons().forEach(b -> b.setName("b" + b.getText())); // to make them findable
            ref.set(sButtonGroup);
            return TestUtil.inSFrame(sButtonGroup.getButtons());
        });
        SButtonGroup<Integer> sButtonGroup = ref.get();

        // THEN there was an initial sync upon binding
        Assertions.assertEquals(1, city.getDistance());

        // WHEN click on 2
        frameFixture.toggleButton("b2").click();
        // THEN
        Assertions.assertEquals(2, city.getDistance());

        // WHEN change value programmatically to 3
        SwingUtilities.invokeAndWait(() -> city.distance(3));
        // THEN
        Assertions.assertEquals(3, sButtonGroup.getValue());
    }

    @Test
    public void happyOfTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);
        var ref = new AtomicReference<SButtonGroup<City>>();
        construct(() -> {
            var sButtonGroup = SButtonGroup.of(new CityFormat(cities), () -> new SToggleButton(), berlin, bredevoort, amsterdam, rome, paris);
            sButtonGroup.getButtons().forEach(b -> b.setName(b.getText())); // to make them findable
            ref.set(sButtonGroup);
            return TestUtil.inSFrame(sButtonGroup.getButtons());
        });
        SButtonGroup<City> sButtonGroup = ref.get();

        // THEN
        Assertions.assertEquals(null, sButtonGroup.getValue());

        // WHEN click on 2
        frameFixture.toggleButton("Amsterdam").click();

        // THEN
        Assertions.assertEquals(amsterdam, sButtonGroup.getValue());
    }

    @Test
    public void happyOfClassTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);

        SFormatRegistry.register(City.class, new CityFormat(cities));
        try {
            var ref = new AtomicReference<SButtonGroup<City>>();
            construct(() -> {
                var sButtonGroup = SButtonGroup.of(City.class, () -> new SToggleButton(), berlin, bredevoort, amsterdam, rome, paris);
                sButtonGroup.getButtons().forEach(b -> b.setName(b.getText())); // to make them findable
                ref.set(sButtonGroup);
                return TestUtil.inSFrame(sButtonGroup.getButtons());
            });
            SButtonGroup<City> sButtonGroup = ref.get();

            // WHEN click on 2
            frameFixture.toggleButton("Amsterdam").click();

            // THEN
            Assertions.assertEquals(amsterdam, sButtonGroup.getValue());
        }
        finally {
            Assertions.assertTrue(SFormatRegistry.unregister(City.class));
        }
    }

    @Test
    public void happyOfListTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);

        SFormatRegistry.register(City.class, new CityFormat(cities));
        try {
            var ref = new AtomicReference<SButtonGroup<City>>();
            construct(() -> {
                var sButtonGroup = SButtonGroup.of(() -> new SToggleButton(), berlin, bredevoort, amsterdam, rome, paris);
                sButtonGroup.getButtons().forEach(b -> b.setName(b.getText())); // to make them findable
                ref.set(sButtonGroup);
                return TestUtil.inSFrame(sButtonGroup.getButtons());
            });
            SButtonGroup<City> sButtonGroup = ref.get();

            // WHEN click on 2
            frameFixture.toggleButton("Amsterdam").click();

            // THEN
            Assertions.assertEquals(amsterdam, sButtonGroup.getValue());
        }
        finally {
            Assertions.assertTrue(SFormatRegistry.unregister(City.class));
        }
    }

    @Test
    public void happyOfRadioButtonTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);

        SFormatRegistry.register(City.class, new CityFormat(cities));
        try {
            var ref = new AtomicReference<SButtonGroup<City>>();
            construct(() -> {
                var sButtonGroup = SButtonGroup.of(() -> new SRadioButton(), berlin, bredevoort, amsterdam, rome, paris);
                sButtonGroup.getButtons().forEach(b -> b.setName(b.getText())); // to make them findable
                ref.set(sButtonGroup);
                return TestUtil.inSFrame(sButtonGroup.getButtons());
            });
            SButtonGroup<City> sButtonGroup = ref.get();

            // WHEN click on 2
            frameFixture.toggleButton("Amsterdam").click();

            // THEN
            Assertions.assertEquals(amsterdam, sButtonGroup.getValue());
        }
        finally {
            Assertions.assertTrue(SFormatRegistry.unregister(City.class));
        }
    }

    @Test
    public void happyOfRadioButtonsTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);

        SFormatRegistry.register(City.class, new CityFormat(cities));
        try {
            var ref = new AtomicReference<SButtonGroup<City>>();
            construct(() -> {
                var sButtonGroup = SButtonGroup.ofRadioButtons(berlin, bredevoort, amsterdam, rome, paris);
                sButtonGroup.getButtons().forEach(b -> b.setName(b.getText())); // to make them findable
                ref.set(sButtonGroup);
                return TestUtil.inSFrame(sButtonGroup.getButtons());
            });
            SButtonGroup<City> sButtonGroup = ref.get();

            // WHEN click on 2
            frameFixture.toggleButton("Amsterdam").click();

            // THEN
            Assertions.assertEquals(amsterdam, sButtonGroup.getValue());
        }
        finally {
            Assertions.assertTrue(SFormatRegistry.unregister(City.class));
        }
    }
}
