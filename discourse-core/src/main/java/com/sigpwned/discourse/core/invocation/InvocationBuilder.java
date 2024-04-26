package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.AccessorNamingSchemeChain;
import com.sigpwned.discourse.core.ConfigurableClass;
import com.sigpwned.discourse.core.ConfigurableClassWalker;
import com.sigpwned.discourse.core.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.ConfigurableInstanceFactoryProviderChain;
import com.sigpwned.discourse.core.ConfigurableParameterScannerChain;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.FieldConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.GetterConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.SetterConfigurableComponent;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.VariableNameCoordinate;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.MoreIterables;
import com.sigpwned.discourse.core.util.ParameterAnnotations;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class InvocationBuilder {

  public <T> InvocationBuilderResolveStep<T> scan(Class<T> rawType, InvocationContext context) {

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listener -> {
      listener.beforeScan(rawType);
    });

    Command<T> command = doScan(rawType, context);

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listener -> {
      listener.afterScan(command);
    });

    return new InvocationBuilderResolveStep<>(command);
  }

  /**
   * extension hook
   */
  protected <T> Command<T> doScan(Class<T> rawType, InvocationContext context) {
    final ConfigurableInstanceFactoryProviderChain factoryProviderChain = context.get(
        InvocationContext.CONFIGURABLE_INSTANCE_FACTORY_PROVIDER_CHAIN_KEY).orElseThrow();

    final ConfigurableParameterScannerChain parameterScannerChain = context.get(
        InvocationContext.CONFIGURABLE_PARAMETER_SCANNER_CHAIN_KEY).orElseThrow();

    final AtomicReference<Command<? extends T>> result = new AtomicReference<>();

    new ConfigurableClassWalker<>(rawType).walk(new ConfigurableClassWalker.Visitor<>() {
      private final Stack<Map<Discriminator, Command<? extends T>>> subcommandsStack = new Stack<>();

      @Override
      public <M extends T> void enterMultiCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        subcommandsStack.push(new HashMap<>());
      }

      @Override
      public <M extends T> void leaveMultiCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        Map<Discriminator, Command<? extends M>> subcommands = (Map) subcommandsStack.pop();
        MultiCommand<M> multi = new MultiCommand<M>(clazz, name, description, "1.0", subcommands);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, multi);
        } else {
          result.set(multi);
        }
      }

      @Override
      public <M extends T> void visitSingleCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        ConfigurableInstanceFactory<M> factory = factoryProviderChain.getConfigurationInstanceFactory(
            clazz).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No factory for " + rawType);
        });

        ConfigurableParameterScannerChain parameterScannerChain = null;
        List<ConfigurationParameter> parameters = parameterScannerChain.scanForParameters(clazz);
        if (parameters.isEmpty()) {
          // TODO Warn
        }

        SingleCommand<M> single = new SingleCommand<>(name, description, "1.0", parameters, null);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, single);
        } else {
          result.set(single);
        }
      }
    });

    return (Command<T>) result.get();
  }

  protected static record AttributeDefinition(String name, Annotation parameterAnnotation,
      Class<?> rawType, Type genericType) {

  }


  protected static class AttributeBucketBuilder {

    private final String name;
    private final Annotation annotation;
    private final Class<?> rawType;
    private final Type genericType;
    private final List<InputConfigurableComponent> inputs;
    private final List<FieldConfigurableComponent> fields;
    private final List<GetterConfigurableComponent> getters;
    private final List<SetterConfigurableComponent> setters;

    public static AttributeBucketBuilder fromAttributeDefinition(AttributeDefinition definition) {
      return new AttributeBucketBuilder(definition.name(), definition.parameterAnnotation(),
          definition.rawType(), definition.genericType());
    }

    public AttributeBucketBuilder(String name, Annotation annotation, Class<?> rawType,
        Type genericType) {
      this.name = name;
      this.annotation = annotation;
      this.rawType = rawType;
      this.genericType = genericType;
      this.inputs = new ArrayList<>();
      this.fields = new ArrayList<>();
      this.getters = new ArrayList<>();
      this.setters = new ArrayList<>();
    }

    public String getName() {
      return name;
    }

    public Annotation getAnnotation() {
      return annotation;
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
      return new AttributeBucket(name, annotation, rawType, genericType,
          MoreIterables.first(inputs).orElse(null), MoreIterables.first(fields).orElse(null),
          MoreIterables.first(getters).orElse(null), MoreIterables.first(setters).orElse(null));
    }
  }

  protected static class AttributeBucket {

    private final String name;
    private final Annotation annotation;
    private final Class<?> rawType;
    private final Type genericType;
    private final InputConfigurableComponent input;
    private final FieldConfigurableComponent field;
    private final GetterConfigurableComponent getter;
    private final SetterConfigurableComponent setter;

    public AttributeBucket(String name, Annotation annotation, Class<?> rawType, Type genericType,
        InputConfigurableComponent input, FieldConfigurableComponent field,
        GetterConfigurableComponent getter, SetterConfigurableComponent setter) {
      this.name = requireNonNull(name);
      this.annotation = requireNonNull(annotation);
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

    public Annotation getAnnotation() {
      return annotation;
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
        .getConfigurationInstanceFactory(clazz).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No factory for " + clazz);
        });

    // Now that we have our factory, we can compute the set of non-factory related components. That
    // is, fields, getters, and setters.
    List<ConfigurableComponent> instanceComponents = context.get(
            InvocationContext.CONFIGURABLE_COMPONENT_SCANNER_CHAIN_KEY).orElseThrow()
        .scanForComponents(clazz, context);

    return new ConfigurableClass<>(clazz, instanceFactory, instanceComponents);
  }

  protected <M> SingleCommand.InstanceFactory<M> resolveInstanceFactory(ConfigurableClass<M> clazz,
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
        // TODO better exception
        return new IllegalArgumentException("Multiple parameter annotations for " + component);
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
          new AttributeDefinition(attributeName, parameterAnnotation, component.getRawType(),
              component.getGenericType()));
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
      for (ConfigurableComponent component : components) {
        // For each component, for each attribute, check if the component is relevant to the attribute.
        if (!component.getName().equals(bucketBuilder.getName())) {
          continue;
        }

        // If the component is relevant to the attribute, add it to the bucket.
        if (component instanceof FieldConfigurableComponent field) {
          bucketBuilder.addField(field);
        } else if (component instanceof GetterConfigurableComponent getter) {
          bucketBuilder.addGetter(getter);
        } else if (component instanceof SetterConfigurableComponent setter) {
          bucketBuilder.addSetter(setter);
        } else if (component instanceof InputConfigurableComponent input) {
          bucketBuilder.addInput(input);
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

    // Let's get our buckets now
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

    return new SingleCommand.InstanceFactory<>() {
      @Override
      public List<ConfigurationParameter> getParameters() {
        return Streams.concat(inputParameters.stream(),
            setterParameters.stream().map(AssignableConfigurationParameter::parameter),
            fieldParameters.stream().map(AssignableConfigurationParameter::parameter)).toList();
      }

      @Override
      public M createInstance(Map<String, Object> arguments) {
        M instance = clazz.getInstanceFactory().createInstance(arguments);
        for (AssignableConfigurationParameter parameter : setterParameters) {
          parameter.assigner().accept(instance, arguments.get(parameter.parameter().getName()));
        }
        for (AssignableConfigurationParameter parameter : fieldParameters) {
          parameter.assigner().accept(instance, arguments.get(parameter.parameter().getName()));
        }
        return instance;
      }
    };
  }

  protected static record AssignableConfigurationParameter(ConfigurationParameter parameter,
      BiConsumer<Object, Object> assigner) {

  }

  private static ConfigurationParameter toConfigurationParameter(AttributeBucket bucket,
      InvocationContext context) {
    ValueDeserializer<?> deserializer = context.get(
            InvocationContext.VALUE_DESERIALIZER_RESOLVER_KEY).orElseThrow()
        .resolveValueDeserializer(bucket.getGenericType(), null).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No deserializer for " + bucket.getRawType());
        });

    ValueSink sink = context.get(InvocationContext.VALUE_SINK_RESOLVER_KEY).orElseThrow()
        .resolveValueSink(bucket.getGenericType(), null);

    if (bucket.getAnnotation() instanceof FlagParameter flag) {
      ShortSwitchNameCoordinate shortName = Optional.ofNullable(flag.shortName())
          .map(ShortSwitchNameCoordinate::new).orElse(null);
      LongSwitchNameCoordinate longName = Optional.ofNullable(flag.longName())
          .map(LongSwitchNameCoordinate::new).orElse(null);
      if (shortName == null && longName == null) {
        // TODO better exception
        throw new IllegalArgumentException("no names");
      }
      return new FlagConfigurationParameter(bucket.getName(), flag.description(), deserializer,
          sink, shortName, longName, flag.help(), flag.version());
    } else if (bucket.getAnnotation() instanceof OptionParameter option) {
      ShortSwitchNameCoordinate shortName = Optional.ofNullable(option.shortName())
          .map(ShortSwitchNameCoordinate::new).orElse(null);
      LongSwitchNameCoordinate longName = Optional.ofNullable(option.longName())
          .map(LongSwitchNameCoordinate::new).orElse(null);
      if (shortName == null && longName == null) {
        // TODO better exception
        throw new IllegalArgumentException("no names");
      }
      return new OptionConfigurationParameter(bucket.getName(), option.description(),
          option.required(), deserializer, sink, shortName, longName);
    } else if (bucket.getAnnotation() instanceof PositionalParameter positional) {
      PositionCoordinate position = new PositionCoordinate(positional.position());
      return new PositionalConfigurationParameter(bucket.getName(), positional.description(),
          positional.required(), deserializer, sink, position);
    } else if (bucket.getAnnotation() instanceof PropertyParameter property) {
      PropertyNameCoordinate propertyName = PropertyNameCoordinate.fromString(
          property.propertyName());
      return new PropertyConfigurationParameter(bucket.getName(), property.description(),
          property.required(), deserializer, sink, propertyName);
    } else if (bucket.getAnnotation() instanceof EnvironmentParameter environment) {
      VariableNameCoordinate variableName = VariableNameCoordinate.fromString(
          environment.variableName());
      return new EnvironmentConfigurationParameter(bucket.getName(), environment.description(),
          environment.required(), deserializer, sink, variableName);
    }

    throw new IllegalArgumentException("Unknown parameter annotation: " + bucket.getAnnotation());
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
}
