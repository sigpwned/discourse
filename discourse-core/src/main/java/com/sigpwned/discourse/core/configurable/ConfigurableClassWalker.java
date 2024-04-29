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
package com.sigpwned.discourse.core.configurable;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.configuration.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.DuplicateDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultiCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SealedSubcommandsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendParentCommandConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.util.Discriminators;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Walks a class that is annotated with {@link Configurable}, which is to say a class that is the
 * source of configuration information for a {@link Command}. Visits the given class first, then
 * visits all subcommands of the class, and so on. This class performs validation on the contents of
 * the {@code Configurable} annotations and the structure of the classes themselves -- i.e.,
 * abstract or concrete as expected, not interfaces, etc. -- but does not validate any constructors
 * or fields.
 * </p>
 *
 * <p>
 * Methods are called on the given {@link ConfigurableClassWalker.Visitor visitor} as the walker
 * visits each class. The visitor is responsible for handling the classes as they are visited. The
 * visitor's methods are called in a depth-first manner, so that the visitor will be called on the
 * root class first, then on all subcommands of the root class, then on all subcommands of the
 * subcommands, and so on.
 * </p>
 *
 * <p>
 * For example, for the given class structure:
 * </p>
 *
 * <pre>
 *   RootCommand (MultiCommand)
 *     │
 *     ├── discriminator1 &rarr; Subcommand1 (MultiCommand)
 *     │     │
 *     │     ├── discriminator1A &rarr; Subcommand1A (SingleCommand)
 *     │     │
 *     │     └── discriminator1B &rarr; Subcommand1B (SingleCommand)
 *     │
 *     ├── discriminator2 &rarr; Subcommand2 (SingleCommand)
 *     │
 *     ├── discriminator3 &rarr; Subcommand3 (MultiCommand)
 *     │     │
 *     │     ├── discriminator3A &rarr; Subcommand3A (SingleCommand)
 *     │     │
 *     │     ├── discriminator3B &rarr; Subcommand3B (SingleCommand)
 *     │     │
 *     │     └── discriminator3C &rarr; Subcommand3C (SingleCommand)
 *     │
 *     └── discriminator4 &rarr; Subcommand4 (SingleCommand)
 *  </pre>
 *
 * <p>
 * The visitor will be called in the following order:
 * </p>
 *
 * <ol>
 *   <li><code>enterMultiCommandClass(null, RootCommand)</code></li>
 *   <li><code>enterMultiCommandClass(discriminator1, Subcommand1)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator1A, Subcommand1A)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator1B, Subcommand1B)</code></li>
 *   <li><code>leaveMultiCommandClass(discriminator1, Subcommand1)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator2, Subcommand2)</code></li>
 *   <li><code>enterMultiCommandClass(discriminator3, Subcommand3)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator3A, Subcommand3A)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator3B, Subcommand3B)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator3C, Subcommand3C)</code></li>
 *   <li><code>leaveMultiCommandClass(discriminator3, Subcommand3)</code></li>
 *   <li><code>visitSingleCommandClass(discriminator4, Subcommand4)</code></li>
 *   <li><code>leaveMultiCommandClass(null, RootCommand)</code></li>
 * </ol>
 *
 * <p>
 *   The name of this class, <code>ConfigurableClassWalker</code>, is meant to say "walker of
 *   &#x40;Configurable classes", not "walker of classes that is itself configurable".
 * </p>
 */
public final class ConfigurableClassWalker<T> {

  public static interface Visitor<T> {

    /**
     * <p>
     * Visit a class that represents a {@link MultiCommand}. A multi command is a command that can
     * run any one of multiple subcommands based on a {@link Discriminator}. This mode is modeled
     * after the AWS CLI and Git CLI. A multi command may be a root command or a subcommand, but is
     * never a leaf command.
     * </p>
     *
     * <p>
     * This method is called before any subcommands are visited.
     * </p>
     *
     * @param discriminator the discriminator for the commands. if not null, then this command is a
     *                      subcommand. if null, then this command is a root command.
     * @param name          the name of the command
     * @param description   the description of the command
     * @param version       the version of the command
     * @param clazz         the class that represents the command
     * @param <M>           the type of this MutliCommand class
     */
    default <M extends T> void enterMultiCommandClass(Discriminator discriminator, String name,
        String description, String version, Class<M> clazz) {

    }

