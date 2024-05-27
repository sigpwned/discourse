package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class DuplicateSyntaxNomineesScanException extends ScanException {
  private final Class<?> clazz;

  private final Set<Object> duplicatedNominees;

  public DuplicateSyntaxNomineesScanException(Class<?> clazz, Set<Object> duplicatedNominees) {
    super(format("Class %s has duplicate syntax nominees", clazz.getName()));
    this.clazz = requireNonNull(clazz);
    this.duplicatedNominees = unmodifiableSet(duplicatedNominees);
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public Set<Object> getDuplicatedNominees() {
    return duplicatedNominees;
  }
}
