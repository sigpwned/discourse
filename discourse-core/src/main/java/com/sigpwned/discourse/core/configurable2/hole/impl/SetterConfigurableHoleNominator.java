package com.sigpwned.discourse.core.configurable2.hole.impl;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleCandidate;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleNominator;
import com.sigpwned.discourse.core.util.Reflection;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class SetterConfigurableHoleNominator implements ConfigurableHoleNominator {

  @Override
  public List<ConfigurableHoleCandidate> nominateHoles(Class<?> clazz) {
    List<ConfigurableHoleCandidate> result = new ArrayList<>();
    for (Method method : clazz.getDeclaredMethods()) {
      if (Reflection.hasInstanceSetterSignature(method) && Modifier.isPublic(
          method.getModifiers())) {
        result.add(new ConfigurableHoleCandidate(method, method.getGenericParameterTypes()[0],
            List.of(method.getAnnotations())));
      }
    }
    return unmodifiableList(result);
  }
}