    /**
     * <p>
     * Visit a class that represents a {@link MultiCommand}. A multi command is a command that can
     * run any one of multiple subcommands based on a {@link Discriminator}. This mode is modeled
     * after the AWS CLI and Git CLI. A multi command may be a root command or a subcommand, but is
     * never a leaf command.
     * </p>
     *
     * <p>
     * This method is called after all subcommands are visited.
     * </p>
     *
     * @param discriminator the discriminator for the commands. if not null, then this command is a
     *                      subcommand. if null, then this command is a root command.
     * @param name          the name of the command
     * @param description   the description of the command
     * @param version       the version of the command
     * @param clazz         the class that represents the command
     * @param <M>           the type of this MutliCommand class
     */
    default <M extends T> void leaveMultiCommandClass(Discriminator discriminator, String name,
        String description, String version, Class<M> clazz) {

    }

    /**
     * Visit a class that represents a {@link SingleCommand}. A single command is a standalone
     * command that takes arguments and constructs an instance of the command class. A single
     * command may be a root command or a subcommand, but is always a leaf command.
     *
     * @param discriminator the discriminator for the command. if not null, then this command is a
     *                      subcommand. if null, then this command is a root command.
     * @param name          the name of the command
     * @param description   the description of the command
     * @param version       the version of the command
     * @param clazz         the class that represents the command
     */
    default <S extends T> void visitSingleCommandClass(Discriminator discriminator, String name,
        String description, String version, Class<S> clazz) {

    }
  }

  private final Class<T> clazz;

  public ConfigurableClassWalker(Class<T> clazz) {
    this.clazz = requireNonNull(clazz);
  }

  public void walk(Visitor<T> visitor) {
    if (clazz.isInterface()) {
      // TODO New configurable exception
      throw new IllegalArgumentException("Cannot walk an interface");
    }

    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new NotConfigurableConfigurationException(clazz);
    }

    if (discriminatorFromConfigurable(clazz, configurable).isPresent()) {
      throw new UnexpectedDiscriminatorConfigurationException(clazz);
    }

