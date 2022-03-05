package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A configuration property that encodes a boolean value by its presence or absence. If the flag
 * appears on the command line, then it is assigned {@code true}. Otherwise, it is assigned
 * {@code false}.
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface FlagParameter {
  public String shortName() default "";

  public String longName() default "";

  public String description() default "";
}
