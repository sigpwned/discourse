package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A configuration property that appears positionally after all options and switches in the command
 * line. Parameter positions are zero-based.
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface PositionalParameter {
  public int position();

  public String description() default "";

  public boolean required() default true;
}
