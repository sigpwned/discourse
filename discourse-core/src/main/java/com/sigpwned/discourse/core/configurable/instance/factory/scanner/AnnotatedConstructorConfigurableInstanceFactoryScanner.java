package com.sigpwned.discourse.core.configurable.instance.factory.scanner;

import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import com.sigpwned.discourse.core.configurable.instance.factory.AnnotatedConstructorConfigurableInstanceFactory;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.util.collectors.Only;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnnotatedConstructorConfigurableInstanceFactoryScanner implements
    ConfigurableInstanceFactoryScanner {

  @Override
  public <T> Optional<ConfigurableInstanceFactory<T>> scanForInstanceFactory(Class<T> rawType) {
    Constructor<T> constructor = Stream.of(rawType.getConstructors())
        .map(ci -> (Constructor<T>) ci)
        .filter(ci -> ci.isAnnotationPresent(DiscourseCreator.class)).collect(Only.toOnly())
        .orElse(null, () -> {
          // TODO better exception
          return new IllegalArgumentException(
              "multiple constructors annotated with @DiscourseCreator");
        });
    if (constructor == null) {
      return Optional.empty();
    }
    if (!Modifier.isPublic(constructor.getModifiers())) {
      // TODO Should we log here? Or throw?
      return Optional.empty();
    }

    List<InputConfigurableComponent> inputs = IntStream.range(0, constructor.getParameterCount())
        .mapToObj(i -> new InputConfigurableComponent(i, constructor.getParameters()[i],
            constructor.getGenericParameterTypes()[i])).toList();

    return Optional.of(
        new AnnotatedConstructorConfigurableInstanceFactory<>(constructor, inputs));
  }
}
