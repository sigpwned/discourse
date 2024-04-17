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

import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.annotation.Configurable;
import java.util.Optional;

public final class Discriminators {

  private Discriminators() {
  }

  /**
   * Extracts the discriminator from a configurable.
   *
   * @param configurable the configurable
   * @return the discriminator
   */
  public static Optional<Discriminator> fromConfigurable(Configurable configurable) {
    if (configurable.discriminator().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Discriminator.fromString(configurable.discriminator()));
  }
}
