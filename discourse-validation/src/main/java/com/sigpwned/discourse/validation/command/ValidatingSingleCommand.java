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
package com.sigpwned.discourse.validation.command;

import java.util.List;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.validation.ValidatingInvocation;

public class ValidatingSingleCommand<T> extends SingleCommand<T> {
  public ValidatingSingleCommand(ConfigurationClass configurationClass) {
    super(configurationClass);
  }

  @Override
  protected Invocation<T> newInvocation(ConfigurationClass configurationClass, List<String> args) {
    return new ValidatingInvocation<T>(this, configurationClass, args);
  }
}
