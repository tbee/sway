package org.tbee.sway.beanGenerator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Property  {
    public String name() default "";

    public boolean setter() default true;
    public boolean getter() default true;
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

    public boolean bindEndpoint() default true;
    public boolean beanBinderEndpoint() default true;

    public boolean propertyNameConstant() default true;

    public boolean includeInToString() default true;
}
