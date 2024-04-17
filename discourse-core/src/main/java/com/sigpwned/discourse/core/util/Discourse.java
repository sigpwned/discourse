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
package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import java.util.List;
import com.sigpwned.discourse.core.ArgumentException;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;

public final class Discourse {
  private Discourse() {}

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, String[] args) {
    return configuration(rawType, asList(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, CommandBuilder b, String[] args) {
    return configuration(rawType, b, asList(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, List<String> args) {
    return configuration(rawType, new CommandBuilder(), args);
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, CommandBuilder b, List<String> args) {
    Command<T> command;
    try {
      command = b.build(rawType);
    } catch (ConfigurationException e) {
      e.printStackTrace(System.err);
      System.exit(1);
      throw new AssertionError("exit");
    }

    T result;
    try {
      result = command.args(args).printHelp().printVersion().configuration();
    } catch (SyntaxException e) {
      System.err.println("ERROR: " + e.getMessage());
      if (args.isEmpty())
        System.err.println(DefaultHelpFormatter.INSTANCE);
      System.exit(2);
      throw new AssertionError("exit");
    } catch (ArgumentException e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(3);
      throw new AssertionError("exit");
    } catch (RuntimeException e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(4);
      throw new AssertionError("exit");
    }

    return result;
  }
}
