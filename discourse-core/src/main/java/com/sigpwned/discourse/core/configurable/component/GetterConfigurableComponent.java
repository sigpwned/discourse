package com.sigpwned.discourse.core.configurable.component;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public final class GetterConfigurableComponent extends ConfigurableComponent {

  private final Method method;

  public GetterConfigurableComponent(Method method) {
    super(method.getName(), method.getReturnType(), method.getGenericReturnType(),
        List.of(method.getAnnotations()));
    this.method = requireNonNull(method);
  }

  public boolean isVisible() {
    return Modifier.isPublic(getMethod().getModifiers());
  }

  private Method getMethod() {
    return method;
  }
}
