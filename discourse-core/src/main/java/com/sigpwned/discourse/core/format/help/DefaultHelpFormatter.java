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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.text.TableLayout;
import com.sigpwned.discourse.core.util.JodaBeanUtils;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Text;
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

  private static class CommandPropertyHelp implements Comparable<CommandPropertyHelp> {
    private final LeafCommandProperty property;
    private final CommandPropertyCategory category;
    private final Comparable<?> syntaxKey;
    private final List<String> syntaxes;
    private final List<HelpMessage> description;

    public CommandPropertyHelp(LeafCommandProperty property, CommandPropertyCategory category,
        Comparable<?> syntaxKey, List<String> syntaxes, List<HelpMessage> description) {
      this.property = requireNonNull(property);
      this.category = requireNonNull(category);
      this.syntaxKey = requireNonNull(syntaxKey);
      this.syntaxes = unmodifiableList(syntaxes);
      this.description = unmodifiableList(description);
    }

    /**
     * @return the property
     */
    public LeafCommandProperty getProperty() {
      return property;
    }

    /**
     * @return the category
     */
    public CommandPropertyCategory getCategory() {
      return category;
    }

    /**
     * @return the syntaxKey
     */
    public Comparable<?> getSyntaxKey() {
      return syntaxKey;
    }

    /**
     * @return the syntaxes
     */
    public List<String> getSyntaxes() {
      return syntaxes;
    }

    /**
     * @return the description
     */
    public List<HelpMessage> getDescription() {
      return description;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int compareTo(CommandPropertyHelp that) {
      Comparable a = this.getSyntaxKey();
      Comparable b = that.getSyntaxKey();
      return a.compareTo(b);
    }
  }

  protected String formatLeafCommandHelp(Dialect dialect, ResolvedCommand<?> command,
      InvocationContext context) {
    ValueSinkFactory sinkFactory = context.get(PlanStep.VALUE_SINK_FACTORY_KEY).orElseThrow();

    ValueDeserializerFactory<?> deserializerFactory =
        context.get(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY).orElseThrow();

    // TODO Give SynopsisEditor a key
    SynopsisEditor synopsisFactory = context.get(SynopsisEditor.class).orElseThrow();

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
          // TODO What do we do if there isn't one?
          // TODO How do we know if a command has options?
          // TODO What if a command has things other than options, like flags?
          List<SynopsisEntry> synopsisEntries =
              synopsisFactory.editEntries(command, emptyList(), context);

          Synopsis synopsis = new Synopsis(synopsisEntries);

          SynopsisFormatter synopsisFormatter = context.get(SynopsisFormatter.class).orElseThrow();

          out.println(Text.rstrip(synopsisFormatter.formatSynopsis(synopsis)));
          out.println();

          if (command.getCommand().getDescription().isPresent()) {
            // TODO How should we localize?
            String localizedDescription = localizer.localizeMessage(
                HelpMessage.of(command.getCommand().getDescription().orElseThrow()), List.of(),
                context).getMessage();
            out.println(Text.rstrip(localizedDescription));
            out.println();
          }

          Map<CommandPropertyCategory, List<CommandPropertyHelp>> commandPropertyHelps =
              new TreeMap<>();
          for (LeafCommandProperty commandProperty : command.getCommand().getProperties()) {
            CommandPropertySyntax syntax =
                syntaxFormatter.formatParameterSyntax(commandProperty, context).orElseThrow(() -> {
                  // TODO better exception
                  return new IllegalArgumentException(
                      "Failed to format syntax: " + commandProperty);
                });

            Maybe<List<HelpMessage>> maybeDescriptions =
                propertyDescriber.describe(commandProperty, context);

            if (maybeDescriptions.isNo())
              continue;

            List<HelpMessage> descriptions = maybeDescriptions.orElseGet(List::of);

            CommandPropertyCategory category = syntax.getCategory();

            List<String> syntaxes = syntax.getSyntaxes();

            commandPropertyHelps.computeIfAbsent(category, s -> new ArrayList<>())
                .add(new CommandPropertyHelp(commandProperty, category, syntax.getKey(), syntaxes,
                    descriptions));
          }

          for (Map.Entry<CommandPropertyCategory, List<CommandPropertyHelp>> entry : commandPropertyHelps
              .entrySet()) {
            List<CommandPropertyHelp> helps = entry.getValue();

            helps.sort(Comparator.naturalOrder());
          }

          for (Map.Entry<CommandPropertyCategory, List<CommandPropertyHelp>> entry : commandPropertyHelps
              .entrySet()) {
            CommandPropertyCategory category = entry.getKey();
            List<CommandPropertyHelp> helps = entry.getValue();

            TableLayout layout = new TableLayout(2);

            for (CommandPropertyHelp help : helps) {
              LeafCommandProperty commandProperty = help.getProperty();

              String syntax = help.getSyntaxes().stream()
                  .sorted(Comparator.comparingInt(String::length).reversed())
                  .collect(joining(System.lineSeparator()));

              List<HelpMessage> localizedDescriptions = help
                  .getDescription().stream().map(description -> localizer
                      .localizeMessage(description, commandProperty.getAnnotations(), context))
                  .collect(toList());

              List<String> formattedDescriptions = localizedDescriptions.stream()
                  .map(localizedDescription -> MessageFormat.format(
                      localizedDescription.getMessage(),
                      localizedDescription.getArguments().toArray(Object[]::new)))
                  .collect(toList());

              String description = String.join(System.lineSeparator() + System.lineSeparator(),
                  formattedDescriptions);

              layout.addRow(new TableLayout.Row(List.of(syntax, description)));
            }

            String categoryName =
                localizer.localizeMessage(category.getName(), List.of(), context).getMessage();

            out.println(categoryName + ":");
            out.println();
            out.println(Text.rstrip(layout.toString(getWidth(),
                new int[] {TableLayout.COLUMN_WIDTH_TIGHT, TableLayout.COLUMN_WIDTH_FLEX}, 1, 4)));
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
