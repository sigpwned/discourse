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
package com.sigpwned.discourse.core.chain;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.listener.DiscourseListener;
import com.sigpwned.discourse.core.model.argument.DeserializedArgument;
import com.sigpwned.discourse.core.model.argument.ParsedArgument;
import com.sigpwned.discourse.core.model.argument.PreparedArgument;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import java.util.List;

/**
 * A {@link DiscourseListener} implementation that delegates to a chain of {@code DiscourseListener}
 * instances. Each link in the chain is informed of events in order. If one link in the chain throws
 * an exception, then subsequent links are not informed and the exception is propagated.
 */
public class DiscourseListenerChain extends Chain<DiscourseListener> implements DiscourseListener {

  @Override
  public void beforeScan(Class<?> clazz, InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeScan(clazz, context);
    }
  }

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterScan(rootCommand, context);
    }
  }

  @Override
  public <T> void beforeResolve(Command<T> rootCommand, List<String> args,
      InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeResolve(rootCommand, args, context);
    }
  }

  @Override
  public <T> void beforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs,
      InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeParse(rootCommand, dereferencedCommands, resolvedCommand,
          remainingArgs, context);
    }
  }

  @Override
  public <T> void beforeDeserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments,
      InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeDeserialize(rootCommand, dereferencedCommands, resolvedCommand,
          parsedArguments, context);
    }
  }

  @Override
  public <T> void beforePrepare(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<DeserializedArgument> deserializedArguments,
      InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforePrepare(rootCommand, dereferencedCommands, resolvedCommand,
          deserializedArguments, context);
    }
  }

  @Override
  public <T> void beforeBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeBuild(rootCommand, dereferencedCommands, resolvedCommand,
          sinkedArguments, context);
    }
  }

  @Override
  public <T> void afterBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      T instance, InvocationContext context) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterBuild(rootCommand, dereferencedCommands, resolvedCommand,
          sinkedArguments, instance, context);
    }
  }
}
