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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.command.SuperCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.SubCommand;
import com.sigpwned.discourse.core.util.JodaBeanUtils;
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
  public String formatHelp(Syntax syntax, Command<?> command) {
    if (command instanceof LeafCommand leafCommand) {
      return formatLeafCommandHelp(syntax, leafCommand);
    } else if (command instanceof SuperCommand<?> superCommand) {
      return formatSuperCommandHelp(syntax, command);
    } else {
      // TODO better exception
      throw new IllegalArgumentException("Command is not a leaf command or super command");
    }
  }

  protected String formatLeafCommandHelp(Syntax syntax, LeafCommand<?> command) {
    StringWriter result = new StringWriter();
    try {
      try {
        try (PrintWriter out = new PrintWriter(result)) {
          // Do we have a name?
          String commandName = null;
          if (command instanceof RootCommand<?> root) {
            if (root.getName().isPresent()) {
              commandName = root.getName().orElseThrow();
            }
          }
          if (commandName == null) {
            commandName = "hello";
          }

          out.println(commandName);
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

  protected <T> String formatSuperCommandHelp(Syntax syntax, Command<T> command) {
    StringWriter result = new StringWriter();
    try {
      try {
        try (PrintWriter out = new PrintWriter(result)) {
          // Do we have a name?
          String commandName = null;
          if (command instanceof RootCommand<?> root) {
            if (root.getName().isPresent()) {
              commandName = root.getName().orElseThrow();
            }
          }

          // Print the name and description
          IntUnaryOperator descriptionWidthFunction;
          if (commandName != null) {
            final StringBuilder buf = new StringBuilder().append(commandName).append(":");

            out.print(buf);

            descriptionWidthFunction =
                n -> n == 0 ? Math.max(getWidth() - buf.length(), 1) : getWidth();
          } else {
            descriptionWidthFunction = n -> getWidth();

          }
          if (command.getDescription().isPresent()) {
            out.print(Text.wrap(command.getDescription().orElseThrow(), descriptionWidthFunction));
            out.println();
          }
          out.println();

          // Print out the subcommands
          final int maxDiscriminatorLength =
              command.getSubcommands().keySet().stream().mapToInt(String::length).max().orElse(0);
          final int minIndentLength = maxDiscriminatorLength + 4;
          final String minIndent = Text.times(" ", minIndentLength);
          for (Map.Entry<String, SubCommand<? extends T>> e : command.getSubcommands().entrySet()) {
            final String discriminator = e.getKey();
            final SubCommand<? extends T> subcommand = e.getValue();

            out.print(new StringBuilder().append(discriminator)
                .append(Text.times(discriminator, maxDiscriminatorLength - discriminator.length()))
                .append("    "));

            if (subcommand.getDescription().isPresent()) {
              out.print(Text.wrap(subcommand.getDescription().orElseThrow(),
                  lineNumber -> getWidth() - minIndentLength, s -> minIndent + s));
              out.println();
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
