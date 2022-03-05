package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A configuration option captured from the named environment variable.
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface EnvironmentParameter {
  public String variableName();

  public String description() default "";

  public boolean required() default false;
}
