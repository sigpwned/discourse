package com.sigpwned.discourse.core.format.help.text;

import static java.util.Objects.requireNonNull;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.format.help.TextFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public abstract class AbstractPatternTextFormatter implements TextFormatter {
  /**
   * The pattern to match. Must have exactly one capture group.
   */
  private final Pattern pattern;

  /**
   * The format to prepend to the matched text.
   */
  private final String beginFormat;

  /**
   * The format to append to the matched text.
   */
  private final String endFormat;

  protected AbstractPatternTextFormatter(Pattern pattern, String beginFormat, String endFormat) {
    this.pattern = requireNonNull(pattern);
    this.beginFormat = requireNonNull(beginFormat);
    this.endFormat = requireNonNull(endFormat);
  }

  @Override
  public String formatText(String text, InvocationContext context) {
    return getPattern().matcher(text).replaceAll(getBeginFormat() + "$1" + getEndFormat());
  }

  /**
   * @return the pattern
   */
  private Pattern getPattern() {
    return pattern;
  }

  /**
   * @return the beginFormat
   */
  private String getBeginFormat() {
    return beginFormat;
  }

  /**
   * @return the endFormat
   */
  private String getEndFormat() {
    return endFormat;
  }
}
