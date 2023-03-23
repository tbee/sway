package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BeanGeneratorTest {

    @Test
    public void partnerCity() {
        City city = new City().name("city");
        City partnerCity = new City().name("partner");
        city.addPartnerCity(partnerCity);
        Assertions.assertEquals(1, city.partnerCities().size());
    }
}
