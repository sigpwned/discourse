package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a super command has more than one subcommand with the same discriminator.
 */
@SuppressWarnings("serial")
public class DuplicateDiscriminatorsScanException extends ScanException {
  private final Set<Discriminator> duplicatedDiscriminators;

  public DuplicateDiscriminatorsScanException(Class<?> clazz,
      Set<Discriminator> duplicatedDiscriminators) {
    super(clazz, format("Subcommand class %s has duplicate discriminators %s", clazz,
        duplicatedDiscriminators));
    this.duplicatedDiscriminators = unmodifiableSet(duplicatedDiscriminators);
  }

  public Set<Discriminator> getDuplicatedDiscriminators() {
    return duplicatedDiscriminators;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(),
        getDuplicatedDiscriminators().stream().map(Objects::toString).collect(joining(", "))};
  }
}
