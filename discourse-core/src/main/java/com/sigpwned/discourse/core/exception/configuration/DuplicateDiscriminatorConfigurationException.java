/*-
 * =================================LICENSE_START==================================
 * discourse-core
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
package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.*;

import com.sigpwned.discourse.core.exception.ConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;

public class DuplicateDiscriminatorConfigurationException extends ConfigurationException {
  private final Discriminator discriminator;

  // TODO Add configurable class here
  public DuplicateDiscriminatorConfigurationException(Discriminator discriminator) {
    super(format("Multiple subcommands have the same discriminator %s", discriminator));
    this.discriminator = discriminator;
  }

  /**
   * @return the coordinate
   */
  public Discriminator getDiscriminator() {
    return discriminator;
  }
}
