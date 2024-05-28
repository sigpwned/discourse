package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class NotConfigurableScanException extends ScanException {
  public NotConfigurableScanException(Class<?> clazz) {
    super(clazz, format("Command class %s must be annotated with @Configurable", clazz.getName()));
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName()};
  }
}
