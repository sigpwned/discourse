package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class DuplicateDiscriminatorsScanException extends ScanException {
  private final Class<?> supercommandClazz;

  private final Set<Discriminator> duplicatedDiscriminators;

  public DuplicateDiscriminatorsScanException(Class<?> supercommandClazz,
      Set<Discriminator> duplicatedDiscriminators) {
    super(format("Subcommand class %s has duplicate discriminators %s", supercommandClazz,
        duplicatedDiscriminators));
    this.supercommandClazz = requireNonNull(supercommandClazz);
    this.duplicatedDiscriminators = unmodifiableSet(duplicatedDiscriminators);
  }

  public Class<?> getSupercommandClazz() {
    return supercommandClazz;
  }

  public Set<Discriminator> getDuplicatedDiscriminators() {
    return duplicatedDiscriminators;
  }
}
