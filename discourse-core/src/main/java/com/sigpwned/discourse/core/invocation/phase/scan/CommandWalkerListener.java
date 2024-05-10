package com.sigpwned.discourse.core.invocation.phase.scan;

import com.sigpwned.discourse.core.annotation.Configurable;
import java.util.Map;

/**
 * <p>
 * A listener for the command walker. This listener is called as the walker walks the command
 * hierarchy. For a class hierarchy like:
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
 *   <li><code>beginWalk(RootCommand)</code></li>
 *   <li><code>enterClazz(RootCommand, @Configurable, {})</code></li>
 *   <li><code>visitClazz(RootCommand, @Configurable, {SubCommand1, SubCommand2})</code></li>
 *   <li><code>enterClazz(SubCommand1, @Configurable, {})</code></li>
 *   <li><code>visitClazz(SubCommand1, @Configurable, {SubCommand1A, SubCommand1B})</code></li>
 *   <li><code>enterClazz(SubCommand1A, @Configurable, {})</code></li>
 *   <li><code>visitClazz(SubCommand1A, @Configurable, {})</code></li>
 *   <li><code>leaveClazz(SubCommand1A, @Configurable, {})</code></li>
 *   <li><code>enterClazz(SubCommand1B, @Configurable, {})</code></li>
 *   <li><code>visitClazz(SubCommand1B, @Configurable, {})</code></li>
 *   <li><code>leaveClazz(SubCommand1B, @Configurable, {})</code></li>
 *   <li><code>leaveClazz(SubCommand1, @Configurable, {SubCommand1A, SubCommand1B})</code></li>
 *   <li><code>enterClazz(SubCommand2, @Configurable, {})</code></li>
 *   <li><code>visitClazz(SubCommand2, @Configurable, {})</code></li>
 *   <li><code>leaveClazz(SubCommand2, @Configurable, {})</code></li>
 *   <li><code>leaveClazz(RootCommand, @Configurable, {SubCommand1, SubCommand2})</code></li>
 *   <li><code>endWalk(RootCommand)</code></li>
 * </ol>
 *
 * <p>
 *   Broadly, the walker implements a depth-first walk of the command hierarchy. For a given class,
 *   the class is {@link #enterClazz(String, Class, Configurable, Map) entered}, then
 *   {@link #visitClazz(String, Class, Configurable, Map) visited}, and finally
 *   {@link #leaveClazz(String, Class, Configurable, Map) left}. A class is visited before any of
 *   its subclasses are visited. Any subclasses are visited before the parent is left.
 * </p>
 */
public interface CommandWalkerListener<T> {

  default void beginWalk(Class<T> rootClazz) {
  }

  default <U extends T> void enterClazz(String discriminator, Class<U> commandClazz,
      Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {
  }

  default <U extends T> void visitClazz(String discriminator, Class<U> commandClazz,
      Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {
  }

  default <U extends T> void leaveClazz(String discriminator, Class<U> commandClazz,
      Configurable configurable, Map<String, Class<? extends U>> subcommandClazzes) {
  }

  default void endWalk(Class<T> rootClazz) {
  }
}
