package com.sigpwned.discourse.core.command.walk;

import static java.util.Objects.requireNonNull;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.util.Discriminators;

public class CommandWalker {

  /**
   * <p>
   * A listener for the resolvedCommand walker. This listener is called as the walker walks the
   * resolvedCommand hierarchy. For a class hierarchy like:
   * </p>
   *
   * <pre>
   *   RootCommand
   *     │
   *     ├── SubCommand1
   *     │     │
   *     │     ├── SubCommand1A
   *     │     │
   *     │     └── SubCommand1B
   *     │
   *     └── SubCommand2
   * </pre>
   *
   * <p>
   * The listener will be called in the following order:
   * </p>
   *
   * <ol>
   * <li><code>beginWalk(RootCommand)</code></li>
   * <li><code>enterClazz(RootCommand, @Configurable, {})</code></li>
   * <li><code>visitClazz(RootCommand, @Configurable, {SubCommand1, SubCommand2})</code></li>
   * <li><code>enterClazz(SubCommand1, @Configurable, {})</code></li>
   * <li><code>visitClazz(SubCommand1, @Configurable, {SubCommand1A, SubCommand1B})</code></li>
   * <li><code>enterClazz(SubCommand1A, @Configurable, {})</code></li>
   * <li><code>visitClazz(SubCommand1A, @Configurable, {})</code></li>
   * <li><code>leaveClazz(SubCommand1A, @Configurable, {})</code></li>
   * <li><code>enterClazz(SubCommand1B, @Configurable, {})</code></li>
   * <li><code>visitClazz(SubCommand1B, @Configurable, {})</code></li>
   * <li><code>leaveClazz(SubCommand1B, @Configurable, {})</code></li>
   * <li><code>leaveClazz(SubCommand1, @Configurable, {SubCommand1A, SubCommand1B})</code></li>
   * <li><code>enterClazz(SubCommand2, @Configurable, {})</code></li>
   * <li><code>visitClazz(SubCommand2, @Configurable, {})</code></li>
   * <li><code>leaveClazz(SubCommand2, @Configurable, {})</code></li>
   * <li><code>leaveClazz(RootCommand, @Configurable, {SubCommand1, SubCommand2})</code></li>
   * <li><code>endWalk(RootCommand)</code></li>
   * </ol>
   *
   * <p>
   * Broadly, the walker implements a depth-first walk of the resolvedCommand hierarchy. For a given
   * class, the class is {@link #enterClazz(String, Class, Configurable, Map) entered}, then
   * {@link #visitClazz(String, Class, Configurable, Map) visited}, and finally
   * {@link #leaveClazz(String, Class, Configurable, Map) left}. A class is visited before any of
   * its subclasses are visited. Any subclasses are visited before the parent is left.
   * </p>
   */
  public static interface Listener<T> {

    default void beginWalk(Class<T> rootClazz) {}

    default <U extends T> void enterClazz(String discriminator, Class<U> commandClazz,
        Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {}

    default <U extends T> void visitClazz(String discriminator, Class<U> commandClazz,
        Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {}

    default <U extends T> void leaveClazz(String discriminator, Class<U> commandClazz,
        Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {}

    default void endWalk(Class<T> rootClazz) {}
  }

  private final SubCommandScanner scanner;

  public CommandWalker(SubCommandScanner scanner) {
    this.scanner = requireNonNull(scanner);
  }

  private SubCommandScanner getScanner() {
    return scanner;
  }

  public <T> void walk(Class<T> clazz, CommandWalker.Listener<T> listener) {
    listener.beginWalk(clazz);
    subwalk(null, clazz, listener);
    listener.endWalk(clazz);
  }

  protected <T, U extends T> void subwalk(String discriminator, Class<U> clazz,
      CommandWalker.Listener<T> listener) {
    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new IllegalArgumentException("clazz must be a Configurable");
    }

    Map<String, Class<? extends U>> subcommands =
        getScanner().scanForSubCommands(clazz).orElseGet(Collections::emptyMap);

    if (subcommands.isEmpty()) {
      // This is a leaf node. It doesn't have to be concrete, since an abstract class with a factory
      // method is perfectly valid, so we don't check for that here. As a result, this can be either
      // a class or an interface, too, so we don't check for that here either.
    } else {
      // This is a non-leaf node. It must be abstract. It can be a class or an interface.
      if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
        throw new IllegalArgumentException(
            "Non-leaf resolvedCommand " + clazz + " must be abstract");
      }
      for (Map.Entry<String, Class<? extends U>> entry : subcommands.entrySet()) {
        String subdiscriminator = entry.getKey();
        Class<? extends U> subclazz = entry.getValue();
        if (!Discriminators.isValid(subdiscriminator)) {
          throw new IllegalArgumentException("Invalid discriminator: " + subdiscriminator);
        }
        if (!clazz.isAssignableFrom(subclazz)) {
          throw new IllegalArgumentException(
              "Subcommand " + subclazz + " is not a subclass of " + clazz);
        }
      }
    }

    listener.enterClazz(discriminator, clazz, configurable, subcommands);

    listener.visitClazz(discriminator, clazz, configurable, subcommands);

    if (!subcommands.isEmpty()) {
      for (Map.Entry<String, Class<? extends U>> subcommand : subcommands.entrySet()) {
        subwalk(subcommand.getKey(), subcommand.getValue(), listener);
      }
    }

    listener.leaveClazz(discriminator, clazz, configurable, subcommands);
  }
}
