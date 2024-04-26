package com.sigpwned.discourse.core.accessor.naming;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the method name to determine if a method is an
 * attribute getter or setter.
 * </p>
 *
 * <p>
 * This implementation always returns {@link Optional#empty() empty} when parsing names.
 * </p>
 *
 * <p>
 * When matching a method to an attribute, this naming scheme will return
 * {@link Optional#of(Object) true} if the method name is the same as the attribute name. Otherwise,
 * it will return {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * This implementation is intended to be used with <a
 * href="https://docs.oracle.com/en/java/javase/17/language/records.html">record classes</a>, which
 * have a naming scheme that is similar to the JavaBeans naming scheme, but with the {@code get} and
 * {@code set} prefixes removed.
 * </p>
 */
public class RecordAccessorNamingScheme implements AccessorNamingScheme {

  public static final RecordAccessorNamingScheme INSTANCE = new RecordAccessorNamingScheme();

  @Override
  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    if (methodName.equals(attributeName)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    if (methodName.equals(attributeName)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }
}
