package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

public class SDatePickerTest extends TestBase {

    @Test
    public void singleTest() {

        var ref = new AtomicReference<SLocalDatePicker>();
        construct(() -> {
            var sList = new SLocalDatePicker() //
                    .name("sLocalDatePicker")
                    .displayedLocalDate(LocalDate.of(2024, 6, 1));
            ref.set(sList);
            return TestUtil.inSFrame(sList);
        });
        var sLocalDatePicker = ref.get();

        // WHEN click on date -> select
        frameFixture.toggleButton("2024-06-15").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getValue());

        // WHEN click on same date -> clear
        frameFixture.toggleButton("2024-06-15").click();
        // THEN
        Assertions.assertEquals(0, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(null, sLocalDatePicker.getValue());

        // WHEN click on date -> select
        frameFixture.toggleButton("2024-06-15").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getValue());

        // WHEN click on other date -> select
        frameFixture.toggleButton("2024-06-20").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 20), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 20), sLocalDatePicker.getValue());
    }

    @Test
    public void rangeTest() {

        var ref = new AtomicReference<SLocalDatePicker>();
        construct(() -> {
            var sList = new SLocalDatePicker() //
                    .name("sLocalDatePicker")
                    .mode(SLocalDatePicker.Mode.RANGE)
                    .displayedLocalDate(LocalDate.of(2024, 6, 1));
            ref.set(sList);
            return TestUtil.inSFrame(sList);
        });
        var sLocalDatePicker = ref.get();

        // WHEN click on single date -> select
        frameFixture.toggleButton("2024-06-15").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getValue());

        // WHEN shift click on other date -> select range
        withShiftPressed(() -> {
            frameFixture.toggleButton("2024-06-17").click();
        });
        // THEN
        Assertions.assertEquals(3, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 16), sLocalDatePicker.getSelection().get(1));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getSelection().get(2));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getValue());

        // WHEN shift click on another data -> extend range
        withShiftPressed(() -> {
            frameFixture.toggleButton("2024-06-14").click();
        });
        // THEN
        Assertions.assertEquals(4, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 16), sLocalDatePicker.getSelection().get(1));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getSelection().get(2));
        Assertions.assertEquals(LocalDate.of(2024, 6, 14), sLocalDatePicker.getSelection().get(3));
        Assertions.assertEquals(LocalDate.of(2024, 6, 14), sLocalDatePicker.getValue());

        // WHEN click on single date -> select only one
        frameFixture.toggleButton("2024-06-20").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 20), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 20), sLocalDatePicker.getValue());

        // WHEN click on single date again -> unselect
        frameFixture.toggleButton("2024-06-20").click();
        // THEN
        Assertions.assertEquals(0, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(null, sLocalDatePicker.getValue());
    }

    @Test
    public void multiTest() {

        var ref = new AtomicReference<SLocalDatePicker>();
        construct(() -> {
            var sList = new SLocalDatePicker() //
                    .name("sLocalDatePicker")
                    .mode(SLocalDatePicker.Mode.MULTIPLE)
                    .displayedLocalDate(LocalDate.of(2024, 6, 1));
            ref.set(sList);
            return TestUtil.inSFrame(sList);
        });
        var sLocalDatePicker = ref.get();

        // WHEN click on single date -> select
        frameFixture.toggleButton("2024-06-15").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getValue());

        // WHEN shift click on other date -> select range
        withShiftPressed(() -> {
            frameFixture.toggleButton("2024-06-17").click();
        });
        // THEN
        Assertions.assertEquals(3, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 16), sLocalDatePicker.getSelection().get(1));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getSelection().get(2));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getValue());

        // WHEN shift click on another data -> extend range
        withShiftPressed(() -> {
            frameFixture.toggleButton("2024-06-14").click();
        });
        // THEN
        Assertions.assertEquals(4, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 16), sLocalDatePicker.getSelection().get(1));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getSelection().get(2));
        Assertions.assertEquals(LocalDate.of(2024, 6, 14), sLocalDatePicker.getSelection().get(3));
        Assertions.assertEquals(LocalDate.of(2024, 6, 14), sLocalDatePicker.getValue());

        // WHEN ctrl click on another data -> extend range
        withControlPressed(() -> {
            frameFixture.toggleButton("2024-06-10").click();
        });
        // THEN
        Assertions.assertEquals(5, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 15), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 16), sLocalDatePicker.getSelection().get(1));
        Assertions.assertEquals(LocalDate.of(2024, 6, 17), sLocalDatePicker.getSelection().get(2));
        Assertions.assertEquals(LocalDate.of(2024, 6, 14), sLocalDatePicker.getSelection().get(3));
        Assertions.assertEquals(LocalDate.of(2024, 6, 10), sLocalDatePicker.getSelection().get(4));
        Assertions.assertEquals(LocalDate.of(2024, 6, 10), sLocalDatePicker.getValue());

        // WHEN click on single date -> select only one
        frameFixture.toggleButton("2024-06-20").click();
        // THEN
        Assertions.assertEquals(1, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(LocalDate.of(2024, 6, 20), sLocalDatePicker.getSelection().get(0));
        Assertions.assertEquals(LocalDate.of(2024, 6, 20), sLocalDatePicker.getValue());

        // WHEN click on single date again -> unselect
        frameFixture.toggleButton("2024-06-20").click();
        // THEN
        Assertions.assertEquals(0, sLocalDatePicker.getSelection().size());
        Assertions.assertEquals(null, sLocalDatePicker.getValue());
    }
}
