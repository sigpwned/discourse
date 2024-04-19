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
package com.sigpwned.discourse.core.invocation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.invocation.strategy.DefaultInvocationStrategy;
import java.util.List;

public class DefaultInvocationBuilderArgsStage<T> {

  private final InvocationContext context;
  private final Command<T> command;
  private InvocationStrategy strategy;

  public DefaultInvocationBuilderArgsStage(InvocationContext context, Command<T> command) {
    this.context = requireNonNull(context);
    this.command = requireNonNull(command);
    this.strategy = new DefaultInvocationStrategy();
  }

  public DefaultInvocationBuilderArgsStage<T> strategy(InvocationStrategy strategy) {
    this.strategy = requireNonNull(strategy);
    return this;
  }

  public Invocation<? extends T> invoke(String[] args) {
    return invoke(List.of(args));
  }

  public Invocation<? extends T> invoke(List<String> args) {
    return strategy.invoke(command, context, args);
  }
}
