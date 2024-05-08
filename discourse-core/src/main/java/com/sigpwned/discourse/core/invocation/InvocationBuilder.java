/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.chain.AccessorNamingSchemeChain;
import com.sigpwned.discourse.core.chain.ConfigurableInstanceFactoryScannerChain;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.configurable.ConfigurableClass;
import com.sigpwned.discourse.core.configurable.ConfigurableClassWalker;
import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.FieldConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.GetterConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.SetterConfigurableComponent;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.exception.bean.AssignmentFailureBeanException;
import com.sigpwned.discourse.core.exception.bean.NewInstanceFailureBeanException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.exception.syntax.RequiredParametersMissingSyntaxException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.util.ConfigurationParameters;
import com.sigpwned.discourse.core.util.MoreIterables;
import com.sigpwned.discourse.core.util.MoreSets;
import com.sigpwned.discourse.core.util.ParameterAnnotations;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class InvocationBuilder {

  public <T> InvocationBuilderResolveStep<T> scan(Class<T> rawType, InvocationContext context) {

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listener -> {
      listener.beforeScan(rawType, context);
    });

    Command<T> command = doScan(rawType, context);

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listener -> {
      listener.afterScan(command, context);
    });

    return new InvocationBuilderResolveStep<>(command);
  }

  /**
   * extension hook
   */
  protected <T> Command<T> doScan(Class<T> rawType, InvocationContext context) {
    final ConfigurableInstanceFactoryScannerChain factoryProviderChain = context.get(
        InvocationContext.CONFIGURABLE_INSTANCE_FACTORY_PROVIDER_CHAIN_KEY).orElseThrow();
    final AtomicReference<Command<? extends T>> result = new AtomicReference<>();

    new ConfigurableClassWalker<>(rawType).walk(new ConfigurableClassWalker.Visitor<>() {
      private final Stack<Map<Discriminator, Command<? extends T>>> subcommandsStack = new Stack<>();

      @Override
      public <M extends T> void enterMultiCommandClass(Discriminator discriminator, String name,
          String description, String version, Class<M> clazz) {
        subcommandsStack.push(new HashMap<>());
      }

      @Override
      public <M extends T> void leaveMultiCommandClass(Discriminator discriminator, String name,
          String description, String version, Class<M> clazz) {
        Map<Discriminator, Command<? extends M>> subcommands = (Map) subcommandsStack.pop();
        MultiCommand<M> multi = new MultiCommand<>(clazz, name, description, version, subcommands);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, multi);
        } else {
          result.set(multi);
        }
      }

      @Override
      public <M extends T> void visitSingleCommandClass(Discriminator discriminator, String name,
          String description, String version, Class<M> clazz) {
        ConfigurableClass<M> configurable = doScanSingleCommand(discriminator, name, description,
            clazz, context);

        MyInstanceFactory<M> instanceFactory = resolveInstanceFactory(configurable, context);

        SingleCommand<M> single = new SingleCommand<>(name, description, version, instanceFactory);
        instanceFactory.setCommand(single);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, single);
        } else {
          result.set(single);
        }
      }
    });

    return (Command<T>) result.get();
  }

  protected static record AttributeDefinition(String name, List<Annotation> annotations,
      Annotation parameterAnnotation, Class<?> rawType, Type genericType) {

  }

  public static class AttributeBucketBuilder {

    private final String name;
    private final List<Annotation> annotations;
    private final Annotation parameterAnnotation;
    private final Class<?> rawType;
    private final Type genericType;
    private final List<InputConfigurableComponent> inputs;
    private final List<FieldConfigurableComponent> fields;
    private final List<GetterConfigurableComponent> getters;
    private final List<SetterConfigurableComponent> setters;

    public static AttributeBucketBuilder fromAttributeDefinition(AttributeDefinition definition) {
      return new AttributeBucketBuilder(definition.name(), definition.annotations(),
          definition.parameterAnnotation(), definition.rawType(), definition.genericType());
    }

    public AttributeBucketBuilder(String name, List<Annotation> annotations,
        Annotation parameterAnnotation, Class<?> rawType, Type genericType) {
      this.name = requireNonNull(name);
      this.annotations = unmodifiableList(annotations);
      this.parameterAnnotation = requireNonNull(parameterAnnotation);
      this.rawType = requireNonNull(rawType);
      this.genericType = requireNonNull(genericType);
      this.inputs = new ArrayList<>();
      this.fields = new ArrayList<>();
      this.getters = new ArrayList<>();
      this.setters = new ArrayList<>();
    }

    public String getName() {
      return name;
    }

    public List<Annotation> getAnnotations() {
      return annotations;
    }

    public Annotation getParameterAnnotation() {
      return parameterAnnotation;
    }

    public Class<?> getRawType() {
      return rawType;
    }

    public Type getGenericType() {
      return genericType;
    }

    public List<InputConfigurableComponent> getInputs() {
      return unmodifiableList(inputs);
    }

    public List<FieldConfigurableComponent> getFields() {
      return unmodifiableList(fields);
    }

    public List<GetterConfigurableComponent> getGetters() {
      return unmodifiableList(getters);
    }

    public List<SetterConfigurableComponent> getSetters() {
      return unmodifiableList(setters);
    }

    public void addInput(InputConfigurableComponent input) {
      inputs.add(input);
    }

    public void addField(FieldConfigurableComponent field) {
      fields.add(field);
    }

    public void addGetter(GetterConfigurableComponent getter) {
      getters.add(getter);
    }

    public void addSetter(SetterConfigurableComponent setter) {
      setters.add(setter);
    }

    public int size() {
      return inputs.size() + fields.size() + getters.size() + setters.size();
    }

    public Stream<ConfigurableComponent> stream() {
      return Streams.concat(inputs.stream(), fields.stream(), getters.stream(), setters.stream());
    }

    public AttributeBucket build() {
      if (size() == 0) {
        // TODO better exception
        throw new IllegalArgumentException("No components for attribute: " + name);
      }
      if (fields.size() > 1) {
        // TODO better exception
        throw new IllegalArgumentException("Multiple fields for attribute: " + name);
      }
      if (inputs.size() > 1) {
        // TODO better exception
        throw new IllegalArgumentException("Multiple inputs for attribute: " + name);
      }
      // TODO We should probably log a warning if we choose a component that was not annotated
      return new AttributeBucket(name, annotations, parameterAnnotation, rawType, genericType,
          MoreIterables.first(inputs).orElse(null), MoreIterables.first(fields).orElse(null),
          MoreIterables.first(getters).orElse(null), MoreIterables.first(setters).orElse(null));
    }
  }

  protected static class AttributeBucket {

    private final String name;
    private final List<Annotation> annotations;
    private final Annotation parameterAnnotation;
    private final Class<?> rawType;
    private final Type genericType;
    private final InputConfigurableComponent input;
    private final FieldConfigurableComponent field;
    private final GetterConfigurableComponent getter;
    private final SetterConfigurableComponent setter;

    public AttributeBucket(String name, List<Annotation> annotations,
        Annotation parameterAnnotation, Class<?> rawType, Type genericType,
        InputConfigurableComponent input, FieldConfigurableComponent field,
        GetterConfigurableComponent getter, SetterConfigurableComponent setter) {
      this.name = requireNonNull(name);
      this.annotations = unmodifiableList(annotations);
      this.parameterAnnotation = requireNonNull(parameterAnnotation);
      this.rawType = requireNonNull(rawType);
      this.genericType = requireNonNull(genericType);

      if (input == null && field == null && getter == null && setter == null) {
        throw new IllegalArgumentException("No components");
      }

      this.input = input;
      this.field = field;
      this.getter = getter;
      this.setter = setter;
    }

    public String getName() {
      return name;
    }

    public List<Annotation> getAnnotations() {
      return annotations;
    }

    public Annotation getParameterAnnotation() {
      return parameterAnnotation;
    }

    public Class<?> getRawType() {
      return rawType;
    }

    public Type getGenericType() {
      return genericType;
    }

    public Optional<InputConfigurableComponent> getInput() {
      return Optional.ofNullable(input);
    }

    public Optional<FieldConfigurableComponent> getField() {
      return Optional.ofNullable(field);
    }

    public Optional<GetterConfigurableComponent> getGetter() {
      return Optional.ofNullable(getter);
    }

    public Optional<SetterConfigurableComponent> getSetter() {
      return Optional.ofNullable(setter);
    }
  }

  protected <M> ConfigurableClass<M> doScanSingleCommand(Discriminator discriminator, String name,
      String description, Class<M> clazz, InvocationContext context) {
    // The first thing we need to do is compute the set of "components" for this class, which is all
    // inputs, fields, getters, and setters. The set of all components for this class could include
    // inputs to the factory. So before we can compute the set of components, we need to choose our
    // factory. This is an abstraction of a constructor or factory method.
    ConfigurableInstanceFactory<M> instanceFactory = context.get(
            InvocationContext.CONFIGURABLE_INSTANCE_FACTORY_PROVIDER_CHAIN_KEY).orElseThrow()
        .scanForInstanceFactory(clazz).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No factory for " + clazz);
        });

    // Now that we have our factory, we can compute the set of non-factory related components. That
    // is, fields, getters, and setters.
    List<ConfigurableComponent> instanceComponents = context.get(
            InvocationContext.CONFIGURABLE_COMPONENT_SCANNER_CHAIN_KEY).orElseThrow()
        .scanForComponents(clazz);

    return new ConfigurableClass<>(clazz, instanceFactory, instanceComponents);
  }

  record AssignableConfigurationParameter(ConfigurationParameter parameter,
      BiConsumer<Object, Object> assigner) {

  }

  protected <M> MyInstanceFactory<M> resolveInstanceFactory(ConfigurableClass<M> clazz,
      InvocationContext context) {

    // Combine them together into one list
    List<ConfigurableComponent> components = clazz.getComponents();

    // Get ready to extract attribute names
    AccessorNamingSchemeChain naming = context.get(
        InvocationContext.ACCESSOR_NAMING_SCHEME_CHAIN_KEY).orElseThrow();

    // Now discover the configuration "attributes," which are the parameters as defined by the user
    // via the various parameter annotations (e.g., @FlagParameter, @OptionParameter, etc).
    List<AttributeDefinition> anchors = new ArrayList<>();
    for (ConfigurableComponent component : components) {
      Annotation parameterAnnotation = ParameterAnnotations.findParameterAnnotation(
          component.getAnnotations()).orElse(null, () -> {
        throw new TooManyAnnotationsConfigurationException(component.getName());
      });
      if (parameterAnnotation == null) {
        // This component is not annotated to be a parameter, which is just fine.
        continue;
      }

      String attributeName = attributeName(naming, component).orElseThrow(() -> {
        // TODO better exception
        return new IllegalArgumentException("No attribute name for " + component);
      });

      anchors.add(
          new AttributeDefinition(attributeName, component.getAnnotations(), parameterAnnotation,
              component.getRawType(), component.getGenericType()));
    }
    Streams.duplicates(anchors.stream()).findFirst().ifPresent(duplicate -> {
      // TODO better exception
      throw new IllegalArgumentException("Duplicate attribute name: " + duplicate.name());
    });

    // The annotated components are the "anchors" for each attribute that defines their names and
    // types, but may not be the actual components that are used to assign the attribute. So now we
    // need to collect all the components that are relevant to each attribute to identify how each
    // attribute is assigned.
    List<AttributeBucketBuilder> bucketBuilders = anchors.stream()
        .map(AttributeBucketBuilder::fromAttributeDefinition).toList();
    for (AttributeBucketBuilder bucketBuilder : bucketBuilders) {
      final String attributeName = bucketBuilder.getName();
      for (ConfigurableComponent component : components) {
        // For each component, for each attribute, if the component is relevant to the attribute,
        // then add it to the bucket.
        if (component instanceof FieldConfigurableComponent field) {
          if (naming.isAttributeFieldFor(attributeName, field.getName(), field.getAnnotations())
              .orElse(false)) {
            bucketBuilder.addField(field);
          }
        } else if (component instanceof GetterConfigurableComponent getter) {
          if (naming.isAttributeGetterFor(attributeName, getter.getName(), getter.getAnnotations())
              .orElse(false)) {
            bucketBuilder.addGetter(getter);
          }
        } else if (component instanceof SetterConfigurableComponent setter) {
          if (naming.isAttributeSetterFor(attributeName, setter.getName(), setter.getAnnotations())
              .orElse(false)) {
            bucketBuilder.addSetter(setter);
          }
        } else if (component instanceof InputConfigurableComponent input) {
          if (naming.isAttributeInputFor(attributeName, input.getName(), input.getAnnotations())
              .orElse(false)) {
            bucketBuilder.addInput(input);
          }
        }
      }
    }

    // Now that we have all the components relevant to each attribute, we need to ensure that each
    // component is assigned to at most one attribute.
    Streams.duplicates(bucketBuilders.stream()).findFirst().ifPresent(duplicate -> {
      // TODO better exception
      throw new IllegalArgumentException(
          "component assigned to multiple attributes: " + duplicate.getName());
    });

    // Let's build our buckets now
    List<AttributeBucket> buckets = bucketBuilders.stream().map(AttributeBucketBuilder::build)
        .toList();

    List<ConfigurationParameter> inputParameters = buckets.stream()
        .filter(bucket -> bucket.getInput().isPresent())
        .map(bucket -> toConfigurationParameter(bucket, context)).toList();

    List<AssignableConfigurationParameter> setterParameters = buckets.stream()
        .filter(bucket -> bucket.getInput().isEmpty())
        .<AssignableConfigurationParameter>mapMulti((bucket, downstream) -> {
          bucket.getSetter().flatMap(SetterConfigurableComponent::getSetter).map(
              setter -> new AssignableConfigurationParameter(
                  toConfigurationParameter(bucket, context), setter)).ifPresent(downstream);
        }).toList();

    List<AssignableConfigurationParameter> fieldParameters = buckets.stream()
        .filter(bucket -> bucket.getInput().isEmpty())
        .filter(bucket -> bucket.getSetter().isEmpty())
        .<AssignableConfigurationParameter>mapMulti((bucket, downstream) -> {
          bucket.getField().flatMap(FieldConfigurableComponent::getSetter).map(
              setter -> new AssignableConfigurationParameter(
                  toConfigurationParameter(bucket, context), setter)).ifPresent(downstream);
        }).toList();

    return new MyInstanceFactory<>(clazz, inputParameters, setterParameters, fieldParameters);
  }

  private static ConfigurationParameter toConfigurationParameter(
      InvocationBuilder.AttributeBucket bucket, InvocationContext context) {
    ValueSink sink = context.get(InvocationContext.VALUE_SINK_FACTORY_CHAIN_KEY).orElseThrow()
        .getSink(bucket.getGenericType(), bucket.getAnnotations());

    ValueDeserializer<?> deserializer = context.get(
            InvocationContext.VALUE_DESERIALIZER_FACTORY_CHAIN_KEY).orElseThrow()
        .getDeserializer(sink.getGenericType(), bucket.getAnnotations()).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No deserializer for " + sink.getGenericType());
        });

    return ConfigurationParameters.createConfigurationParameter(bucket.getParameterAnnotation(),
        bucket.getName(), deserializer, sink);
  }

  private static Optional<String> attributeName(AccessorNamingSchemeChain naming,
      ConfigurableComponent component) {
    if (component instanceof InputConfigurableComponent input) {
      // TODO handle factory methods
      return naming.getAttributeConstructorParameterName(input.getAnnotations(), input.getName(),
          input.getAnnotations());
    } else if (component instanceof FieldConfigurableComponent field) {
      return naming.getAttributeFieldName(field.getName(), field.getAnnotations());
    } else if (component instanceof GetterConfigurableComponent getter) {
      return naming.getAttributeGetterName(getter.getName(), getter.getAnnotations());
    } else if (component instanceof SetterConfigurableComponent setter) {
      return naming.getAttributeSetterName(setter.getName(), setter.getAnnotations());
    }
    return Optional.empty();
  }

  protected static class MyInstanceFactory<M> implements SingleCommand.InstanceFactory<M> {

    private final ConfigurableClass<M> clazz;
    private final List<ConfigurationParameter> inputParameters;
    private final List<AssignableConfigurationParameter> setterParameters;
    private final List<AssignableConfigurationParameter> fieldParameters;
    protected SingleCommand<M> command;

    public MyInstanceFactory(ConfigurableClass<M> clazz,
        List<ConfigurationParameter> inputParameters,
        List<AssignableConfigurationParameter> setterParameters,
        List<AssignableConfigurationParameter> fieldParameters) {
      this.clazz = clazz;
      this.inputParameters = inputParameters;
      this.setterParameters = setterParameters;
      this.fieldParameters = fieldParameters;
    }

    @Override
    public Class<M> getRawType() {
      return clazz.getClazz();
    }

    @Override
    public List<ConfigurationParameter> getParameters() {
      return Streams.concat(inputParameters.stream(),
          setterParameters.stream().map(AssignableConfigurationParameter::parameter),
          fieldParameters.stream().map(AssignableConfigurationParameter::parameter)).toList();
    }

    @Override
    public M createInstance(Map<String, Object> arguments) {
      List<ConfigurationParameter> parameters = getParameters();
      Set<String> requiredParameters = parameters.stream()
          .filter(ConfigurationParameter::isRequired).map(ConfigurationParameter::getName)
          .collect(toSet());
      Set<String> providedArguments = arguments.keySet();
      if (!providedArguments.containsAll(requiredParameters)) {
        // TODO better exception
        throw new RequiredParametersMissingSyntaxException(getCommand(),
            MoreSets.difference(requiredParameters, providedArguments));
      }

      // TODO We should probably log a warning if we have arguments that are not parameters

      M instance;
      try {
        instance = clazz.getInstanceFactory().createInstance(arguments);
      } catch (Exception e) {
        Exception cause = e;
        while (cause.getCause() instanceof ReflectiveOperationException roe) {
          cause = roe;
        }
        throw new NewInstanceFailureBeanException(getCommand(), cause);
      }
      for (AssignableConfigurationParameter parameter : setterParameters) {
        String parameterName = parameter.parameter().getName();
        if (arguments.containsKey(parameterName)) {
          try {
            parameter.assigner().accept(instance, arguments.get(parameterName));
          } catch (Exception e) {
            Exception cause = e;
            while (cause.getCause() instanceof ReflectiveOperationException roe) {
              cause = roe;
            }
            throw new AssignmentFailureBeanException(getCommand(), parameterName, cause);
          }
        }
      }
      for (AssignableConfigurationParameter parameter : fieldParameters) {
        String parameterName = parameter.parameter().getName();
        if (arguments.containsKey(parameterName)) {
          try {
            parameter.assigner().accept(instance, arguments.get(parameterName));
          } catch (Exception e) {
            Exception cause = e;
            while (cause.getCause() instanceof ReflectiveOperationException roe) {
              cause = roe;
            }
            throw new AssignmentFailureBeanException(getCommand(), parameterName, cause);
          }
        }
      }

      return instance;
    }

    protected void setCommand(SingleCommand<M> command) {
      if (this.command != null) {
        throw new IllegalStateException("command already set");
      }
      this.command = requireNonNull(command);
    }

    private SingleCommand<M> getCommand() {
      if (command == null) {
        throw new IllegalStateException("command not set");
      }
      return command;
    }
  }
}
