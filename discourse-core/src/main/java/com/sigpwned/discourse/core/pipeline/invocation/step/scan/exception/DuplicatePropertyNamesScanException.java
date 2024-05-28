package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class DuplicatePropertyNamesScanException extends ScanException {
  private final Set<String> duplicatedPropertyNames;

  public DuplicatePropertyNamesScanException(Class<?> clazz, Set<String> duplicatedPropertyNames) {
    super(clazz, format("Class %s has duplicate rule nominees", clazz.getName()));
    this.duplicatedPropertyNames = unmodifiableSet(duplicatedPropertyNames);
  }

  public Set<String> getDuplicatedPropertyNames() {
    return duplicatedPropertyNames;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(),
        getDuplicatedPropertyNames().stream().collect(joining(", "))};
  }
}
