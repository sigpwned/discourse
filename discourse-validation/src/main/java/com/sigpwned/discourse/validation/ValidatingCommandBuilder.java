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

import static java.util.stream.Collectors.toMap;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.validation.command.ValidatingMultiCommand;
import com.sigpwned.discourse.validation.command.ValidatingSingleCommand;

public class ValidatingCommandBuilder extends CommandBuilder {
  @Override
  public ValidatingCommandBuilder register(Module module) {
    return (ValidatingCommandBuilder) super.register(module);
  }

  @Override
  public <T> Command<T> build(Class<T> rawType) {
    Command<T> result = super.build(rawType);
    switch (result.getType()) {
      case MULTI:
        MultiCommand<T> multi = (MultiCommand<T>) result;
        return new ValidatingMultiCommand<T>(multi.getName(), multi.getDescription(),
            multi.getVersion(), multi.listSubcommands().stream()
                .collect(toMap(d -> d, d -> multi.getSubcommand(d).get())));
      case SINGLE:
        SingleCommand<T> single = (SingleCommand<T>) result;
        return new ValidatingSingleCommand<T>(single.getConfigurationClass());
      default:
        throw new AssertionError("Unrecognized command type " + result.getType());
    }
  }
}
