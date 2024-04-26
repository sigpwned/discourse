package com.sigpwned.discourse.core.configurable.component;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class FieldConfigurableComponent extends ConfigurableComponent {

  private final Field field;

  public FieldConfigurableComponent(Field field) {
    super(field.getName(), field.getType(), field.getGenericType(),
        List.of(field.getAnnotations()));
    this.field = requireNonNull(field);
  }

  public boolean isVisible() {
    return Modifier.isPublic(getField().getModifiers());
  }

  public boolean isMutable() {
    return !Modifier.isFinal(getField().getModifiers());
  }

  private Field getField() {
    return field;
  }

  public Optional<BiConsumer<Object, Object>> getSetter() {
    if (isVisible() && isMutable()) {
      return Optional.of((object, value) -> {
        try {
          getField().set(object, value);
        } catch (IllegalAccessException e) {
          // TODO better exception
          throw new RuntimeException(e);
        }
      });
    }
    return Optional.empty();
  }
}
