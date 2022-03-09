package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({TYPE})
public @interface Configurable {
  public String name() default "";
  
  public String description() default "";
  
  public String discriminator() default "";
  
  public String version() default "";
  
  public Subcommand[] subcommands() default {};
}
