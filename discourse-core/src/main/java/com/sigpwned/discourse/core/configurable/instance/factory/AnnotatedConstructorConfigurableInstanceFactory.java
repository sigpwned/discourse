package com.sigpwned.discourse.core.configurable.instance.factory;

import com.sigpwned.discourse.core.AttributeDefinition;
import com.sigpwned.discourse.core.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.ConfigurableInstanceFactoryProvider;
import com.sigpwned.discourse.core.annotation.DiscourseAttribute;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.util.ClassWalkers;
import com.sigpwned.discourse.core.util.ParameterAnnotations;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.discourse.core.util.collectors.Only;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class AnnotatedConstructorConfigurableInstanceFactory<T> implements
    ConfigurableInstanceFactory<T> {

  private static final Pattern SYNTHETIC_PARAMETER_NAME_PATTERN = Pattern.compile("arg\\d+");

  public static class Provider implements ConfigurableInstanceFactoryProvider {

    @Override
    public <T> Optional<ConfigurableInstanceFactory<T>> getConfigurationInstanceFactory(
        Class<T> rawType) {
      Constructor<T> constructor = ClassWalkers.streamClass(rawType)
          .mapMulti(Streams.filterAndCast(Constructor.class)).map(ci -> (Constructor<T>) ci)
          .filter(ci -> ci.isAnnotationPresent(DiscourseCreator.class)).collect(Only.toOnly())
          .orElseGet(null, () -> {
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

      List<String> parameterNames = new ArrayList<>();
      List<AttributeDefinition> parameterDefinitions = new ArrayList<>();
      for (int i = 0; i < constructor.getParameterCount(); i++) {
        Parameter pi = constructor.getParameters()[i];
        Type pigt = constructor.getGenericParameterTypes()[i];
        Annotation[] pias = constructor.getParameterAnnotations()[i];

        String parameterName = null;
        if (parameterName == null) {
          parameterName = Optional.ofNullable(pi.getAnnotation(DiscourseAttribute.class))
              .map(DiscourseAttribute::value).orElse(null);
        }
        if (parameterName == null) {
          if (!SYNTHETIC_PARAMETER_NAME_PATTERN.matcher(pi.getName()).matches()) {
            parameterName = pi.getName();
          }
        }
        if (parameterName == null) {
          // TODO Should we log here? Or throw?
          // We can't use an instance factory that doesn't have a name for the parameter
          return Optional.empty();
        }

        if (parameterNames.contains(parameterName)) {
          // TODO better exception
          throw new IllegalArgumentException("duplicate parameter name: " + parameterName);
        }

        parameterNames.add(parameterName);

        ParameterAnnotations.findParameterAnnotation(pias).ifPresent(parameterAnnotation -> {
          parameterDefinitions.add(
              new AttributeDefinition(pi.getName(), parameterAnnotation, pi.getType(), pigt));
        }, () -> {
          // TODO better exception
          throw new IllegalArgumentException("multiple parameter annotations");
        });
      }

      Constructor<T> defaultConstructor;
      try {
        defaultConstructor = rawType.getConstructor();
      } catch (NoSuchMethodException e) {
        return Optional.empty();
      }
      if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
        return Optional.empty();
      }
      return Optional.of(new DefaultConstructorConfigurableInstanceFactory<>(defaultConstructor));
    }
  }

  private final Constructor<T> constructor;
  private final List<String> parameterNames;
  private final List<AttributeDefinition> attributeDefinitions;

  @Override
  public Set<String> getRequiredParameterNames() {
    return Set.copyOf(parameterNames);
  }

  @Override
  public List<ConfigurationParameter> getDefinedParameters() {
    return Collections.unmodifiableList(attributeDefinitions);
  }

  @Override
  public T createInstance(Map<String, Object> arguments) {
    Object[] argumentValues = getRequiredParameterNames().stream()
        .map(parameterName -> Optional.ofNullable(arguments.get(parameterName))
            .orElseThrow(() -> {
              // TODO better exception
              return new IllegalArgumentException("missing parameter: " + parameterName);
            }))
        .toArray(Object[]::new);
    try {
      return constructor.newInstance(argumentValues);
    } catch (ReflectiveOperationException e) {
      // TODO better exception
      throw new RuntimeException(e);
    }
  }
}
