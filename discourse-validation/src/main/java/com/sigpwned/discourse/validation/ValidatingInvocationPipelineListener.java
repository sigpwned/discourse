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
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import com.sigpwned.discourse.core.command.tree.Command;
import com.sigpwned.discourse.core.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.validation.exception.argument.ValidationArgumentException;
import com.sigpwned.discourse.validation.util.Validation;

/**
 * An invocation that validates the configuration object using Jakarta validation before returning
 * it.
 */
public class ValidatingInvocationPipelineListener implements InvocationPipelineListener {
  public static Validator defaultValidator() {
    return Validation.defaultValidator();
  }

  private final Validator validator;

  public ValidatingInvocationPipelineListener() {
    this(defaultValidator());
  }

  public ValidatingInvocationPipelineListener(Validator validator) {
    this.validator = requireNonNull(validator);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> void afterFactoryPhase(Command<T> resolvedCommand, Map<String, Object> state,
      T instance) {
    Set<ConstraintViolation<T>> violations = getValidator().validate(instance);
    if (!violations.isEmpty()) {
      throw new ValidationArgumentException((Set) violations);
    }
  }

  /**
   * @return the validator
   */
  private Validator getValidator() {
    return validator;
  }
}
