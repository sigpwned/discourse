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
package com.sigpwned.discourse.core.command;

import static java.lang.String.*;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ConfigurableClass;
import com.sigpwned.discourse.core.ConfigurableClass.SubcommandClass;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.configuration.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultiCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * A command that has multiple subcommands, each of which is a command in its own right. The user
 * indicates which subcommand to execute by providing a discriminator. For example, the following
 * command has two subcommands, {@code foo} and {@code bar}:
 * </p>
 *
 * <pre>
 *   &#x40;Configurable(subcommands = {
 *     &#x40;Subcommand(configurable=FooSubcommand.class, discriminator="foo"),
 *     &#x40;Subcommand(configurable=BarSubcommand.class, discriminator="bar")
 *   })
 *   public class MyCommand {
 *     &#x40;OptionParameter(shortName="x", longName="xray")
 *     public String xray;
 *   }
 *
 *   &#x40;Configurable(discriminator="foo")
 *   public class FooSubcommand extends MyCommand {
 *     &#x40;PositionalParameter(position=0)
 *     public String foo;
 *   }
 *
 *   &#x40;Configurable(discriminator="bar")
 *   public class BarSubcommand extends MyCommand {
 *     &#x40;PositionalParameter(position=0)
 *     public String bar;
 *   }
 * </pre>
 *
 * <p>
 * This results in a command tree like the following, with discriminators as indicated:
 * </p>
 *
 * <pre>
 *   MyCommand
 *   │
 *   │   foo
 *   ├───────── FooSubcommand
 *   │
 *   │   bar
 *   └───────── BarSubcommand
 * </pre>
 *
 * <p>
 * Note that the {@code MyCommand} class is abstract. All multi command classes must be abstract.
 * </p>
 *
 * <p>
 * Also, note that the {@code FooSubcommand} and {@code BarSubcommand} classes have a discriminator
 * annotation. This is required for all subcommands. The discriminator annotation must match the
 * discriminator annotation in the {@code Subcommand} annotation in the {@code MyCommand} class.
 * </p>
 *
 * <p>
 * In multi commands, fields are inherited from superclasses. Therefore, in the above example,
 * {@code FooSubcommand} has the fields xray and foo, and {@code BarSubcommand} has the fields xray
 * and bar.
 * </p>
 *
 * <p>
 * Finally, note that both {@code FooSubcommand} and {@code BarSubcommand} have a positional
 * parameter, with position 0. This is not a conflict because they are in different subcommands. The
 * parameters in any particular command must not conflict with each other, but may conflict with
 * parameters in other subcommands not related by inheritance.
 * </p>
 *
 * <p>
 * Since Java 17, subcommands may also be defined using sealed classes and permits clauses. The
 * following is equivalent to the above example:
 * </p>
 *
 * <pre>
 *   &#x40;Configurable
 *   public sealed class MyCommand permits FooSubcommand, BarSubcommand {
 *     &#x40;OptionParameter(shortName="x", longName="xray")
 *     public String xray;
 *   }
 *
 *   &#x40;Configurable(discriminator="foo")
 *   public final class FooSubcommand extends MyCommand {
 *     &#x40;PositionalParameter(position=0)
 *     public String foo;
 *   }
 *
 *   &#x40;Configurable(discriminator="bar")
 *   public final class BarSubcommand extends MyCommand {
 *     &#x40;PositionalParameter(position=0)
 *     public String bar;
 *   }
 * </pre>
 *
 * <p>
 * This may be preferable because it is more concise and less error-prone since it does not require
 * the use of the {@code Subcommand} annotation and therefore does not require repeating the
 * discriminators.
 * </p>
 *
 * @param <T> the type of the object that the command returns
 */
public final class MultiCommand<T> extends Command<T> {

  public static <T> MultiCommand<T> scan(InvocationContext context,
      ConfigurableClass<T> configurableClass) {
    if (configurableClass.getSubcommands().isEmpty()) {
      // TODO This should be a configuration exception
      throw new IllegalArgumentException(
          format("Configurable %s has no subcommands", configurableClass.getRawType().getName()));
    }
    if (!Modifier.isAbstract(configurableClass.getRawType().getModifiers())) {
      throw new MultiCommandNotAbstractConfigurationException(configurableClass.getRawType());
    }

    Map<Discriminator, Command<? extends T>> subcommands = new LinkedHashMap<>();
    for (SubcommandClass<? extends T> subcommand : configurableClass.getSubcommands()) {
      ConfigurableClass<? extends T> subcommandClass = ConfigurableClass.scan(subcommand.rawType());

      Discriminator subcommandDiscriminator = subcommandClass.getDiscriminator().orElseThrow(
          () -> new NoDiscriminatorConfigurationException(subcommandClass.getRawType()));

      if (subcommand.discriminator().isPresent() && !subcommand.discriminator().orElseThrow()
          .equals(subcommandDiscriminator)) {
        throw new DiscriminatorMismatchConfigurationException(subcommandClass.getRawType(),
            subcommandDiscriminator, subcommand.discriminator().orElseThrow());
      }

      if (!Objects.equals(subcommandClass.getRawType().getSuperclass(),
          configurableClass.getRawType())) {
        throw new SubcommandDoesNotExtendRootCommandConfigurationException(
            configurableClass.getRawType(), subcommandClass.getRawType());
      }

      subcommands.put(subcommandDiscriminator, Command.subscan(context, subcommandClass));
    }

    return new MultiCommand<>(configurableClass.getRawType(), configurableClass.getName(),
        configurableClass.getDescription(), configurableClass.getVersion(), subcommands);
  }

  private final Class<T> rawType;
  private final Map<Discriminator, Command<? extends T>> subcommands;

  public MultiCommand(Class<T> rawType, String name, String description, String version,
      Map<Discriminator, Command<? extends T>> subcommands) {
    super(name, description, version);
    this.rawType = requireNonNull(rawType);
    if (subcommands.isEmpty()) {
      throw new IllegalArgumentException("no subcommands");
    }
    this.subcommands = unmodifiableMap(subcommands);
  }

  public Class<T> getRawType() {
    return rawType;
  }

  /**
   * @return the subcommands
   */
  public Map<Discriminator, Command<? extends T>> getSubcommands() {
    return subcommands;
  }
}
