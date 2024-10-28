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
public class DuplicateSyntaxNamesScanException extends ScanException {
  private final Set<String> duplicatedNames;

  public DuplicateSyntaxNamesScanException(Class<?> clazz, Set<String> duplicatedNames) {
    super(clazz, format("Class %s has duplicate syntax names %s", clazz.getName(),
        String.join(", ", duplicatedNames)));
    this.duplicatedNames = unmodifiableSet(duplicatedNames);
  }

  public Set<String> getDuplicatedNames() {
    return duplicatedNames;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(),
        getDuplicatedNames().stream().map(Objects::toString).collect(joining(", "))};
  }
}
