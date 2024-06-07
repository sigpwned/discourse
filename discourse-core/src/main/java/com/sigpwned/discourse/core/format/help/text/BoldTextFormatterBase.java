package com.sigpwned.discourse.core.format.help.text;

import java.util.regex.Pattern;

public abstract class BoldTextFormatterBase extends AbstractPatternTextFormatter {
  private static final Pattern PATTERN =
      Pattern.compile("(?<!\\\\)(?<!\\*)\\*\\*([^*]+)\\*\\*(?!\\*)");

  protected BoldTextFormatterBase(String beginFormat, String endFormat) {
    super(PATTERN, beginFormat, endFormat);
  }
}
