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
package com.sigpwned.discourse.core.invocation.strategy;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import com.sigpwned.discourse.core.ArgumentException;
import com.sigpwned.discourse.core.ArgumentsParser;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.VariableNameCoordinate;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.invocation.DefaultInvocation;
import com.sigpwned.discourse.core.optional.OptionalEnvironmentVariable;
import com.sigpwned.discourse.core.optional.OptionalSystemProperty;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.espresso.BeanInstance;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An {@link com.sigpwned.discourse.core.InvocationStrategy} that takes a single command and its
 * arguments and constructs an instance of the command with the arguments.
 */
public class SingleCommandInvocationStrategy implements InvocationStrategy {

  @FunctionalInterface
  /* default */ static interface EnvironmentVariables {

    public OptionalEnvironmentVariable<String> get(String name);
  }

  @FunctionalInterface
  /* default */ static interface SystemProperties {

    public OptionalSystemProperty<String> get(String name);
  }

  /**
   * A command-line argument that has been parsed and resolved to a {@link ConfigurationParameter}.
   *
   * @param coordinate the coordinate of the argument
   * @param parameter  the parameter that the argument corresponds to
   * @param text       the text of the argument, exactly as given on the command-line, environment
   *                   variable, or system property. may be null.
   */
  protected static record ParsedArgument(Coordinate coordinate, ConfigurationParameter parameter,
      String text) {

    public ParsedArgument {
      coordinate = requireNonNull(coordinate);
      parameter = requireNonNull(parameter);
    }
  }

  /**
   * A command-line argument that has been parsed, resolved to a {@link ConfigurationParameter}, and
   * deserialized to a value.
   *
   * @param coordinate the coordinate of the argument
   * @param parameter  the parameter that the argument corresponds to
   * @param text       the text of the argument, exactly as given on the command-line, environment
   *                   variable, or system property. may be null.
   * @param value      the deserialized value of the argument. if text is null, then value is null.
   */
  protected static record DeserializedArgument(Coordinate coordinate,
      ConfigurationParameter parameter, String text, Object value) {

    public DeserializedArgument {
      coordinate = requireNonNull(coordinate);
      parameter = requireNonNull(parameter);
    }
  }

  private EnvironmentVariables variables;
  private SystemProperties properties;

  public SingleCommandInvocationStrategy() {
    this.variables = OptionalEnvironmentVariable::getenv;
    this.properties = OptionalSystemProperty::getProperty;
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args) {
    if (!(command instanceof SingleCommand<T> single)) {
      throw new IllegalArgumentException("Command is not a SingleCommand");
    }

    // Parse our arguments
    List<ParsedArgument> parsedArguments = parseArguments(single, args);

    // Ensure that all our required parameters have assignments
    Set<String> requiredParameters = single.getParameters().stream()
        .filter(ConfigurationParameter::isRequired).map(ConfigurationParameter::getName)
        .collect(toSet());
    Set<String> assignedParameters = parsedArguments.stream().map(a -> a.parameter().getName())
        .collect(toSet());
    if (!assignedParameters.containsAll(requiredParameters)) {
      Set<String> unassignedRequiredParameters = new HashSet<>(requiredParameters);
      unassignedRequiredParameters.removeAll(assignedParameters);
      throw new UnassignedRequiredParametersArgumentException(unassignedRequiredParameters);
    }

    // Deserialize our arguments
    List<DeserializedArgument> deserializedArguments = parsedArguments.stream()
        .map(a -> deserializeArgument(single, a)).toList();

    // Create our instance from the arguments
    T instance = createInstance(single, deserializedArguments);

    return new DefaultInvocation<>(List.of(), single, args, instance);
  }

  /*
        try {
          property.set(instance.getInstance(), "true");
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
   */

  /**
   * <p>
   * Parse the given arguments into a list of
   * {@link SingleCommandInvocationStrategy.ParsedArgument}s. The order of the arguments in the list
   * is undefined.
   * </p>
   *
   * <p>
   * This method is broken out partly as a hook for extension by users. For example, a user could
   * override this method to provide a different way of parsing arguments.
   * </p>
   *
   * @param command the command that the arguments are for
   * @param args    the command-line arguments to parse as given by the user
   */
  protected List<ParsedArgument> parseArguments(SingleCommand<?> command, List<String> args) {
    final List<ParsedArgument> result = new ArrayList<>();

    // Handle command-line arguments
    new ArgumentsParser(command, new ArgumentsParser.Handler() {
      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        // Flag fields are always boolean, so if a flag switch is present we just pretend it has an
        // implicit value of "true"
        result.add(new ParsedArgument(name, property, "true"));
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String text) {
        result.add(new ParsedArgument(name, property, text));
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String text) {
        result.add(new ParsedArgument(position, property, text));
      }
    }).parse(args);

