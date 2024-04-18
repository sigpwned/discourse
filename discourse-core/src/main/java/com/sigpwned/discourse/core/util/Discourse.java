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

import com.sigpwned.discourse.core.ArgumentException;
import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.invocation.strategy.SingleCommandInvocationStrategy;
import java.util.List;

public final class Discourse {

  private Discourse() {
  }

  /**
   * Creates a default command builder.
   */
  public static CommandBuilder defaultCommandBuilder() {
    return new CommandBuilder();
  }

  /**
   * Creates a default invocation strategy.
   */
  public static InvocationStrategy defaultInvocationStrategy() {
    return new SingleCommandInvocationStrategy();
  }

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
  public static <T> T configuration(Class<T> rawType, CommandBuilder b, InvocationStrategy invoker,
      String[] args) {
    return configuration(rawType, b, invoker, List.of(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, List<String> args) {
    return configuration(rawType, defaultCommandBuilder(), defaultInvocationStrategy(), args);
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, CommandBuilder b, InvocationStrategy invoker,
      List<String> args) {

    Command<T> command;
    try {
      command = b.build(rawType);
    } catch (ConfigurationException e) {
      System.err.println("There was a problem with the application configuration.");
      System.err.println("You should reach out to the application developer for help.");
      System.err.println("They may find the following information useful:");
      System.err.println("ARGUMENTS: " + args);
      System.err.println("STACK TRACE:");
      e.printStackTrace(System.err);
      throw exit(1);
    }

    T result;
    try {
      result = invoker.invoke(command, args).getConfiguration();
    } catch (SyntaxException e) {
      System.err.println("ERROR: " + e.getMessage());
      if (args.isEmpty()) {
        System.err.println(DefaultHelpFormatter.INSTANCE.formatHelp(command));
      }
      throw exit(2);
    } catch (ArgumentException e) {
      System.err.println("ERROR: " + e.getMessage());
      throw exit(3);
    } catch (RuntimeException e) {
      System.err.println("ERROR: " + e.getMessage());
      throw exit(4);
    }

    return result;
  }

  private static AssertionError exit(int code) {
    System.exit(code);
    return new AssertionError("exit");
  }
}
