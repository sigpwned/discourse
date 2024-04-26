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
package com.sigpwned.discourse.core.format.version;

import com.sigpwned.discourse.core.command.Command;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The default version formatter. This formatter formats the version as a string with the name of
 * the command followed by the version of the command:
 * </p>
 *
 * <pre>
 *   name version
 * </pre>
 */
public class DefaultVersionFormatter implements VersionFormatter {

  public static final DefaultVersionFormatter INSTANCE = new DefaultVersionFormatter();

  @Override
  public String formatVersion(Command<?> command) {
    List<String> parts = new ArrayList<>();
    if (command.getName() != null) {
      parts.add(command.getName());
    }
    if (command.getVersion() != null) {
      parts.add(command.getVersion());
    }
    return String.join(" ", parts);
  }
}