    // Handle environment variable arguments
    command.getParameters().stream()
        .mapMulti(Streams.filterAndCast(EnvironmentConfigurationParameter.class))
        .forEach(property -> {
          String variableName = property.getVariableName().toString();
          getVariables().get(variableName).ifPresent((name, text) -> {
            result.add(new ParsedArgument(new VariableNameCoordinate(name), property, text));
          });
        });

    // Handle system property arguments
    command.getParameters().stream()
        .mapMulti(Streams.filterAndCast(PropertyConfigurationParameter.class)).forEach(property -> {
          String propertyName = property.getPropertyName().toString();
          getProperties().get(propertyName).ifPresent((name, text) -> {
            result.add(new ParsedArgument(new PropertyNameCoordinate(name), property, text));
          });
        });

    return unmodifiableList(result);
  }

  /**
   * <p>
   * Deserialize the given {@link SingleCommandInvocationStrategy.ParsedArgument} into a
   * {@link SingleCommandInvocationStrategy.DeserializedArgument}. Note that the
   * {@link ParsedArgument#text()} attribute of the given argument may be null, in which case the
   * {@link DeserializedArgument#text()} and {@link DeserializedArgument#value()} attribute of the
   * result should also be null. The {@link ValueDeserializer} to use for deserialization is given
   * by the {@link ConfigurationParameter#getDeserializer()} method of the
   * {@link ConfigurationParameter} of the argument.
   * </p>
   *
   * <p>
   * This method is broken out partly as a hook for extension by users. For example, a user could
   * override this method to provide a different way of deserializing arguments, or to provide a
   * different way of handling arguments that have no text. In such cases, the user should be
   * careful to throw any appropriate {@link ArgumentException exceptions} if deserialization
   * fails.
   * </p>
   *
   * @param command the command that the argument is for
   * @param parsed  the argument to deserialize
   * @throws ArgumentException if deserialization fails
   */
  protected SingleCommandInvocationStrategy.DeserializedArgument deserializeArgument(
      SingleCommand<?> command, ParsedArgument parsed) {
    if (parsed.text() == null) {
      return new DeserializedArgument(parsed.coordinate(), parsed.parameter(), null, null);
    }

    Object value;
    try {
      value = parsed.parameter().getDeserializer().deserialize(parsed.text());
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to deserialize argument", e);
    }

    return new DeserializedArgument(parsed.coordinate(), parsed.parameter(), parsed.text(), value);
  }


  /**
   * <p>
   * Use the given {@link SingleCommand} to create an instance of the command class and the given
   * list of {@link SingleCommandInvocationStrategy.DeserializedArgument}s to populate it. The
   * {@link SingleCommand#getBeanClass()} method of the command can be used to create the instance.
   * The appropriate {@link ValueSink} to use for assigning each property is given by
   * {@link ConfigurationParameter#getSink()}. The {@link DeserializedArgument#value()} attribute of
   * each argument is the value to assign to the property.
   * </p>
   *
   * <p>
   * This method is broken out partly as a hook for extension by users. For example, a user could
   * override this method to provide a different way of creating or populating instances. In such
   * cases, the user should be careful to throw any appropriate exceptions if deserialization
   * fails.
   * </p>
   *
   * @param command the command that the argument is for
   * @param args    the arguments to assign
   */
  protected <T> T createInstance(SingleCommand<T> command, List<DeserializedArgument> args) {
    BeanInstance result;
    try {
      result = command.getBeanClass().newInstance();
    } catch (InvocationTargetException e) {
      throw new NewInstanceFailureArgumentException(e);
    }

    for (DeserializedArgument arg : args) {
      ConfigurationParameter parameter = arg.parameter();
      try {
        parameter.getSink().write(result.getInstance(), arg.value());
      } catch (InvocationTargetException e) {
        throw new AssignmentFailureArgumentException(parameter.getName(), e);
      }
    }

    return command.getRawType().cast(result.getInstance());
  }

  private SingleCommandInvocationStrategy.EnvironmentVariables getVariables() {
    return variables;
  }

  /**
   * test hook
   */
  void setVariables(EnvironmentVariables variables) {
    this.variables = requireNonNull(variables);
  }

  private SingleCommandInvocationStrategy.SystemProperties getProperties() {
    return properties;
  }

  /**
   * test hook
   */
  void setProperties(SystemProperties properties) {
    this.properties = requireNonNull(properties);
  }
}
