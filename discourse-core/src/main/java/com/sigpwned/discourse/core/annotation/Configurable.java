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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import com.sigpwned.discourse.core.command.tree.Command;

/**
 * An annotation that marks a class as a configurable object. Configurable objects can be used to
 * create {@link Command} objects.
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface Configurable {

  /**
   * The optional name of the configurable object. This is the name that will be used to refer to
   * the application in help messages and other output.
   */
  public String name() default "";

  /**
   * The version of the configurable object. This is the version that will be used to describe which
   * version of the application is running in help messages and other output.
   */
  public String version() default "";

  /**
   * The discriminator for the configurable object. This is the discriminator that will be used to
   * determine which subcommand to run. Only subcommands of a {@link MultiCommand} should have a
   * discriminator.
   *
   * @see MultiCommand#getSubcommands()
   */
  public String discriminator() default "";

  /**
   * The subcommands of this configurable object. This list "points" to this logical
   * resolvedCommand's subcommands and is used to create a {@link MultiCommand} object.
   *
   * @see MultiCommand#getSubcommands()
   */
  public Subcommand[] subcommands() default {};
}
