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

import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactoryScanner;
import com.sigpwned.discourse.core.util.Chains;
import java.util.Optional;

public class ConfigurableInstanceFactoryScannerChain extends
    Chain<ConfigurableInstanceFactoryScanner> {

  public <T> Optional<ConfigurableInstanceFactory<T>> scanForInstanceFactory(
      Class<T> type) {
    return Chains.stream(this)
        .flatMap(provider -> provider.scanForInstanceFactory(type).stream()).findFirst();
  }
}
