package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ResourceBundle;

@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface DiscourseLocalize {
  /**
   * The {@link ResourceBundle bundle name} to use for localization.
   * 
   * @return
   */
  public String value();
}
