package com.sigpwned.discourse.core.configurable2.naming;

import com.sigpwned.discourse.core.configurable2.ConfigurableAttributeNamingScheme;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntax;
import com.sigpwned.discourse.core.util.Reflection;
import java.lang.reflect.Method;
import java.util.Optional;

public class GetterAttributeNamingScheme implements ConfigurableAttributeNamingScheme {

  @Override
  public Optional<String> nameAttribute(ConfigurableSyntax syntax) {
    if (syntax.nominated() instanceof Method method && Reflection.hasInstanceGetterSignature(method)
        && method.getName().startsWith("get") && method.getName().length() >= 4) {
      return Optional.of(
          Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4));
    }
    return Optional.empty();
  }
}
