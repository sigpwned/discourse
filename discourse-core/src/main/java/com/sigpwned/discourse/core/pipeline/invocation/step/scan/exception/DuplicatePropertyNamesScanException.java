package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class DuplicatePropertyNamesScanException extends ScanException {
  private final Class<?> clazz;

  private final Set<String> duplicatedPropertyNames;

  public DuplicatePropertyNamesScanException(Class<?> clazz, Set<String> duplicatedPropertyNames) {
    super(format("Class %s has duplicate rule nominees", clazz.getName()));
    this.clazz = requireNonNull(clazz);
    this.duplicatedPropertyNames = unmodifiableSet(duplicatedPropertyNames);
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public Set<String> getDuplicatedPropertyNames() {
    return duplicatedPropertyNames;
  }
}
