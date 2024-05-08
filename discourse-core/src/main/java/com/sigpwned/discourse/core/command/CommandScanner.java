package com.sigpwned.discourse.core.command;

import static com.sigpwned.discourse.core.phase2.FooFactory.politeAssemblyStep;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.accessor.naming.AccessorNamingScheme;
import com.sigpwned.discourse.core.command.attribute.AttributeScanner;
import com.sigpwned.discourse.core.configurable.CandidateConfigurableComponent;
import com.sigpwned.discourse.core.configurable.ConfigurableClass;
import com.sigpwned.discourse.core.configurable.ConfigurableClassScanner;
import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.ConfigurableComponentFactory;
import com.sigpwned.discourse.core.configurable.ConfigurableSink;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponentComparator;
import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import com.sigpwned.discourse.core.configurable.component.scanner.ConfigurableCandidateComponentScanner;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.exception.bean.AssignmentFailureBeanException;
import com.sigpwned.discourse.core.exception.bean.NewInstanceFailureBeanException;
import com.sigpwned.discourse.core.exception.syntax.RequiredParametersMissingSyntaxException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.phase2.FooAttribute;
import com.sigpwned.discourse.core.phase2.FooFactory;
import com.sigpwned.discourse.core.util.ConfigurationParameters;
import com.sigpwned.discourse.core.util.MoreLists;
import com.sigpwned.discourse.core.util.MoreSets;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommandScanner {

  public static record ComponentElement(ConfigurableComponent component,
      ConfigurableElement element) {
  }

  public static record NamedComponentElement(String name, ConfigurableComponent component,
      ConfigurableElement element) {

  }

  public static record AttributeAnchor(String name, Type genericType,
      Map<Object, String> coordinates) {

  }

  public static record ComponentSink(ConfigurableComponent component, ConfigurableSink sink) {

  }


  protected static class AttributeBucketBuilder {

    public static AttributeBucketBuilder fromAttributeAnchor(AttributeAnchor anchor) {
      return new AttributeBucketBuilder(anchor.name(), anchor.genericType(), anchor.coordinates());
    }

    private final String name;
    private final Type genericType;
    private final Map<Object, String> coordinates;
    private final List<NamedComponentElement> components;

    public AttributeBucketBuilder(String name, Type genericType, Map<Object, String> coordinates) {
      this.name = name;
      this.genericType = genericType;
      this.coordinates = coordinates;
      this.components = new ArrayList<>();
    }

    public String getName() {
      return name;
    }

    public Type getGenericType() {
      return genericType;
    }

    public List<NamedComponentElement> getComponents() {
      return unmodifiableList(components);
    }

    public void addComponent(NamedComponentElement component) {
      if (!component.name().equals(name)) {
        // TODO better exception
        throw new IllegalArgumentException("component name must match attribute name");
      }
      components.add(component);
    }

    public Map<Object, String> getCoordinates() {
      return coordinates;
    }

    public int size() {
      return components.size();
    }

    public Stream<NamedComponentElement> stream() {
      return components.stream();
    }

    public AttributeBucket build() {
      if (size() == 0) {
        // TODO better exception
        throw new IllegalArgumentException("No components for attribute: " + name);
      }
      // TODO We should probably log a warning if we choose a component that was not annotated
      return new AttributeBucket(name, genericType, coordinates, components);
    }
  }

  protected static class AttributeBucket {

    private final String name;
    private final Type genericType;
    private final Map<Object, String> coordinates;
    private final List<NamedComponentElement> components;

    public AttributeBucket(String name, Type genericType, Map<Object, String> coordinates,
        List<NamedComponentElement> components) {
      this.name = requireNonNull(name);
      this.genericType = requireNonNull(genericType);
      this.coordinates = coordinates;
      this.components = components;
    }

    public String getName() {
      return name;
    }

    public Type getGenericType() {
      return genericType;
    }

    public Map<Object, String> getCoordinates() {
      return coordinates;
    }

    public List<NamedComponentElement> getComponents() {
      return components;
    }

    public List<NamedComponentElement> getSinks() {
      return getComponents().stream().filter(nce -> nce.component().isSink()).toList();
    }
  }

  private final ConfigurableClassScanner configurableScanner;
  private final AccessorNamingScheme accessorNamingScheme;
  private final AttributeScanner attributeScanner;

  public <T> Command<T> scan(Class<T> clazz) {
    // TODO how do we do the ignore?
    ConfigurableCandidateComponentScanner candidateComponentScanner = getCandidateComponentScanner();
    List<CandidateConfigurableComponent> candidateComponents = candidateComponentScanner.scanForCandidateComponents(
        clazz).stream().distinct().toList();

    // TODO how do we do the naming?
    ConfigurableComponentFactory componentFactory = getComponentFactory();
    List<ConfigurableComponent> components = candidateComponents.stream().flatMap(
        candidateComponent -> componentFactory.createConfigurableComponent(candidateComponent)
            .stream()).toList();

    components.stream().flatMap(component -> component.getSinks().stream()
            .map(sink -> new ComponentSink(component, sink)))
        .filter(cs -> !cs.sink().getCoordinates().isEmpty())
        .map(cs -> new FooAttribute(cs.sink().getName(), cs.sink().getCoordinates(),
            component.component().getMapper(), component.component().getReducer()))
        .toList();

    Map<String,Object> args=Map.of();



    ConfigurableClass<T> configurableClazz = getConfigurableScanner().scan(clazz);

    // Combine them together into one list
    List<ConfigurableComponent> components = configurableClazz.getComponents();

    // Unpack the elements
    List<ComponentElement> elements = components.stream().flatMap(
        component -> component.getSinks().stream()
            .map(element -> new ComponentElement(component, element))).toList();

    // Name them
    ConfigurableComponentElementNamingScheme namingScheme = getNamingScheme();
    List<NamedComponentElement> nameds = elements.stream().map(element -> {
      String name = namingScheme.nameConfigurableElement(element.component(), element.element())
          .orElse(null);
      if (name == null) {
        // TODO better exception
        throw new IllegalArgumentException("No name for element");
      }
      return new NamedComponentElement(name, element.component(), element.element());
    }).toList();

    // Now discover the configuration "attributes," which are the parameters as defined by the user
    // via the various parameter annotations (e.g., @FlagParameter, @OptionParameter, etc).
    AttributeScanner attributeScanner = getAttributeScanner();
    List<AttributeAnchor> anchors = nameds.stream().flatMap(named -> {
      String name = named.name();
      ConfigurableComponent component = named.component();
      ConfigurableElement element = named.element();
      return attributeScanner.scanForAttributes(name, component, element).stream().map(
          metadata -> new AttributeAnchor(name, element.getGenericType(), metadata.coordinates()));
    }).toList();

    // TODO Should we be able to define the instance attribute more than once?
    // We aren't defining the same attribute more than once, are we?
    Streams.duplicates(anchors.stream().map(AttributeAnchor::name)).findFirst()
        .ifPresent(duplicateName -> {
          // TODO better exception
          throw new IllegalArgumentException("Duplicate attribute name: " + duplicateName);
        });

    // Did we define the instance attribute?
    if (anchors.stream()
        .noneMatch(anchor -> anchor.name().equals(ConfigurableElement.INSTANCE_NAME))) {
      // TODO better exception
      throw new IllegalArgumentException("No instance attribute");
    }

    // TODO Should we be able to define the instance coordinate more than once?
    // We aren't defining the same coordinate more than once, are we?
    Streams.duplicates(anchors.stream().flatMap(m -> m.coordinates().keySet().stream())).findFirst()
        .ifPresent(duplicateCoordinate -> {
          // TODO better exception
          throw new IllegalArgumentException(
              "Duplicate attribute coordinate: " + duplicateCoordinate);
        });

    // Did we define the instance coordinate?
    if (anchors.stream()
        .noneMatch(anchor -> anchor.coordinates().containsKey(ConfigurableElement.INSTANCE_NAME))) {
      // TODO better location for instance coordinate
      // TODO better exception
      throw new IllegalArgumentException("No instance coordinate");
    }

    // The annotated components are the "anchors" for each attribute that defines their names and
    // types, but may not be the actual components that are used to assign the attribute. So now we
    // need to collect all the components that are relevant to each attribute to identify how each
    // attribute is assigned.
    List<AttributeBucketBuilder> bucketBuilders = anchors.stream()
        .map(AttributeBucketBuilder::fromAttributeAnchor).toList();
    for (AttributeBucketBuilder bucketBuilder : bucketBuilders) {
      for (NamedComponentElement named : nameds) {
        if (named.name().equals(bucketBuilder.getName())) {
          bucketBuilder.addComponent(named);
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

    // OK. At this point, we have two kinds of attributes: attributes WITH coordinates, and
    // attributes WITHOUT coordinates. The attributes WITH coordinates are expected to be given
    // directly by the user. The attributes WITHOUT coordinates are expected to be derived from
    // the attributes WITH coordinates. So the attributes WITH coordinates will end up in
    // in FooFactory as FooAttribute instances, and the attributes WITHOUT coordinates will end up
    // in FooFactory as assembly steps. We order the assembly steps by the length of the attribute
    // name descending as a convention, simply because that guarantees that the instance attribute
    // (which should have name "") is the last assembly step.
    List<AttributeBucket> direct = buckets.stream()
        .filter(bucket -> !bucket.getCoordinates().isEmpty()).toList();
    List<AttributeBucket> derived = buckets.stream()
        .filter(bucket -> bucket.getCoordinates().isEmpty()).toList();

    // Need a SinkFactory that creates sinks. This returns a reducer function and Type element type.
    // Need a MapperFactory that creates mappers. This returns a function to map a string to an object.
    // This is Copilot Generated code. Need to review.
    List<FooAttribute> attributes = direct.stream().map(bucket -> {
      String name = bucket.getName();
      Type genericType = bucket.getGenericType();
      Map<Object, String> coordinates = bucket.getCoordinates();
      Function<String, Object> mapper = bucket.getComponents().stream()
          .map(named -> named.component().getMapper())
          .reduce(Function.identity(), Function::andThen);
      Function<List<Object>, Object> reducer = bucket.getComponents().stream()
          .map(named -> named.component().getReducer())
          .reduce(Function.identity(), Function::andThen);
      return new FooAttribute(name, genericType, coordinates, mapper, reducer);
    }).toList();

    // This is Copilot Generated code. Need to review.
    List<Consumer<Map<String, Object>>> assemblySteps = derived.stream()
        .sorted(Comparator.comparing(AttributeBucket::getName, Comparator.reverseOrder()))
        .map(bucket -> {
          String name = bucket.getName();
          List<NamedComponentElement> components = bucket.getComponents();
          Set<String> expectedArgNames = components.stream().map(NamedComponentElement::name)
              .collect(toSet());
          Function<Map<String, Object>, Object> factoryFunction = arguments -> {
            Map<String, Object> expectedArgs = components.stream()
                .collect(toUnmodifiableMap(NamedComponentElement::name, named -> {
                  ConfigurableComponent component = named.component();
                  ConfigurableElement element = named.element();
                  return component.getMapper().apply(arguments.get(element.getName()));
                }));
            return bucket.getReducer().apply(expectedArgs.values());
          };
          return politeAssemblyStep(expectedArgNames, factoryFunction);
        }).toList();

    // When we create a new component type, you have to BYO:
    // - Object representation. See FieldConfigurableComponent, GetterConfigurableComponent, etc.
    // - Default naming strategy. See GetterConfigurableComponent.
    // - Default assignment strategy. See FieldConfigurableComponent.
    // The assignment strategy should be: BiFunction<Object,Map<String,Object>,Object> The first
    // argument is the object to mutate, the second argument is the map of arguments, and the return
    // value is the object that was mutated. In most cases, the return value will simply be the
    // first parameter, but in some cases, it may be a new object, for example after creating the
    // object with a constructor. The map is also mutable, and these functions can write to the map.
    // The naming strategy should be to use the object's field, getter, or setter name.

    // Now we need to figure out which combination of sinks works to satisfy each attribute. The
    // direct attributes are automatically generated by the pipeline, but we still need to worry
    // about assignment them. The derived attributes are not generated by the pipeline, so we need
    // to worry about generating them as well as assigning them. We prefer the shortest combination
    // of sinks that satisfies all the attributes. If there are multiple combinations of sinks that
    // satisfy all the attributes, we prefer the combination with the fewest sinks.
    Set<String> spanned = Set.of();
    List<ConfigurableComponent> spanner = List.of();
    Map<String, List<ConfigurableComponent>> attributeSinks = buckets.stream().flatMap(
            bucket -> bucket.getSinks().stream().map(sink -> Map.entry(bucket.getName(), sink)))
        .distinct().collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));
    Map<ConfigurableComponent, Set<String>> sinkAttributes = attributeSinks.entrySet().stream()
        .flatMap(entry -> entry.getValue().stream().map(sink -> Map.entry(sink, entry.getKey())))
        .distinct()
        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toUnmodifiableSet())));
    try (Stream<List<ConfigurableComponent>> permutations = MoreLists.cartesianProduct(
        List.copyOf(attributeSinks.values())).stream().map(xs -> xs.stream().distinct().toList())) {
      Iterator<List<ConfigurableComponent>> iterator = permutations.iterator();
      while (iterator.hasNext()) {
        List<ConfigurableComponent> permutation = iterator.next();

        Set<String> attributes = permutation.stream().flatMap(c -> sinkAttributes.get(c).stream())
            .collect(toSet());

        if (attributes.size() > spanned.size()) {
          // This permutation is better than what we have.
          spanner = permutation;
          spanned = attributes;
        } else if (attributes.size() == spanned.size() && permutation.size() < spanner.size()) {
          // This permutation is better than what we have.
          spanner = permutation;
          spanned = attributes;
        } else {
          // This permutation is at best no better than what we have, and maybe worse.
        }
      }
    }
    if (!spanned.containsAll(attributeSinks.keySet())) {
      // TODO better exception
      throw new IllegalArgumentException(
          "No sink for attribute: " + MoreSets.difference(attributeSinks.keySet(), spanned));
    }

    List<ConfigurableComponent> sinks = spanner.stream()
        .sorted(ConfigurableComponentComparator.INSTANCE).toList();
    // Map<String,List<Object>> + name --> Object + name --> Map<String,Object>
    // Run instance last

    return new MyInstanceFactory<>(clazz, sinks);

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
  }

  private static ConfigurationParameter toConfigurationParameter(AttributeBucket bucket) {
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

  private Optional<String> attributeName(ConfigurableElement element) {

  }

  protected static class MyInstanceFactory<M> implements SingleCommand.InstanceFactory<M> {

    private final ConfigurableClass<M> clazz;
    private final List<ConfigurableComponent> sinks;

    public MyInstanceFactory(ConfigurableClass<M> clazz, List<ConfigurableComponent> sinks) {
      this.clazz = clazz;
      this.sinks = unmodifiableList(sinks);
    }

    @Override
    public Class<M> getRawType() {
      return clazz.getClazz();
    }

    @Override
    public List<ConfigurableComponent> getParameters() {
      return sinks;
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

  private ConfigurableClassScanner getConfigurableScanner() {
    return configurableScanner;
  }

  private AccessorNamingScheme getAccessorNamingScheme() {
    return accessorNamingScheme;
  }
}
