package com.sigpwned.discourse.core.accessor.naming;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses parameter names to determine the names of attributes.
 * </p>
 *
 * <p>
 * This naming scheme will use the name of the parameter as the name of the attribute. If the
 * parameter is synthetic, then it will return {@link Optional#empty() empty}.
 * </p>
 *
 * @see <a
 * href="https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html">
 * https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html</a>
 */
public class ParameterAccessorNamingScheme implements AccessorNamingScheme {

  public static final ParameterAccessorNamingScheme INSTANCE = new ParameterAccessorNamingScheme();

  /**
   * @see <a
   * href="https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html">
   * https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html</a>
   */
  private static final Pattern SYNTHETIC_PARAMETER_NAME_PATTERN = Pattern.compile("arg\\d+");

  @Override
  public Optional<String> getAttributeConstructorParameterName(
      List<Annotation> constructorAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    if (SYNTHETIC_PARAMETER_NAME_PATTERN.matcher(parameterName).matches()) {
      return Optional.empty();
    }
    return Optional.of(parameterName);
  }

  @Override
  public Optional<String> getAttributeFactoryMethodParameterName(String methodName,
      List<Annotation> methodAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    if (SYNTHETIC_PARAMETER_NAME_PATTERN.matcher(parameterName).matches()) {
      return Optional.empty();
    }
    return Optional.of(parameterName);
  }
}
