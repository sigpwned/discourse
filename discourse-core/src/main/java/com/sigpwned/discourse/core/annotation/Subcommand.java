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
package com.sigpwned.discourse.core.annotation;



/**
 * An annotation that marks a class as a subcommand of a {@link MultiCommand}. Subcommands are
 * commands that are run by a {@link MultiCommand} based on the discriminator. Subcommands are
 * configured by a {@link Configurable} object. Note that the discriminator must be unique among all
 * subcommands of a {@link MultiCommand}. Also, the discriminator that appears in this annotation
 * must match the discriminator that appears in the subcommand's {@link Configurable @Configurable}
 * annotation.
 */
public @interface Subcommand {

  /**
   * The discriminator for the subcommand. This is the discriminator that will be used to determine
   * which subcommand to run. Must match the discriminator that appears in the subcommand's
   * {@link Configurable @Configurable} annotation. Must match regular expression
   * {@code "[a-zA-Z0-9][-a-zA-Z0-9_]*"}.
   *
   * @see MultiCommand#getSubcommands()
   * @see Discriminator
   */
  public String discriminator();

  /**
   * The class of the configurable object that configures the subcommand.
   */
  public Class<?> configurable();
}
