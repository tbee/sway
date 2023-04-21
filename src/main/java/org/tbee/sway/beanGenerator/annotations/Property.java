package org.tbee.sway.beanGenerator.annotations;

import org.tbee.sway.beanGenerator.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Property  {
    String name() default "";

    /**
     * Only relevant for collections like isList
     */
    String nameSingular() default "";

    boolean getter() default true;
    /**
     * Some other methods need the getter to be present.
     * If it should not be visible, then this can make it private
     */
    Scope getterScope() default Scope.PUBLIC;
    boolean setter() default true;
    /**
     * Some other methods need the setter to be present,
     * If it should not be visible, then this can make it private
     */
    Scope setterScope() default Scope.PUBLIC;
    boolean wither() default false;

    /**
     * RecordStyleGetter and RecordStyleWither will conflict
     */
    boolean recordStyleGetter() default false;
    boolean recordStyleSetter() default false;

    /**
     * RecordStyleWither and RecordStyleGetter will conflict
     */
    boolean recordStyleWither() default true;

    boolean bindEndpoint() default true;
    boolean beanBinderEndpoint() default true;

    boolean propertyNameConstant() default true;

    boolean includeInToString() default false;
    boolean includeInEqualsAndHashcode() default false;
    boolean includeInHasSameIdentity() default false;
    boolean includeInHasSameState() default false;

    boolean isList() default false;

    String opposingProperty() default "";
}
