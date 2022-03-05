package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A configuration property that encodes its value according to a name/value pair in a command line.
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface OptionParameter {
  public String shortName() default "";

  public String longName() default "";

  public String description() default "";
  
  public boolean required() default false;
}
