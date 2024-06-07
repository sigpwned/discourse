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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.text.TableLayout;
import com.sigpwned.discourse.core.util.JodaBeanUtils;
import com.sigpwned.discourse.core.util.Types;

/**
 * <p>
 * A default implementation of {@link HelpFormatter}. Generates messages in the following format:
 * </p>
 *
 * <h2>Single Command</h2>
 *
 * <pre>
 *   Usage: resolvedCommand [ flags | options ] [ positional ... ]
 *
 *   Description of the resolvedCommand.
 *
 *   Flags:
 *   -f, --flag1
 *     Description of flag1.
 *
 *   -g, --flag2
 *     Description of flag2.
 *
 *   Options:
 *   -o, --option1 &lt;type&gt;
 *     Description of option1.
 *
 *   -p, --option2 &lt;type&gt;
 *     Description of option2.
 *
 *   Environment Variables:
 *   ENV_VAR1
 *     Description of ENV_VAR1.
 *
 *   ENV_VAR2
 *     Description of ENV_VAR2.
 *
 *   System Properties:
 *   PROP1
 *     Description of PROP1.
 *
 *   PROP2
 *     Description of PROP2.
 *
 *    ...
 * </pre>
 *
 * <h2>Multi Command</h2>
 *
 * <pre>
 *   Usage: resolvedCommand &lt;subcommand&gt; [ flags | options ] [ positional ... ]
 *
 *   Description of the resolvedCommand.
 *
 *   Common Flags:
 *   -f, --flag1
 *   Description of flag1.
 *
 *   -g, --flag2
 *   Description of flag2.
 *
 *   Common Options:
 *   -o, --option1 &lt;type&gt;
 *     Description of option1.
 *
 *   -p, --option2 &lt;type&gt;
 *     Description of option2.
 *
 *   First parameter must be a subcommand specifier: subcommand1, subcommand2, ...
 *
 *   Usage: resolvedCommand subcommand1 [ flags | options ] [ positional ... ]
 *
 *   Description of subcommand1.
 *
 *   Flags:
 *   -f, --flag1
 *     Description of flag1.
 *
 *   -g, --flag2
 *     Description of flag2.
 *
 *   Options:
 *   -o, --option1 &lt;type&gt;
 *     Description of option1.
 *
 *   -p, --option2 &lt;type&gt;
 *     Description of option2.
 *
 *   Usage: resolvedCommand subcommand2 [ flags | options ] [ positional ... ]
 *
 *   Description of subcommand2.
 *
 *   Flags:
 *   -f, --flag1
 *     Description of flag1.
 *
 *   -g, --flag2
 *     Description of flag2.
 *
 *   Options:
 *   -o, --option1 &lt;type&gt;
 *     Description of option1.
 *
 *   -p, --option2 &lt;type&gt;
 *     Description of option2.
 *
 *   All Environment Variables:
 *   ENV_VAR1
 *   Description of ENV_VAR1.
 *
 *   ENV_VAR2
 *   Description of ENV_VAR2.
 *
 *   All System Properties:
 *   PROP1
 *   Description of PROP1.
 *
 *   PROP2
 *   Description of PROP2.
 * </pre>
 */
public class DefaultHelpFormatter implements HelpFormatter {

  public static final int DEFAULT_WIDTH = 100;

  public static final int COLUMN_WIDTH = 32;

  public static final String CONTINUATION_INDENTATION = "    ";

  public static final int MIN_WIDTH = 80;

  private final int width;
  private final CommandPropertySyntaxFormatter syntaxFormatter;
  private final CommandPropertyDescriber propertyDescriber;
  private final MessageLocalizer localizer;
  private final TextFormatter textFormatter;

  public DefaultHelpFormatter(int width, CommandPropertySyntaxFormatter syntaxFormatter,
      CommandPropertyDescriber propertyDescriber, MessageLocalizer localizer,
      TextFormatter textFormatter) {
    this.width = width;
    this.syntaxFormatter = requireNonNull(syntaxFormatter);
    this.propertyDescriber = requireNonNull(propertyDescriber);
    this.localizer = requireNonNull(localizer);
    this.textFormatter = requireNonNull(textFormatter);
    if (width < MIN_WIDTH)
      throw new IllegalArgumentException("width must be at least " + MIN_WIDTH);
  }

