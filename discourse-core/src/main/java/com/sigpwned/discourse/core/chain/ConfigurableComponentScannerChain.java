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

import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.scanner.ConfigurableComponentScanner;
import com.sigpwned.discourse.core.util.Chains;
import java.util.List;

/**
 * A {@link ConfigurableComponentScanner} implementation that delegates to a chain of
 * {@code ConfigurableComponentScanner} instances. Each link in the chain is consulted in order and
 * the results are concatenated.
 */
public class ConfigurableComponentScannerChain extends
    Chain<ConfigurableComponentScanner> implements ConfigurableComponentScanner {

  public List<ConfigurableComponent> scanForComponents(Class<?> rawType) {
    return Chains.stream(this)
        .flatMap(scanner -> scanner.scanForComponents(rawType).stream()).toList();
  }
}
