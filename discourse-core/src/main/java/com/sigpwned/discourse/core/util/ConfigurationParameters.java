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
package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ConfigurationParameters {

  private ConfigurationParameters() {
  }

  public static BiConsumer<ConfigurationParameter, Consumer<FlagConfigurationParameter>> mapMultiFlag() {
    return mapMulti(FlagConfigurationParameter.class);
  }

  public static <P extends ConfigurationParameter> BiConsumer<ConfigurationParameter, Consumer<P>> mapMulti(
      Class<P> clazz) {
    return (x, d) -> {
      if (clazz.isInstance(x)) {
        d.accept(clazz.cast(x));
      }
    };
  }
}
