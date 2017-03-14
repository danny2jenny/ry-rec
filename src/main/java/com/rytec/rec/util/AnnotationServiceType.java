package com.rytec.rec.util;

import java.lang.annotation.*;

/**
 * Created by Danny on 2017/3/13.
 * <p>
 * Tcp 服务的类型，内部使用
 */

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AnnotationServiceType {
    int value();
}

