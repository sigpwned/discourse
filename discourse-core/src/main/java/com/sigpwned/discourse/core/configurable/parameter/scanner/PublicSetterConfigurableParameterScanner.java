package com.sigpwned.discourse.core.configurable.parameter.scanner;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ConfigurableParameterScanner;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerResolver;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkResolver;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.util.ConfigurationParameters;
import com.sigpwned.discourse.core.util.ParameterAnnotations;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublicSetterConfigurableParameterScanner implements ConfigurableParameterScanner {

  @Override
  public List<ConfigurationParameter> scanForParameters(Class<?> type, InvocationContext context) {
    final ValueDeserializerResolver deserializerResolver = context.get(
        InvocationContext.VALUE_DESERIALIZER_RESOLVER_KEY).orElseThrow();
    final ValueSinkResolver sinkResolver = context.get(InvocationContext.VALUE_SINK_RESOLVER_KEY)
        .orElseThrow();
    return scanForFields(type).stream().map(sfi -> {
      // TODO Better Exceptions
      final Annotation annotation = sfi.annotation();
      final Field field = sfi.field();
      final ValueDeserializer<?> deserializer = deserializerResolver.resolveValueDeserializer(
              field.getGenericType(), List.of(field.getAnnotations()))
          .orElseThrow(() -> new IllegalArgumentException("no deserializer"));
      final ValueSink sink = sinkResolver.resolveValueSink(field.getGenericType(),
          List.of(field.getAnnotations()));
      return ConfigurationParameters.createConfigurationParameter(annotation, field.getName(),
          deserializer, sink);
    }).toList();
  }

  protected static record SuitableField(Annotation annotation, Field field) {

    public SuitableField {
      annotation = requireNonNull(annotation);
      field = requireNonNull(field);
    }
  }

  /**
   * <p>
   * Scans the given class for suitable fields. Suitable fields are:
   * </p>
   *
   * <ul>
   *   <li>public</li>
   *   <li>instance (i.e., not static)</li>
   *   <li>mutable (i.e., not final)</li>
   *   <li>annotated with exactly one of the following annotations:</li>
   *   <ul>
   *     <li>{@link FlagParameter}</li>
   *     <li>{@link OptionParameter}</li>
   *     <li>{@link PositionalParameter}</li>
   *     <li>{@link EnvironmentParameter}</li>
   *     <li>{@link PropertyParameter}</li>
   *   </ul>
   * </ul>
   *
   * @param type the class to scan
   * @return a list of suitable fields
   */
  protected List<SuitableField> scanForFields(Class<?> type) {
    List<SuitableField> result = new ArrayList<>();
    for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
      for (Field field : clazz.getFields()) {
        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        if (Modifier.isFinal(field.getModifiers())) {
          continue;
        }
        if (!Modifier.isPublic(field.getModifiers())) {
          continue;
        }

        List<Annotation> annotations = Arrays.stream(field.getAnnotations())
            .filter(ParameterAnnotations::isParameterAnnotation).toList();
        if (annotations.isEmpty()) {
          continue;
        }
        if (annotations.size() > 1) {
          throw new TooManyAnnotationsConfigurationException(field.getName());
        }

        result.add(new SuitableField(annotations.get(0), field));
      }
    }
    return unmodifiableList(result);
  }
}
