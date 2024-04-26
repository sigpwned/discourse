package com.sigpwned.discourse.core.configurable.component;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class SetterConfigurableComponent extends ConfigurableComponent {

  private final Method method;

  public SetterConfigurableComponent(Method method) {
    super(method.getName(), method.getParameterTypes()[0], method.getGenericParameterTypes()[0],
        List.of(method.getAnnotations()));
    this.method = requireNonNull(method);
  }

  public boolean isVisible() {
    return Modifier.isPublic(getMethod().getModifiers());
  }

  public Method getMethod() {
    return method;
  }

  public Optional<BiConsumer<Object, Object>> getSetter() {
    if (isVisible()) {
      return Optional.of((object, value) -> {
        try {
          getMethod().invoke(object, value);
        } catch (ReflectiveOperationException e) {
          // TODO better exception
          throw new RuntimeException(e);
        }
      });
    }
    return Optional.empty();
  }
}
