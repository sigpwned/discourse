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

import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.validation.exception.argument.ValidationArgumentException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidatingInvocation<T> extends Invocation<T> {
  public static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY =
      Validation.buildDefaultValidatorFactory();

  private final Validator validator;

  public ValidatingInvocation(Command<T> command, ConfigurationClass configurationClass,
      List<String> args) {
    this(command, configurationClass, args, DEFAULT_VALIDATOR_FACTORY.getValidator());
  }

  public ValidatingInvocation(Command<T> command, ConfigurationClass configurationClass,
      List<String> args, Validator validator) {
    super(command, configurationClass, args);
    this.validator = validator;
  }

  @Override
  public T configuration() {
    T result = super.configuration();
    @SuppressWarnings({"unchecked", "rawtypes"})
    Set<ConstraintViolation<?>> violations = (Set) getValidator().validate(result);
    if (!violations.isEmpty())
      throw new ValidationArgumentException(violations);
    return result;
  }

  /**
   * @return the validator
   */
  private Validator getValidator() {
    return validator;
  }
}
