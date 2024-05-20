package com.sigpwned.discourse.core.module.parameter.flag;

public @interface FlagParameter {
  public String shortName();

  public String longName();

  public boolean help() default false;

  public boolean version() default false;
}
