package com.sigpwned.discourse.core.configuration;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.phase.parse.model.ParsedArgument;
import com.sigpwned.discourse.core.phase.parse.model.ParsedArguments;
import com.sigpwned.discourse.core.model.coordinate.SwitchNameCoordinate;
import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Configuration {

  private final List<ConfigurationParameter> parameters;

  public Configuration(List<ConfigurationParameter> parameters) {
    this.parameters = unmodifiableList(parameters);
    if (Streams.duplicates(parameters.stream().map(ConfigurationParameter::name)).findAny()
        .isPresent()) {
      throw new IllegalArgumentException("Duplicate parameter names");
    }
  }

  public ConfigurationArguments parse(List<String> args,
      ValueDeserializerFactory<?> deserializerFactory) {
    Set<SwitchNameCoordinate> flags = getParameters().stream()
        .filter(p -> p.genericType().equals(boolean.class) || p.genericType().equals(Boolean.class))
        .flatMap(p -> p.coordinates().stream())
        .mapMulti(Streams.filterAndCast(SwitchNameCoordinate.class)).collect(Collectors.toSet());
    Set<SwitchNameCoordinate> options = getParameters().stream().filter(
            p -> !p.genericType().equals(boolean.class) && !p.genericType().equals(Boolean.class))
        .flatMap(p -> p.coordinates().stream())
        .mapMulti(Streams.filterAndCast(SwitchNameCoordinate.class)).collect(Collectors.toSet());

    ParsedArguments parsedArguments = ParsedArguments.fromArgs(flags, options, args);

    Map<String, List<?>> deserialized = new HashMap<>();
    for (ParsedArgument parsedArgument : parsedArguments) {
      // TODO match collections for positional parameters
      ConfigurationParameter parameter = getParameters().stream()
          .filter(p -> p.coordinates().contains(parsedArgument.coordinate())).findFirst()
          .orElseThrow(() -> {
            // TODO better exception
            // This is probably positional
            return new IllegalArgumentException(
                "No parameter found for coordinate " + parsedArgument.coordinate());
          });
      List values = deserialized.computeIfAbsent(parameter.name(), k -> new ArrayList<>());
      ValueDeserializer<?> deserializer = deserializerFactory.getDeserializer(
          parameter.genericType(), parameter.annotations());
      values.add(deserializer.deserialize(parsedArgument.value()));
    }

    getParameters().stream().filter(ConfigurationParameter::required)
        .filter(p -> !deserialized.containsKey(p.name())).findAny().ifPresent(p -> {
          // TODO better exception
          throw new IllegalArgumentException("Missing required parameter " + p.name());
        });

    return new ConfigurationArguments(deserialized);
  }

  private List<ConfigurationParameter> getParameters() {
    return parameters;
  }
}
