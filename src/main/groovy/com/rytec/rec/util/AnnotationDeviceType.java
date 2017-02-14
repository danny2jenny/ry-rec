package com.rytec.rec.util;

import java.lang.annotation.*;

/**
 * Created by danny on 16-11-18.
 *
 * 设备类型的注解
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AnnotationDeviceType {
    int value();
}
