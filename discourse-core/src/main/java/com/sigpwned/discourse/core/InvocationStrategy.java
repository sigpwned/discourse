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
package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.command.Command;
import java.util.List;

/**
 * A strategy for building configuration objects. Specifically, this is used to create the
 * configuration instance from a {@link Command} and a list of command line arguments.
 */
public interface InvocationStrategy {

  /**
   * Creates an invocation for the given command, context, and arguments.
   *
   * @param command the command to invoke
   * @param context the invocation context, which provides sinks, deserializers, etc.
   * @param args    the arguments to the command
   * @param <T>     the type of the command's result
   * @return the invocation
   * @throws SyntaxException   if the command line arguments cannot be parsed, for example because
   *                           an option that requires a value was not given one
   * @throws ArgumentException if the arguments are not make valid for the command, for example
   *                           because they cannot be deserialized.
   */
  <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args);
}