    List<SubcommandClass<? extends T>> subcommands = subcommands(configurable, clazz, visitor);
    if (subcommands.isEmpty()) {
      if (Modifier.isAbstract(clazz.getModifiers())) {
        // TODO New configurable exception
        throw new IllegalArgumentException("single command must not be abstract");
      }
      visitor.visitSingleCommandClass(null, configurable.name(), configurable.description(),
          configurable.version(), clazz);
    } else {
      if (!Modifier.isAbstract(clazz.getModifiers())) {
        // TODO New configurable exception
        throw new MultiCommandNotAbstractConfigurationException(clazz);
      }
      visitor.enterMultiCommandClass(null, configurable.name(), configurable.description(),
          configurable.version(), clazz);
      for (SubcommandClass<? extends T> subcommand : subcommands) {
        subwalk(subcommand.expectedDiscriminator().orElse(null), configurable.version(),
            subcommand.rawType(), visitor);
      }
      visitor.leaveMultiCommandClass(null, configurable.name(), configurable.description(),
          configurable.version(), clazz);
    }
  }

  private void subwalk(Discriminator expectedDiscriminator, String version,
      Class<? extends T> clazz, Visitor<T> visitor) {
    if (clazz.isInterface()) {
      // TODO New configurable exception
      throw new IllegalArgumentException("Cannot walk an interface");
    }

    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new NotConfigurableConfigurationException(clazz);
    }
    if (!configurable.version().isEmpty() && !configurable.version().equals(version)) {
      // TODO New configurable exception
      throw new IllegalArgumentException("Version mismatch");
    }

    Discriminator discriminator = discriminatorFromConfigurable(clazz, configurable).orElseThrow(
        () -> new NoDiscriminatorConfigurationException(clazz));

    if (expectedDiscriminator != null && !expectedDiscriminator.equals(discriminator)) {
      throw new DiscriminatorMismatchConfigurationException(clazz, expectedDiscriminator,
          discriminator);
    }

    List<SubcommandClass<? extends T>> subcommands = subcommands(configurable, clazz, visitor);
    if (subcommands.isEmpty()) {
      if (Modifier.isAbstract(clazz.getModifiers())) {
        // TODO New configurable exception
        throw new IllegalArgumentException("single command must not be abstract");
      }
      visitor.visitSingleCommandClass(discriminator, configurable.name(),
          configurable.description(), version, clazz);
    } else {
      if (!Modifier.isAbstract(clazz.getModifiers())) {
        // TODO New configurable exception
        throw new MultiCommandNotAbstractConfigurationException(clazz);
      }
      visitor.enterMultiCommandClass(discriminator, configurable.name(), configurable.description(),
          version, clazz);
      for (SubcommandClass<? extends T> subcommand : subcommands) {
        subwalk(subcommand.expectedDiscriminator().orElse(null), version, subcommand.rawType(),
            visitor);
      }
      visitor.leaveMultiCommandClass(discriminator, configurable.name(), configurable.description(),
          version, clazz);
    }
  }

  /**
   * Represents a pointer to a subcommand class. This class is used to store the class of a
   * subcommand and its discriminator. This class is immutable.
   *
   * @param <T>
   */
  private static record SubcommandClass<T>(Optional<Discriminator> expectedDiscriminator,
      Class<T> rawType) {

    public SubcommandClass {
      expectedDiscriminator = requireNonNull(expectedDiscriminator);
      rawType = requireNonNull(rawType);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> List<SubcommandClass<? extends T>> subcommands(Configurable configurable,
      Class<? extends T> configurableClass, Visitor<T> visitor) {
    List<SubcommandClass<? extends T>> result;

    if (configurable.subcommands().length > 0) {
      if (configurableClass.isSealed()) {
        throw new SealedSubcommandsConfigurationException(configurableClass);
      }
      result = Arrays.stream(configurable.subcommands()).map(subcommand -> {
        Discriminator expectedDiscriminator = discriminatorFromSubcommand(configurableClass,
            subcommand);

        // TODO Should this check be done in the walk/subwalk methods?
        if (!subcommand.configurable().getSuperclass().equals(configurableClass)) {
          throw new SubcommandDoesNotExtendParentCommandConfigurationException(configurableClass,
              subcommand.configurable());
        }
        Class<? extends T> subcommandType = (Class<? extends T>) subcommand.configurable();

        return new SubcommandClass<>(Optional.of(expectedDiscriminator), subcommandType);
      }).collect(toList());
    } else if (configurableClass.isSealed()) {
      result = Arrays.stream(configurableClass.getPermittedSubclasses())
          .map(subcommandType -> (Class<? extends T>) subcommandType)
          .map(subcommandType -> new SubcommandClass<>(Optional.empty(), subcommandType))
          .collect(toList());
    } else {
      result = List.of();
    }

    Discriminator duplicateDiscriminator = Streams.duplicates(
        result.stream().flatMap(c -> c.expectedDiscriminator().stream())).findAny().orElse(null);
    if (duplicateDiscriminator != null) {
      throw new DuplicateDiscriminatorConfigurationException(duplicateDiscriminator);
    }

    return result;
  }

  /**
   * Extracts the discriminator from a configurable.
   *
   * @param configurableClass the configurable class which has the configurable annotation
   * @param configurable      the configurable
   * @return the discriminator
   * @throws InvalidDiscriminatorConfigurationException if the discriminator is invalid
   */
  private static Optional<Discriminator> discriminatorFromConfigurable(Class<?> configurableClass,
      Configurable configurable) {
    try {
      return Discriminators.fromConfigurable(configurable);
    } catch (IllegalArgumentException e) {
      throw new InvalidDiscriminatorConfigurationException(configurableClass,
          configurable.discriminator());
    }
  }

  /**
   * Extracts the discriminator from a subcommand.
   *
   * @param configurableClass the configurable class which has the configurable annotation
   * @param subcommand        the configurable
   * @return the discriminator
   * @throws InvalidDiscriminatorConfigurationException if the discriminator is invalid
   */
  private static Discriminator discriminatorFromSubcommand(Class<?> configurableClass,
      Subcommand subcommand) {
    if (subcommand.discriminator().isEmpty()) {
      throw new NoDiscriminatorConfigurationException(configurableClass);
    }
    try {
      return Discriminators.fromSubcommand(subcommand);
    } catch (IllegalArgumentException e) {
      throw new InvalidDiscriminatorConfigurationException(configurableClass,
          subcommand.discriminator());
    }
  }
}
