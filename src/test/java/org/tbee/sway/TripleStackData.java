package org.tbee.sway;

import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.util.AbstractBean;

@Bean(stripSuffix = "Data", appendSuffixToBean = "Bean")
abstract class TripleStackData extends AbstractBean<TripleStack> {
    @Property
    String name;
}
