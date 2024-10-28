package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import java.lang.annotation.Annotation;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when an {@link OptionParameter option} has no coordinates.
 */
@SuppressWarnings("serial")
public class NoAnnotationCoordinatesScanException extends ScanException {
  private final Annotation annotation;
  private final String candidateName;

  public NoAnnotationCoordinatesScanException(Class<?> clazz, String candidateName,
      Annotation annotation) {
    super(clazz,
        format(
            "Command class %s syntax candidate %s annotation %s must have at least one coordinate",
            clazz.getName(), candidateName, annotation.annotationType().getSimpleName()));
    this.candidateName = requireNonNull(candidateName);
    this.annotation = requireNonNull(annotation);
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), getCandidateName(),
        getAnnotation().annotationType().getSimpleName()};
  }

  public String getCandidateName() {
    return candidateName;
  }

  public Annotation getAnnotation() {
    return annotation;
  }
}
