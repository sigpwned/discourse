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

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ArgumentsParser.Handler;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.model.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.model.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.VariableNameCoordinate;
import com.sigpwned.discourse.core.model.argument.ParsedArgument;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import com.sigpwned.discourse.core.optional.OptionalEnvironmentVariable;
import com.sigpwned.discourse.core.optional.OptionalSystemProperty;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import java.util.ArrayList;
import java.util.List;

public class InvocationBuilderParseStep<T> {

  /**
   * The key for the {@link PropertyStore} in the {@link InvocationContext}.
   */
  public static InvocationContext.Key<PropertyStore> PROPERTY_STORE_KEY = InvocationContext.Key.of(
      "discourse.InvocationBuilderParseStep.PropertyStore", PropertyStore.class);

  /**
   * A store of system properties. Generally points to {@link System#getProperty(String)}. This is
   * broken out as a test extension point.
   */
  @FunctionalInterface
  public static interface PropertyStore {

    public OptionalSystemProperty<String> findSystemProperty(String name);
  }

  /**
   * The key for the {@link VariableStore} in the {@link InvocationContext}.
   */
  public static InvocationContext.Key<VariableStore> VARIABLE_STORE_KEY = InvocationContext.Key.of(
      "discourse.InvocationBuilderParseStep.VariableStore", VariableStore.class);

  /**
   * A store of environment variables. Generally points to {@link System#getenv(String)}. This is
   * broken out as a test extension point.
   */
  @FunctionalInterface
  public static interface VariableStore {

    public OptionalEnvironmentVariable<String> findEnvironmentVariable(String name);
  }

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final List<String> remainingArguments;

  public InvocationBuilderParseStep(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArguments) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.remainingArguments = unmodifiableList(remainingArguments);
  }

  public InvocationBuilderDeserializeStep<T> parse(InvocationContext context) {

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.beforeParse(rootCommand, dereferencedCommands, resolvedCommand,
          remainingArguments, context);
    });

    PropertyStore propertyStore = context.get(PROPERTY_STORE_KEY)
        .orElse(OptionalSystemProperty::getProperty);

    VariableStore variableStore = context.get(VARIABLE_STORE_KEY)
        .orElse(OptionalEnvironmentVariable::getenv);

    List<ParsedArgument> parsedArguments = doParse(propertyStore, variableStore);

    return new InvocationBuilderDeserializeStep<>(rootCommand, dereferencedCommands,
        resolvedCommand, parsedArguments);
  }

  protected List<ParsedArgument> doParse(PropertyStore propertyStore, VariableStore variableStore) {
    final List<ParsedArgument> parsedArguments = new ArrayList<>();

    // Check the CLI
    new ArgumentsParser(resolvedCommand, new Handler() {
      @Override
      public void flag(NameCoordinate coordinate, FlagConfigurationParameter property) {
        parsedArguments.add(new ParsedArgument(coordinate, property, "true"));
      }

      @Override
      public void option(NameCoordinate coordinate, OptionConfigurationParameter property,
          String value) {
        parsedArguments.add(new ParsedArgument(coordinate, property, value));
      }

      @Override
      public void positional(PositionCoordinate coordinate,
          PositionalConfigurationParameter property, String value) {
        parsedArguments.add(new ParsedArgument(coordinate, property, value));
      }
    }).parse(remainingArguments);

    for (ConfigurationParameter parameter : resolvedCommand.getParameters()) {
      if (parameter instanceof PropertyConfigurationParameter property) {
        // Check the system properties
        PropertyNameCoordinate coordinate = property.getPropertyName();
        propertyStore.findSystemProperty(coordinate.getText()).ifPresent((name, value) -> {
          parsedArguments.add(new ParsedArgument(coordinate, property, value));
        });
      } else if (parameter instanceof EnvironmentConfigurationParameter variable) {
        // Check the environment variables
        VariableNameCoordinate coordinate = variable.getVariableName();
        variableStore.findEnvironmentVariable(coordinate.getText()).ifPresent((name, value) -> {
          // Just use the first coordinate. There should be only one.
          parsedArguments.add(new ParsedArgument(coordinate, variable, value));
        });
      }
    }

    return parsedArguments;
  }
}
