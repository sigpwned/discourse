package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.util.collectors.Only;
import java.lang.annotation.Annotation;
import java.util.List;

public final class ParameterAnnotations {

  private ParameterAnnotations() {
  }

  public static Only<Annotation> findParameterAnnotation(Annotation[] annotations) {
    return findParameterAnnotation(List.of(annotations));
  }

  public static Only<Annotation> findParameterAnnotation(List<Annotation> annotations) {
    return annotations.stream().filter(ParameterAnnotations::isParameterAnnotation)
        .collect(Only.toOnly());
  }

  /**
   * <p>
   * Determines if the given annotation is a parameter annotation. The annotation must be one of the
   * following:
   * </p>
   *
   * <ul>
   *   <li>{@link FlagParameter}</li>
   *   <li>{@link OptionParameter}</li>
   *   <li>{@link PositionalParameter}</li>
   *   <li>{@link EnvironmentParameter}</li>
   *   <li>{@link PropertyParameter}</li>
   * </ul>
   *
   * @param annotation the annotation to check
   * @return {@code true} if the annotation is a parameter annotation, or {@code false} otherwise
   */
  public static boolean isParameterAnnotation(Annotation annotation) {
    return annotation instanceof FlagParameter || annotation instanceof OptionParameter
        || annotation instanceof PositionalParameter || annotation instanceof EnvironmentParameter
        || annotation instanceof PropertyParameter;
  }

  /**
   * <p>
   * Returns the description field of the given annotation. The annotation must be a parameter
   * annotation.
   * </p>
   *
   * @param annotation the parameter annotation to get the description from
   * @return the description of the parameter
   * @throws IllegalArgumentException if the annotation is not a parameter annotation
   * @see #isParameterAnnotation(Annotation)
   */
  public static String getDescription(Annotation annotation) {
    if (annotation instanceof FlagParameter flag) {
      return flag.description();
    }
    if (annotation instanceof OptionParameter option) {
      return option.description();
    }
    if (annotation instanceof PositionalParameter positional) {
      return positional.description();
    }
    if (annotation instanceof EnvironmentParameter environment) {
      return environment.description();
    }
    if (annotation instanceof PropertyParameter property) {
      return property.description();
    }
    throw new IllegalArgumentException("unexpected annotation " + annotation);
  }

  /**
   * <p>
   * Determines if the given annotation is required. The annotation must be a parameter annotation.
   * </p>
   *
   * @param annotation the parameter annotation to check
   * @return {@code true} if the parameter is required, or {@code false} otherwise
   * @throws IllegalArgumentException if the annotation is not a parameter annotation
   * @see #isParameterAnnotation(Annotation)
   */
  public static boolean isRequired(Annotation annotation) {
    if (annotation instanceof FlagParameter flag) {
      // Flags are always optional
      return false;
    }
    if (annotation instanceof OptionParameter option) {
      return option.required();
    }
    if (annotation instanceof PositionalParameter positional) {
      return positional.required();
    }
    if (annotation instanceof EnvironmentParameter environment) {
      return environment.required();
    }
    if (annotation instanceof PropertyParameter property) {
      return property.required();
    }
    throw new IllegalArgumentException("unexpected annotation " + annotation);
  }
}
