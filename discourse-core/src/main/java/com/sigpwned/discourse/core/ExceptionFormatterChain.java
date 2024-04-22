package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.util.Chains;
import java.util.Optional;

public class ExceptionFormatterChain extends Chain<ExceptionFormatter> {

  public Optional<ExceptionFormatter> getExceptionFormatter(Exception e) {
    return Chains.stream(this).filter(formatter -> formatter.handlesException(e)).findFirst();
  }
}
