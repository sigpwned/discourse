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
package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.syntax.SyntaxTokenizer;

public interface Syntax {
  public SyntaxTokenizer newTokenizer();

  /**
   * <p>
   * Formats the given switch name for display, e.g., in help messages.
   * </p>
   * 
   * <p>
   * For example, a Unix-style syntax might format the switch name {@code "help"} as
   * {@code "--help"}. A Windows-style syntax might format the same switch name as {@code "/help"}.
   * </p>
   * 
   * @param name The switch name to format.
   * @return The formatted switch name.
   */
  public String formatSwitchName(SwitchName name);
}
