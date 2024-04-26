package com.sigpwned.discourse.core.chain;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.format.exception.DefaultExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.ExceptionFormatter;
import com.sigpwned.discourse.core.util.Chains;

public class ExceptionFormatterChain extends Chain<ExceptionFormatter> {

  private ExceptionFormatter defaultFormatter;

  public ExceptionFormatterChain() {
    this(DefaultExceptionFormatter.INSTANCE);
  }

  public ExceptionFormatterChain(ExceptionFormatter defaultFormatter) {
    this.defaultFormatter = requireNonNull(defaultFormatter);
  }

  public ExceptionFormatter getExceptionFormatter(Throwable e) {
    return Chains.stream(this).filter(formatter -> formatter.handlesException(e)).findFirst()
        .orElseGet(this::getDefaultFormatter);
  }

  public ExceptionFormatter getDefaultFormatter() {
    return defaultFormatter;
  }

  public void setDefaultFormatter(ExceptionFormatter defaultFormatter) {
    this.defaultFormatter = requireNonNull(defaultFormatter);
  }
}
