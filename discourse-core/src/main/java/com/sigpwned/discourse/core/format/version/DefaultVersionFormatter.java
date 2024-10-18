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

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.tree.RootCommand;
import com.sigpwned.discourse.core.format.VersionFormatter;

/**
 * <p>
 * The default version formatter. This formatter formats the version as a string with the name of
 * the resolvedCommand followed by the version of the resolvedCommand:
 * </p>
 *
 * <pre>
 *   name version
 * </pre>
 */
public class DefaultVersionFormatter implements VersionFormatter {

  public static final DefaultVersionFormatter INSTANCE = new DefaultVersionFormatter();

  @Override
  public String formatVersion(RootCommand<?> command) {
    List<String> parts = new ArrayList<>();
    if (command.getName().isPresent()) {
      parts.add(command.getName().orElseThrow());
    }
    if (command.getVersion().isPresent()) {
      parts.add(command.getVersion().orElseThrow());
    }
    return String.join(" ", parts);
  }
}
