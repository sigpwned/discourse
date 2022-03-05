package com.sigpwned.discourse.core.util;

import static java.lang.String.format;
import java.util.regex.Pattern;

public class Arguments {
  public static final Pattern SEPARATOR = Pattern.compile("--");

  public static final Pattern LONG_NAME_PREFIX = Pattern.compile("--");

  public static final Pattern LONG_NAME_VALUE_SEPARATOR = Pattern.compile("=");

  public static final Pattern SHORT_NAME_PREFIX = Pattern.compile("-");
  
  public static final Pattern BUNDLE=Pattern.compile(format(
      "%s(%s{2,})",
      SHORT_NAME_PREFIX.pattern(),
      Parameters.SHORT_NAME_PATTERN.pattern()));

  public static final Pattern SHORT_NAME=Pattern.compile(format(
      "%s(%s)",
      SHORT_NAME_PREFIX.pattern(),
      Parameters.SHORT_NAME_PATTERN.pattern()));

  public static final Pattern LONG_NAME=Pattern.compile(format(
      "%s(%s)",
      LONG_NAME_PREFIX.pattern(),
      Parameters.LONG_NAME_PATTERN.pattern()));

  public static final Pattern LONG_NAME_VALUE=Pattern.compile(format(
      "%s(%s)%s(%s)",
      LONG_NAME_PREFIX.pattern(),
      Parameters.LONG_NAME_PATTERN.pattern(),
      LONG_NAME_VALUE_SEPARATOR.pattern(),
      ".*"));
}
