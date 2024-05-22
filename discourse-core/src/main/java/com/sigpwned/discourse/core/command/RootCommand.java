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
package com.sigpwned.discourse.core.command;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;

public final class RootCommand<T> implements Command<T> {

  private final Class<T> clazz;
  private final String name;
  private final String version;
  private final String description;
  private final CommandBody<T> body;
  private final Map<String, SubCommand<? extends T>> subcommands;

  public RootCommand(Class<T> clazz, String name, String version, String description,
      CommandBody<T> body, Map<String, SubCommand<? extends T>> subcommands) {
    this.clazz = requireNonNull(clazz);
    this.name = name;
    this.version = version;
    this.description = description;
    this.body = body;
    this.subcommands = unmodifiableMap(subcommands);
  }

  @Override
  public Class<T> getClazz() {
    return clazz;
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<String> getVersion() {
    return Optional.ofNullable(version);
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Override
  public Optional<CommandBody<T>> getBody() {
    return Optional.ofNullable(body);
  }

  @Override
  public Map<String, SubCommand<? extends T>> getSubcommands() {
    return subcommands;
  }
}
