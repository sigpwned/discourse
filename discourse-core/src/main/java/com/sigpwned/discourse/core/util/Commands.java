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
package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Commands {

  private Commands() {
  }

  /**
   * Returns a stream of all parameters in the given command and all subcommands, if any exist. The
   * stream is not deduplicated. If one parameter appears in multiple subcommands, it will appear
   * multiple times in the stream.
   *
   * @param command the command
   * @return a stream of all parameters in the given command
   */
  public static Stream<ConfigurationParameter> deepParameters(Command<?> command) {
    if (command instanceof SingleCommand<?> single) {
      return single.getParameters().stream();
    } else if (command instanceof MultiCommand<?> multi) {
      return multi.getSubcommands().values().stream().flatMap(Commands::deepParameters);
    }
    throw new AssertionError("unrecognized command type: " + command.getClass().getName());
  }


  /**
   * Returns a set of all parameters that are common to all subcommands of the given multi-command.
   * A parameter is common if it appears in every subcommand.
   *
   * @param multi the multi-command
   */
  public static Set<ConfigurationParameter> commonParameters(MultiCommand<?> multi) {
    return deepParameters(multi).collect(
            Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
        .filter(e -> e.getValue() == multi.getSubcommands().size()).map(Map.Entry::getKey)
        .collect(Collectors.toUnmodifiableSet());
  }

  /**
   * @see #getRootCommandRawType(Class)
   */
  public static <T> Class<? super T> getRootCommandRawType(Command<T> command) {
    return getRootCommandRawType(command.getRawType());
  }

  /**
   * Returns the raw type of the "root" command of the give command. The root command is the highest
   * superclass of the given command that is annotated with {@link Configurable}.
   *
   * @param rawType the raw type
   * @param <T>     the type of the raw type
   * @return the root command raw type of the given raw type
   * @throws IllegalArgumentException if the given raw type is not configurable
   */
  public static <T> Class<? super T> getRootCommandRawType(Class<T> rawType) {
    if (rawType.getAnnotation(Configurable.class) == null) {
      throw new IllegalArgumentException("Class is not configurable");
    }

    Class<? super T> result = rawType;
    while (result.getSuperclass().getAnnotation(Configurable.class) != null) {
      result = result.getSuperclass();
    }

    return result;
  }
}
