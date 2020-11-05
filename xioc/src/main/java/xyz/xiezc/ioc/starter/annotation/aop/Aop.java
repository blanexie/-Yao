package xyz.xiezc.ioc.starter.annotation.aop;

import xyz.xiezc.ioc.starter.annotation.core.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Aop {

  /**
   * 切面类
   *
   * @return
   */
  Class<?> value();


}
