package com.sigpwned.discourse.core.accessor.naming;

import com.sigpwned.discourse.core.annotation.DiscourseIgnore;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the {@link DiscourseIgnore} annotation to determine if
 * a method or field should be ignored.
 * </p>
 *
 * <p>
 * This implementation always returns {@link Optional#empty() empty} when parsing names.
 * </p>
 *
 * <p>
 * When matching a method or field to an attribute, this naming scheme will look for
 * {@code DiscourseIgnore}, and if it is present, then it will return
 * {@link Optional#of(Object) false}. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 */
public class DiscourseIgnoreAnnotationAccessorNamingScheme implements AccessorNamingScheme {

  public static final DiscourseIgnoreAnnotationAccessorNamingScheme INSTANCE = new DiscourseIgnoreAnnotationAccessorNamingScheme();

  @Override
  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return isIgnored(attributeName, methodAnnotations);
  }

  @Override
  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return isIgnored(attributeName, methodAnnotations);
  }

  @Override
  public Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    return isIgnored(attributeName, fieldAnnotations);
  }

  private Optional<Boolean> isIgnored(String attributeName, List<Annotation> annotations) {
    if (annotations.stream().anyMatch(a -> a instanceof DiscourseIgnore)) {
      return Optional.of(false);
    }
    return Optional.empty();
  }
}
