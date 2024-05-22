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
package com.sigpwned.discourse.core.invocation.phase.eval.exception;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseException;

public class ReduceFailedEvalPhaseException extends EvalPhaseException {
  private static final long serialVersionUID = -7910177844211108869L;

  private final String propertyName;
  private final List<Object> propertyValues;

  public ReduceFailedEvalPhaseException(String propertyName, List<Object> propertyValues,
      Throwable cause) {
    super("Failed to reduce property %s values %s".formatted(propertyName, propertyValues), cause);
    this.propertyName = requireNonNull(propertyName);
    this.propertyValues = unmodifiableList(propertyValues);
  }

  public String getPropertyName() {
    return propertyName;
  }

  public List<Object> getPropertyValues() {
    return propertyValues;
  }
}
