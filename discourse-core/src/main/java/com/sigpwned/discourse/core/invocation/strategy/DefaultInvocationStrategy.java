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
package com.sigpwned.discourse.core.invocation.strategy;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import java.util.List;

/**
 * <p>
 * The default invocation strategy. This strategy is composed of the following strategies:
 * </p>
 *
 * <ol>
 *   <li>{@link SubcommandDereferencingInvocationStrategy}</li>
 *   <li>{@link HelpPrintingInvocationStrategy}</li>
 *   <li>{@link VersionPrintingInvocationStrategy}</li>
 *   <li>{@link SingleCommandInvocationStrategy}</li>
 * </ol>
 */
public class DefaultInvocationStrategy implements InvocationStrategy {

  public static final DefaultInvocationStrategy INSTANCE = new DefaultInvocationStrategy();

  private final InvocationStrategy delegate;

  public DefaultInvocationStrategy() {
    this.delegate = new SubcommandDereferencingInvocationStrategy(
        new HelpPrintingInvocationStrategy(
            new VersionPrintingInvocationStrategy(new SingleCommandInvocationStrategy())));
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, List<String> args) {
    return getDelegate().invoke(command, args);
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }
}
