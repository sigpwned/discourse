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
package com.sigpwned.discourse.core.args;

import static java.util.Objects.requireNonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArgumentType {
  private static final Map<String, ArgumentType> VALUES = new HashMap<>();

  public static synchronized ArgumentType valueOf(String name) {
    return VALUES.computeIfAbsent(name, ArgumentType::new);
  }

  /**
   * A flag argument type. A boolean-valued parameter whose value is given by the flag's presence or
   * absence on the command line. For example, {@code --help} or {@code -h}.
   */
  public static final ArgumentType FLAG = ArgumentType.valueOf("flag");

  /**
   * A value argument type. A parameter that takes a value. For example, {@code --output file.txt}.
   */
  public static final ArgumentType OPTION = ArgumentType.valueOf("value");

  /**
   * A positional argument type. A parameter whose value is given by its position on the command
   * line. For example, {@code command arg1 arg2}.
   */
  public static final ArgumentType POSITIONAL = ArgumentType.valueOf("positional");

  private final String name;

  private ArgumentType(String name) {
    this.name = requireNonNull(name);
  }

  public String name() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ArgumentType other = (ArgumentType) obj;
    return Objects.equals(name, other.name);
  }
}
