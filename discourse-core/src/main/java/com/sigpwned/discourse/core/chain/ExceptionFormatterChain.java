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

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.format.exception.DefaultExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.ExceptionFormatter;
import com.sigpwned.discourse.core.util.Chains;

/**
 * A chain of {@link ExceptionFormatter} instances. This chain is used to determine which
 * {@code ExceptionFormatter} to use for a given exception. The chain is searched in order, and the
 * first {@code ExceptionFormatter} that handles the exception is used. If no
 * {@code ExceptionFormatter} in the chain handles the exception, then the default
 * {@code ExceptionFormatter} is used.
 */
public class ExceptionFormatterChain extends Chain<ExceptionFormatter> {

  private ExceptionFormatter defaultFormatter;

  public ExceptionFormatterChain() {
    this(DefaultExceptionFormatter.INSTANCE);
  }

  public ExceptionFormatterChain(ExceptionFormatter defaultFormatter) {
    this.defaultFormatter = requireNonNull(defaultFormatter);
  }

  public ExceptionFormatter getExceptionFormatter(Throwable e, InvocationContext context) {
    return Chains.stream(this).filter(formatter -> formatter.handlesException(e, context))
        .findFirst().orElseGet(this::getDefaultFormatter);
  }

  public ExceptionFormatter getDefaultFormatter() {
    return defaultFormatter;
  }

  public void setDefaultFormatter(ExceptionFormatter defaultFormatter) {
    this.defaultFormatter = requireNonNull(defaultFormatter);
  }
}
