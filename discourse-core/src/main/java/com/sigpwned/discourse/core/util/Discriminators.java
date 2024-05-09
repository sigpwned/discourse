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

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.exception.configuration.InvalidDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import java.util.Optional;
import java.util.regex.Pattern;

public final class Discriminators {

  private Discriminators() {
  }

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9](?:[-._]?[a-zA-Z0-9])*");

  public static boolean isValid(String s) {
    return PATTERN.matcher(s).matches();
  }

  public static final Discriminator HELP = Discriminator.fromString("help");

  /**
   * Extracts the discriminator from a configurable, if it exists.
   *
   * @param configurable the configurable
   * @return the discriminator if it exists, otherwise {@link Optional#empty()}
   * @throws InvalidDiscriminatorConfigurationException if the discriminator is invalid
   */
  public static Optional<Discriminator> fromConfigurable(Configurable configurable) {
    if (configurable.discriminator().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Discriminator.fromString(configurable.discriminator()));
  }

  /**
   * Extracts the discriminator from a subcommand.
   *
   * @param subcommand the configurable
   * @return the discriminator
   * @throws InvalidDiscriminatorConfigurationException if the discriminator is invalid
   */
  public static Discriminator fromSubcommand(Subcommand subcommand) {
    return Discriminator.fromString(subcommand.discriminator());
  }
}
