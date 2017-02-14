package com.rytec.rec.util;


import java.lang.annotation.*;

/**
 * Created by danny on 16-11-18.
 *
 * 通道的类型，可以用于注解一个通道的实现类
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AnnotationChannelType {
    int value();
}
