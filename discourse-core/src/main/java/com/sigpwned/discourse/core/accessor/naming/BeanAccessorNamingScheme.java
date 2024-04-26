package com.sigpwned.discourse.core.accessor.naming;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the JavaBean naming scheme.
 * </p>
 *
 * <p>
 * The JavaBeans naming scheme is a convention for naming methods that are used to access and modify
 * the properties of a Java object. In brief, the convention is to use the prefix {@code get} or
 * {@code set} followed by the name of the property with the first letter capitalized. For example,
 * if the property is {@code name}, then the getter would be {@code getName()} and the setter would
 * be {@code setName(String name)}.
 * </p>
 *
 * <p>
 * When parsing a method, this naming scheme will inspect the method name and determine if it is a
 * getter or setter based on the prefix. If it is a getter, then it will return the name of the
 * attribute that the getter gets. If it is a setter, then it will return the name of the attribute
 * that the setter sets. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * When matching a method to an attribute, this naming scheme will perform the same operation,
 * except that it will compare the attribute name to the name of the attribute that the method
 * represents. If they are equal, then it will return {@link Optional#of(Object) true}. Otherwise,
 * it will return {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * This implementation will always return {@link Optional#empty() empty} for objects that are not
 * methods, which is to say fields and parameters.
 * </p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/JavaBeans">
 * https://en.wikipedia.org/wiki/JavaBeans</a>
 */
public class BeanAccessorNamingScheme implements AccessorNamingScheme {

  public static final BeanAccessorNamingScheme INSTANCE = new BeanAccessorNamingScheme();

  @Override
  public Optional<String> getAttributeGetterName(String methodName, List<Annotation> annotations) {
    if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(
        methodName.charAt(3))) {
      return Optional.of(Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> getAttributeSetterName(String methodName, List<Annotation> annotations) {
    if (methodName.startsWith("set") && methodName.length() > 3 && Character.isUpperCase(
        methodName.charAt(3))) {
      return Optional.of(Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    if (getAttributeGetterName(methodName, methodAnnotations).map(attributeName::equals)
        .orElse(false)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    if (getAttributeSetterName(methodName, methodAnnotations).map(attributeName::equals)
        .orElse(false)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }
}
