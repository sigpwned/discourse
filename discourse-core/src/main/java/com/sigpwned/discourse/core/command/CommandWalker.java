package com.sigpwned.discourse.core.command;

import com.sigpwned.discourse.core.model.command.Discriminator;
import java.util.Map;

/**
 * <p>
 * Performs a depth-first walk of the given {@link Command}. Visits the given class first, then
 * visits all subcommands of the class, and so on.
 * </p>
 *
 * <p>
 * Methods are called on the given {@link CommandWalker.Visitor visitor} as the walker visits each
 * command. The visitor is responsible for handling the commands as they are visited. The visitor's
 * methods are called in a depth-first manner, so that the visitor will be called on the root class
 * first, then on all subcommands of the root class, then on all subcommands of the subcommands, and
 * so on.
 * </p>
 *
 * <p>
 * For example, for the given command tree:
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
 *   <li><code>enterMultiCommand(null, RootCommand)</code></li>
 *   <li><code>enterMultiCommand(discriminator1, Subcommand1)</code></li>
 *   <li><code>visitSingleCommand(discriminator1A, Subcommand1A)</code></li>
 *   <li><code>visitSingleCommand(discriminator1B, Subcommand1B)</code></li>
 *   <li><code>leaveMultiCommand(discriminator1, Subcommand1)</code></li>
 *   <li><code>visitSingleCommand(discriminator2, Subcommand2)</code></li>
 *   <li><code>enterMultiCommand(discriminator3, Subcommand3)</code></li>
 *   <li><code>visitSingleCommand(discriminator3A, Subcommand3A)</code></li>
 *   <li><code>visitSingleCommand(discriminator3B, Subcommand3B)</code></li>
 *   <li><code>visitSingleCommand(discriminator3C, Subcommand3C)</code></li>
 *   <li><code>leaveMultiCommand(discriminator3, Subcommand3)</code></li>
 *   <li><code>visitSingleCommand(discriminator4, Subcommand4)</code></li>
 *   <li><code>leaveMultiCommand(null, RootCommand)</code></li>
 * </ol>
 */
public final class CommandWalker {

  public static interface Visitor<T> {

    /**
     * Called when entering a multi-command.
     */
    default void enterMultiCommand(Discriminator discriminator, MultiCommand<? extends T> command) {

    }

    /**
     * Called when leaving a multi-command.
     */
    default void leaveMultiCommand(Discriminator discriminator, MultiCommand<? extends T> command) {

    }

    /**
     * Called when visiting a single command.
     */
    default void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {

    }
  }

  public <T> void walk(Command<T> command, Visitor<T> visitor) {
    walk(null, command, visitor);
  }

  private <T> void walk(Discriminator discriminator, Command<T> command,
      Visitor<? super T> visitor) {
    if (command instanceof MultiCommand<T> multi) {
      visitor.enterMultiCommand(discriminator, multi);
      for (Map.Entry<Discriminator, Command<? extends T>> e : multi.getSubcommands().entrySet()) {
        walk(e.getKey(), e.getValue(), visitor);
      }
      visitor.leaveMultiCommand(discriminator, multi);
    } else if (command instanceof SingleCommand<T> single) {
      visitor.singleCommand(discriminator, single);
    } else {
      throw new AssertionError(command);
    }
  }
}
