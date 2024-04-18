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
package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A default implementation of {@link Invocation}.
 *
 * @param <T> the type of the configuration object
 */
public class DefaultInvocation<T> implements Invocation<T> {

  private final List<Map.Entry<Discriminator, MultiCommand<? extends T>>> subcommands;
  private final SingleCommand<? extends T> leafCommand;
  private final List<String> leafArgs;
  private final T configuration;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public DefaultInvocation(List<Entry<Discriminator, MultiCommand<? extends T>>> subcommands,
      SingleCommand<? extends T> leafCommand, List<String> leafArgs, T configuration) {
    this.subcommands = (List) subcommands.stream().map(e -> Map.entry(e.getKey(), e.getValue()))
        .toList();
    this.leafCommand = requireNonNull(leafCommand);
    this.leafArgs = unmodifiableList(leafArgs);
    this.configuration = requireNonNull(configuration);
  }

  @Override
  public T getConfiguration() {
    return configuration;
  }

  @Override
  public List<Entry<Discriminator, MultiCommand<? extends T>>> getSubcommands() {
    return subcommands;
  }

  @Override
  public SingleCommand<? extends T> getLeafCommand() {
    return leafCommand;
  }

  @Override
  public List<String> getLeafArgs() {
    return leafArgs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DefaultInvocation<?> that)) {
      return false;
    }
    return Objects.equals(getSubcommands(), that.getSubcommands())
        && Objects.equals(getLeafCommand(), that.getLeafCommand())
        && Objects.equals(getLeafArgs(), that.getLeafArgs()) && Objects.equals(
        getConfiguration(), that.getConfiguration());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSubcommands(), getLeafCommand(), getLeafArgs(), getConfiguration());
  }

  @Override
  public String toString() {
    return "DefaultInvocation{" +
        "subcommands=" + subcommands +
        ", leafCommand=" + leafCommand +
        ", leafArgs=" + leafArgs +
        ", configuration=" + configuration +
        '}';
  }
}
