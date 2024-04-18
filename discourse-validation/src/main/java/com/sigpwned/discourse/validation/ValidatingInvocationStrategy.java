/*-
 * =================================LICENSE_START==================================
 * discourse-validation
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
package com.sigpwned.discourse.validation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.invocation.strategy.DefaultInvocationStrategy;
import com.sigpwned.discourse.validation.util.Validation;
import java.util.List;
import javax.validation.Validator;

/**
 * An invocation strategy that validates the configuration object using Jakarta validation before
 * returning it.
 */
public class ValidatingInvocationStrategy implements InvocationStrategy {

  private final InvocationStrategy delegate;
  private final Validator validator;

  public ValidatingInvocationStrategy() {
    this(new DefaultInvocationStrategy());
  }

  public ValidatingInvocationStrategy(InvocationStrategy delegate) {
    this(delegate, Validation.defaultValidator());
  }


  public ValidatingInvocationStrategy(InvocationStrategy delegate, Validator validator) {
    this.delegate = requireNonNull(delegate);
    this.validator = requireNonNull(validator);
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args) {
    Invocation<? extends T> invocation = getDelegate().invoke(command, context, args);
    return new ValidatingInvocation<>(invocation, getValidator());
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }

  private Validator getValidator() {
    return validator;
  }
}
