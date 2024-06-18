package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a command has more than one property with the same name.
 */
@SuppressWarnings("serial")
public class DuplicateCoordinatesScanException extends ScanException {
  private final Set<Coordinate> duplicatedCoordinates;

  public DuplicateCoordinatesScanException(Class<?> clazz, Set<Coordinate> duplicatedCoordinates) {
    super(clazz, format("Class %s has duplicate coordinates %s", clazz.getName(),
        duplicatedCoordinates.stream().map(Objects::toString).collect(joining(", "))));
    this.duplicatedCoordinates = unmodifiableSet(duplicatedCoordinates);
  }

  public Set<Coordinate> getDuplicatedCoordinates() {
    return duplicatedCoordinates;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(),
        getDuplicatedCoordinates().stream().map(Objects::toString).collect(joining(", "))};
  }
}
