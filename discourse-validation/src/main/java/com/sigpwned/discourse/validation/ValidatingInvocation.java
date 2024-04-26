/*-
 * =================================LICENSE_START==================================
 * discourse-validation
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
package com.sigpwned.discourse.validation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.validation.exception.argument.ValidationArgumentException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * An invocation that validates the configuration object using Jakarta validation before returning
 * it.
 *
 * @param <T>
 */
public class ValidatingInvocation<T> implements Invocation<T> {

  private final Invocation<T> delegate;
  private final Validator validator;

  public ValidatingInvocation(Invocation<T> delegate, Validator validator) {
    this.delegate = requireNonNull(delegate);
    this.validator = requireNonNull(validator);
  }

  @Override
  public T getConfiguration() {
    T result = getDelegate().getConfiguration();
    @SuppressWarnings({"unchecked",
        "rawtypes"}) Set<ConstraintViolation<?>> violations = (Set) getValidator().validate(result);
    if (!violations.isEmpty()) {
      throw new ValidationArgumentException(violations);
    }
    return result;
  }

  @Override
  public List<Entry<Discriminator, MultiCommand<? extends T>>> getSubcommands() {
    return getDelegate().getSubcommands();
  }

  @Override
  public SingleCommand<? extends T> getLeafCommand() {
    return getDelegate().getLeafCommand();
  }

  @Override
  public List<String> getLeafArgs() {
    return getDelegate().getLeafArgs();
  }

  /**
   * @return the delegate
   */
  private Invocation<T> getDelegate() {
    return delegate;
  }

  /**
   * @return the validator
   */
  private Validator getValidator() {
    return validator;
  }
}
