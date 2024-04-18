/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.format.help;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.HelpFormatter;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.Commands;
import com.sigpwned.discourse.core.util.JodaBeanUtils;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.discourse.core.util.Text;
import com.sigpwned.discourse.core.util.Types;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultHelpFormatter implements HelpFormatter {

  public static final DefaultHelpFormatter INSTANCE = new DefaultHelpFormatter();

  public static final int DEFAULT_WIDTH = 100;

  public static final int COLUMN_WIDTH = 32;

  public static final String CONTINUATION_INDENTATION = "    ";

  private final int width;

  public DefaultHelpFormatter() {
    this(DEFAULT_WIDTH);
  }

  public DefaultHelpFormatter(int width) {
    this.width = width;
  }

  @Override
  public String formatHelp(Command<?> command) {
    // JAVA21 Use pattern matching here and get rid of AssertionError
    if (command instanceof SingleCommand<?> single) {
      return formatHelp(single);
    } else if (command instanceof MultiCommand<?> multi) {
      return formatHelp(multi);
    }
    throw new AssertionError("Unknown command type: " + command.getClass());
  }

  public String formatHelp(SingleCommand<?> command) {
    StringWriter result = new StringWriter();
    try {
      try {
        try (PrintWriter out = new PrintWriter(result)) {
          List<FlagConfigurationParameter> flags = command.getParameters().stream()
              .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
              .sorted(Comparator.comparing(FlagConfigurationParameter::getName)).toList();

          List<OptionConfigurationParameter> options = command.getParameters().stream()
              .mapMulti(Streams.filterAndCast(OptionConfigurationParameter.class))
              .sorted(Comparator.comparing(OptionConfigurationParameter::getName)).toList();

          List<PositionalConfigurationParameter> positionals = command.getParameters().stream()
              .mapMulti(Streams.filterAndCast(PositionalConfigurationParameter.class))
              .sorted(Comparator.comparing(PositionalConfigurationParameter::getPosition)).toList();

          out.print("Usage:");

          if (command.getName() != null) {
            out.printf(" %s", command.getName());
          }

          if (!flags.isEmpty() && !options.isEmpty()) {
            out.print(" [ flags | options ]");
          } else if (!flags.isEmpty()) {
            out.print(" [ flags ]");
          } else if (!options.isEmpty()) {
            out.print(" [ options ]");
          }

          if (!positionals.isEmpty()) {
            for (PositionalConfigurationParameter positional : positionals) {
              if (positional.isRequired()) {
                out.printf(" <%s>", positional.getName());
              } else if (positional.isCollection()) {
                out.printf(" [%s ...]", positional.getName());
              } else {
                out.printf(" [%s]", positional.getName());
              }
            }
          }

          out.println();
          out.println();

          if (command.getDescription() != null) {
            out.println(Text.wrap(command.getDescription(), getWidth()));
            out.println();
          }

          if (!flags.isEmpty()) {
            out.println("Flags:");
            for (FlagConfigurationParameter flag : flags) {
              StringBuilder buf = new StringBuilder();
              if (flag.getShortName() != null) {
                buf.append(flag.getShortName().toSwitchString());
              }
              if (flag.getShortName() != null && flag.getLongName() != null) {
                buf.append(", ");
              }
              if (flag.getLongName() != null) {
                buf.append(flag.getLongName().toSwitchString());
              }
              out.print(buf);
              if (flag.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(flag.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }

          if (!options.isEmpty()) {
            out.println("Options:");
            for (OptionConfigurationParameter option : options) {
              StringBuilder buf = new StringBuilder();
              if (option.getShortName() != null) {
                buf.append(option.getShortName().toSwitchString());
              }
              if (option.getShortName() != null && option.getLongName() != null) {
                buf.append(", ");
              }
              if (option.getLongName() != null) {
                buf.append(option.getLongName().toSwitchString());
              }
              buf.append(" <").append(toString(option.getGenericType())).append(">");
              out.print(buf);
              if (option.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(option.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }

          List<EnvironmentConfigurationParameter> variables = command.getParameters().stream()
              .mapMulti(Streams.filterAndCast(EnvironmentConfigurationParameter.class))
              .sorted(Comparator.comparing(EnvironmentConfigurationParameter::getVariableName))
              .toList();

          if (!variables.isEmpty()) {
            out.println("Environment Variables:");
            for (EnvironmentConfigurationParameter variable : variables) {
              StringBuilder buf = new StringBuilder();
              buf.append(variable.getVariableName());
              out.print(buf);
              if (variable.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(variable.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }

          List<PropertyConfigurationParameter> properties = command.getParameters().stream()
              .mapMulti(Streams.filterAndCast(PropertyConfigurationParameter.class))
              .sorted(Comparator.comparing(PropertyConfigurationParameter::getPropertyName))
              .toList();

          if (!properties.isEmpty()) {
            out.println("System Properties:");
            for (PropertyConfigurationParameter property : properties) {
              StringBuilder buf = new StringBuilder();
              buf.append(property.getPropertyName());
              out.print(buf);
              if (property.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(property.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }
        }
      } finally {
        result.close();
      }
    } catch (IOException e) {
      // Should never happen...
      throw new UncheckedIOException("Failed to generate help message", e);
    }
    return result.toString();
  }

  public String formatHelp(MultiCommand<?> command) {
    StringWriter result = new StringWriter();
    try {
      try {
        try (PrintWriter out = new PrintWriter(result)) {
          if (command.getName() != null && command.getDescription() != null) {
            StringBuilder buf = new StringBuilder();
            buf.append(command.getName());
            buf.append(": ");

            out.print(buf);
            out.print(Text.wrap(command.getDescription(),
                n -> n == 0 ? Math.max(getWidth() - buf.length(), 1) : getWidth()));
            out.println();
            out.println();
          }

          Set<ConfigurationParameter> commonParameters = Commands.commonParameters(command);

          List<FlagConfigurationParameter> commonFlags = commonParameters.stream()
              .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class)).toList();

          List<OptionConfigurationParameter> commonOptions = commonParameters.stream()
              .mapMulti(Streams.filterAndCast(OptionConfigurationParameter.class)).toList();

          if (!commonFlags.isEmpty()) {
            out.println("Common Flags:");
            for (FlagConfigurationParameter flag : commonFlags) {
              StringBuilder buf = new StringBuilder();
              if (flag.getShortName() != null) {
                buf.append(flag.getShortName().toSwitchString());
              }
              if (flag.getShortName() != null && flag.getLongName() != null) {
                buf.append(", ");
              }
              if (flag.getLongName() != null) {
                buf.append(flag.getLongName().toSwitchString());
              }
              out.print(buf);
              if (flag.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(flag.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }

          if (!commonOptions.isEmpty()) {
            out.println("Common Options:");
            for (OptionConfigurationParameter option : commonOptions) {
              StringBuilder buf = new StringBuilder();
              if (option.getShortName() != null) {
                buf.append(option.getShortName().toSwitchString());
              }
              if (option.getShortName() != null && option.getLongName() != null) {
                buf.append(", ");
              }
              if (option.getLongName() != null) {
                buf.append(option.getLongName().toSwitchString());
              }
              buf.append(" <").append(toString(option.getGenericType())).append(">");
              out.print(buf);
              if (option.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(option.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }

          out.println(Text.wrap(
              "First parameter must be a subcommand specifier: " + command.getSubcommands().keySet()
                  .stream().sorted().map(Objects::toString).collect(joining(", ")),
              n -> n == 0 ? getWidth() : getWidth() - 4,
              l -> l.startsWith("First ") ? l : "    " + l));
          out.println();

          @SuppressWarnings({"rawtypes",
              "unchecked"}) Iterator<Map.Entry<Discriminator, Command<?>>> subcommandIterator = (Iterator) command.getSubcommands()
              .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).iterator();
          while (subcommandIterator.hasNext()) {
            Map.Entry<Discriminator, Command<?>> subentry = subcommandIterator.next();
            Discriminator subdiscriminator = subentry.getKey();
            Command<?> subcommand = subentry.getValue();

            List<FlagConfigurationParameter> flags = Commands.parameters(subcommand)
                .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
                .filter(not(commonFlags::contains))
                .sorted(Comparator.comparing(FlagConfigurationParameter::getName)).toList();

            List<OptionConfigurationParameter> options = Commands.parameters(subcommand)
                .mapMulti(Streams.filterAndCast(OptionConfigurationParameter.class))
                .filter(not(commonOptions::contains))
                .sorted(Comparator.comparing(OptionConfigurationParameter::getName)).toList();

            List<PositionalConfigurationParameter> positionals = Commands.parameters(subcommand)
                .mapMulti(Streams.filterAndCast(PositionalConfigurationParameter.class))
                .sorted(Comparator.comparing(PositionalConfigurationParameter::getPosition))
                .toList();

            out.print("Usage:");

            if (command.getName() != null) {
              out.printf(" %s", command.getName());
            }

            out.printf(" %s", subdiscriminator);

            if ((!commonFlags.isEmpty() || !flags.isEmpty()) && (!commonOptions.isEmpty()
                || !options.isEmpty())) {
              out.print(" [ flags | options ]");
            } else if (!commonFlags.isEmpty() || !flags.isEmpty()) {
              out.print(" [ flags ]");
            } else if (!commonOptions.isEmpty() || !options.isEmpty()) {
              out.print(" [ options ]");
            }

            if (!positionals.isEmpty()) {
              for (PositionalConfigurationParameter positional : positionals) {
                if (positional.isRequired()) {
                  out.printf(" <%s>", positional.getName());
                } else if (positional.isCollection()) {
                  out.printf(" [%s ...]", positional.getName());
                } else {
                  out.printf(" [%s]", positional.getName());
                }
              }
            }

            out.println();
            out.println();
          }

          List<EnvironmentConfigurationParameter> variables = Commands.parameters(command)
              .mapMulti(Streams.filterAndCast(EnvironmentConfigurationParameter.class))
              .sorted(Comparator.comparing(EnvironmentConfigurationParameter::getVariableName))
              .toList();

          if (!variables.isEmpty()) {
            out.println("All Environment Variables:");
            for (EnvironmentConfigurationParameter variable : variables) {
              StringBuilder buf = new StringBuilder();
              buf.append(variable.getVariableName());
              out.print(buf);
              if (variable.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(variable.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }

          List<PropertyConfigurationParameter> properties = Commands.parameters(command)
              .mapMulti(Streams.filterAndCast(PropertyConfigurationParameter.class))
              .sorted(Comparator.comparing(PropertyConfigurationParameter::getPropertyName))
              .toList();

          if (!properties.isEmpty()) {
            out.println("All System Properties:");
            for (PropertyConfigurationParameter property : properties) {
              StringBuilder buf = new StringBuilder();
              buf.append(property.getPropertyName());
              out.print(buf);
              if (property.getDescription().isEmpty()) {
                out.println();
              } else {
                if (buf.length() < COLUMN_WIDTH) {
                  out.print(Text.times(" ", COLUMN_WIDTH - buf.length()));
                }
                out.println(Text.wrap(property.getDescription(), n -> n == 0 ? Math.max(
                        getWidth() - COLUMN_WIDTH - CONTINUATION_INDENTATION.length(), 1) : getWidth(),
                    l -> CONTINUATION_INDENTATION + l));
              }
            }
            out.println();
          }
        }
      } finally {
        result.close();
      }
    } catch (IOException e) {
      // Should never happen...
      throw new UncheckedIOException("Failed to generate help message", e);
    }
    return result.toString();
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /* default */
  static String toString(Type genericType) {
    Class<?> erased = JodaBeanUtils.eraseToClass(genericType);
    Class<?> boxed = Types.isPrimitive(erased) ? Types.boxed(erased) : erased;
    return boxed.getSimpleName().toLowerCase();
  }
}
