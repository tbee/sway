package org.tbee.sway.beanGenerator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that this class should be extended using the Sway bean generator.
 *
 * This can be done in two ways, the dual or triple stack setup.
 * See README.MD for more information.
 *
 */
@Target(ElementType.TYPE) // this is class
@Retention(RetentionPolicy.SOURCE)
public @interface Bean {
    public String stripSuffix() default "";
    public String appendSuffixToBean() default "";
}
