package com.sigpwned.discourse.core.annotation;

public @interface Subcommand {
  public String discriminator();
  
  public Class<?> configurable();
}
