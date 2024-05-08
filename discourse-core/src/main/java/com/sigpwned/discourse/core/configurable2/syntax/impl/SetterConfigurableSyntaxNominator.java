package com.sigpwned.discourse.core.configurable2.syntax.impl;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxCandidate;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxNominator;
import com.sigpwned.discourse.core.util.Reflection;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SetterConfigurableSyntaxNominator implements ConfigurableSyntaxNominator {

  @Override
  public List<ConfigurableSyntaxCandidate> nominateSyntax(Class<?> clazz) {
    List<ConfigurableSyntaxCandidate> result = new ArrayList<>();
    for (Method method : clazz.getDeclaredMethods()) {
      if (Reflection.hasInstanceSetterSignature(method)) {
        result.add(new ConfigurableSyntaxCandidate(method, method.getGenericParameterTypes()[0],
            List.of(method.getAnnotations())));
      }
    }
    return unmodifiableList(result);
  }
}