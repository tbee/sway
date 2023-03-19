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
public @interface ListProperty {
    /**
     * The plural name
     * @return
     */
    public String name() default "";
    /**
     * The singular name
     * @return
     */
    public String nameSingular() default "";

    public boolean getter() default true;
    public Scope getterScope() default Scope.PUBLIC;
    public boolean setter() default true;
    public Scope setterScope() default Scope.PUBLIC;
    public boolean wither() default false;

    /**
     * RecordStyleGetter and RecordStyleWither will conflict
     * @return
     */
    public boolean recordStyleGetter() default false;
    public boolean recordStyleSetter() default false;

    /**
     * RecordStyleWither and RecordStyleGetter will conflict
     * @return
     */
    public boolean recordStyleWither() default true;

    public boolean adder() default true;
    public boolean remover() default true;

    public boolean bindEndpoint() default true;
    public boolean beanBinderEndpoint() default true;

    public boolean propertyNameConstant() default true;

    public boolean includeInToString() default false;
}
