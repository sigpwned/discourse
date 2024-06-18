package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when one object is nominated for syntax consideration more than once.
 */
@SuppressWarnings("serial")
public class DuplicateSyntaxNomineesScanException extends ScanException {
  private final Set<Object> duplicatedNominees;

  public DuplicateSyntaxNomineesScanException(Class<?> clazz, Set<Object> duplicatedNominees) {
    super(clazz, format("Class %s has duplicate syntax nominees", clazz.getName()));
    this.duplicatedNominees = unmodifiableSet(duplicatedNominees);
  }

  public Set<Object> getDuplicatedNominees() {
    return duplicatedNominees;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(),
        getDuplicatedNominees().stream().map(Objects::toString).collect(joining(", "))};
  }
}