  @Override
  public String formatHelp(Dialect dialect, ResolvedCommand<?> command, InvocationContext context) {
    return formatLeafCommandHelp(dialect, command, context);
  }

  protected String formatLeafCommandHelp(Dialect dialect, ResolvedCommand<?> command,
      InvocationContext context) {
    LeafCommand<?> leaf = (LeafCommand<?>) command.getCommand();

    Map<Class<?>, List<LeafCommandProperty>> partitionedCommandProperties = leaf.getProperties()
        .stream()
        .flatMap(p -> p.getCoordinates().stream().map(Coordinate::getClass).distinct()
            .map(c -> Map.entry(c, p)))
        .collect(Collectors.groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));

    StringWriter result = new StringWriter();
    try {
      try {
        try (PrintWriter out = new PrintWriter(result)) {
          // TODO How do we get the command name?
          String commandName = command.getName().orElse("hello");
          out.println(commandName);
          out.println();

          for (Map.Entry<Class<?>, List<LeafCommandProperty>> e : partitionedCommandProperties
              .entrySet()) {
            Class<?> coordinateType = e.getKey();
            List<LeafCommandProperty> commandProperties = e.getValue();

            out.println(coordinateType.getSimpleName() + ":");
            out.println();

            TableLayout layout = new TableLayout(2);
            for (LeafCommandProperty commandProperty : commandProperties) {
              String syntax = syntaxFormatter.formatParameterSyntax(commandProperty, context)
                  .orElseGet(List::of).stream()
                  .sorted(Comparator.comparingInt(String::length).reversed())
                  .collect(joining("\n"));

              List<String> originalDescriptions =
                  propertyDescriber.describe(commandProperty, context).orElseGet(List::of);

              List<String> localizedDescriptions = originalDescriptions.stream()
                  .map(description -> localizer.localizeMessage(description,
                      commandProperty.getAnnotations(), context))
                  .collect(toList());

              String description = String.join(System.lineSeparator() + System.lineSeparator(),
                  localizedDescriptions);

              layout.addRow(new TableLayout.Row(List.of(syntax, description)));
            }
            out.println(layout.toString(getWidth(),
                new int[] {TableLayout.COLUMN_WIDTH_TIGHT, TableLayout.COLUMN_WIDTH_FLEX}, 1, 4));
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

  protected <T> String formatSuperCommandHelp(Dialect dialect, ResolvedCommand<T> command) {
    StringWriter result = new StringWriter();
    try {
      try {
        try (PrintWriter out = new PrintWriter(result)) {
          // // TODO How do we get the command name?
          // String commandName = "hello";
          // // if (command instanceof RootCommand<?> root) {
          // // if (root.getName().isPresent()) {
          // // commandName = root.getName().orElseThrow();
          // // }
          // // }
          //
          // // Print the name and description
          // IntUnaryOperator descriptionWidthFunction;
          // if (commandName != null) {
          // final StringBuilder buf = new StringBuilder().append(commandName).append(":");
          //
          // out.print(buf);
          //
          // descriptionWidthFunction =
          // n -> n == 0 ? Math.max(getWidth() - buf.length(), 1) : getWidth();
          // } else {
          // descriptionWidthFunction = n -> getWidth();
          //
          // }
          // if (command.getDescription().isPresent()) {
          // out.print(Text.wrap(command.getDescription().orElseThrow(), descriptionWidthFunction));
          // out.println();
          // }
          // out.println();
          //
          // // Print out the subcommands
          // final int maxDiscriminatorLength =
          // command.getSubcommands().keySet().stream().mapToInt(String::length).max().orElse(0);
          // final int minIndentLength = maxDiscriminatorLength + 4;
          // final String minIndent = Text.times(" ", minIndentLength);
          // for (Map.Entry<String, Command<? extends T>> e : command.getSubcommands().entrySet()) {
          // final String discriminator = e.getKey();
          // final Command<? extends T> subcommand = e.getValue();
          //
          // out.print(new StringBuilder().append(discriminator)
          // .append(Text.times(discriminator, maxDiscriminatorLength - discriminator.length()))
          // .append(" "));
          //
          // if (subcommand.getDescription().isPresent()) {
          // out.print(Text.wrap(subcommand.getDescription().orElseThrow(),
          // lineNumber -> getWidth() - minIndentLength, s -> minIndent + s));
          // out.println();
          // }
          //
          // out.println();
          // }
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
