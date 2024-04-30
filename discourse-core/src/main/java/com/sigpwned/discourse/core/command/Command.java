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
package com.sigpwned.discourse.core.command;

/**
 * <p>
 * A command is a blueprint for creating a configurable object from command line arguments and
 * guiding users to provide the right command line arguments. Specifically, it captures the type of
 * configuration class to create, the name of the command, the description of the command, the
 * version of the command, and any command line or environmental parameters needed to build the
 * configuration object.
 * </p>
 *
 * <p>
 * A command can be a {@link SingleCommand single} or {@link MultiCommand multi} command. A single
 * command is a standalone command that simply takes arguments. A multi command is a "container"
 * command that can run any one of multiple subcommands based on a discriminator. This mode is
 * modeled after the AWS CLI and Git CLI.
 * </p>
 *
 * @param <T> The type of the root configuration class.
 */
public abstract sealed class Command<T> permits SingleCommand, MultiCommand {

  private final String name;
  private final String description;
  private final String version;

  /**
   * Creates a new command.
   *
   * @param name        The name of the command.
   * @param description The description of the command.
   * @param version     The version of the command.
   */
  public Command(String name, String description, String version) {
    this.name = name;
    this.description = description;
    this.version = version;
  }

  public abstract Class<T> getRawType();

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }
}
