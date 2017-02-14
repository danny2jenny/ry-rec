package com.rytec.rec.util;

import java.lang.annotation.*;

/**
 * Created by danny on 17-1-31.
 * 对需要导出到JavaScript的常量进行注解
 */

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AnnotationJSExport {
    String value();
}
