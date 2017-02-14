package com.rytec.rec.util;

import java.lang.annotation.*;

/**
 * Created by danny on 17-1-31.
 * 通用注解
 * 一个元素的描述
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AnnotationDescription {
    String value();
}
