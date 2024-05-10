package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.invocation.phase.scan.CommandScanner;
import com.sigpwned.discourse.core.invocation.phase.scan.CommandWalker;
import com.sigpwned.discourse.core.invocation.phase.scan.CommandWalkerListener;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommand;
import com.sigpwned.discourse.core.configurable3.ConfigurableClass;
import com.sigpwned.discourse.core.configurable3.ConfigurableClassScanner;
import com.sigpwned.discourse.core.configurable3.RulesEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class DefaultCommandScanner implements CommandScanner {

  private static record EnteredCommand<T>(Class<T> clazz, String name, String version,
      String description, Function<Map<String, Object>, ? extends T> body,
      Map<String, SubCommand<? extends T>> subclasses) {

  }

  private final CommandWalker walker;
  private final ConfigurableClassScanner scanner;
  private final RulesEngine engine;

  public DefaultCommandScanner(CommandWalker walker, ConfigurableClassScanner scanner,
      RulesEngine engine) {
    this.walker = requireNonNull(walker);
    this.scanner = requireNonNull(scanner);
    this.engine = requireNonNull(engine);
  }

  @Override
  public <T> RootCommand<T> scanForCommand(Class<T> clazz) {
    final AtomicReference<RootCommand<? extends T>> result = new AtomicReference<>();
    walker.walk(clazz, new CommandWalkerListener<T>() {
      private final Stack<EnteredCommand<? extends T>> stack = new Stack<>();

      @Override
      public <U extends T> void enterClazz(String discriminator, Class<U> commandClazz,
          Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {
        String name = Optional.of(configurable.name()).filter(not(String::isBlank)).orElse(null);
        String version = Optional.of(configurable.version()).filter(not(String::isBlank))
            .orElse(null);
        String description = Optional.of(configurable.description()).filter(not(String::isBlank))
            .orElse(null);
        if (discriminator == null) {
          // This is a root command
          if (name == null) {
            // TODO log
          }
          if (version == null) {
            // TODO log
          }
        } else {
          // This is a subcommand
          if (name != null) {
            // TODO log
            name = null;
          }
          if (version != null) {
            // TODO log
            version = null;
          }
        }
        if (description == null) {
          // TODO log
        }

        Function<Map<String, Object>, ? extends U> body = null;
        Map<String, SubCommand<? extends U>> subcommands = emptyMap();
        if (subcommandClazzes.isEmpty()) {
          ConfigurableClass<U> scanned = scanner.scan(commandClazz);
          body = (state) -> commandClazz.cast(engine.run(state, scanned.getRules()));
        } else {
          subcommands = new HashMap<>();
        }

        stack.push(
            new EnteredCommand<>(commandClazz, name, version, description, body, subcommands));
      }

      @Override
      public <U extends T> void leaveClazz(String discriminator, Class<U> commandClazz,
          Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {
        EnteredCommand<U> entered = (EnteredCommand<U>) stack.pop();
        if (stack.isEmpty()) {
          // This is the root command
          result.set(
              new RootCommand<>(entered.clazz, entered.name, entered.version, entered.description,
                  entered.body, entered.subclasses));
        } else {
          // This is a subcommand
          EnteredCommand<T> parent = (EnteredCommand<T>) stack.peek();
          parent.subclasses.put(configurable.discriminator(),
              new SubCommand<>(entered.clazz, configurable.discriminator(),
                  configurable.description(), entered.body, entered.subclasses));
        }
      }
    });
    return (RootCommand<T>) result.get();
  }
}
