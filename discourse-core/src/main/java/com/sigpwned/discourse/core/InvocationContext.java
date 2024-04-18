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

import java.util.Optional;

public interface InvocationContext {

  /**
   * The key for the help formatter. The value should be an instance of {@link HelpFormatter}. All
   * InvocationContext implementations must provide a default value for this key, though the user
   * may overwrite it later.
   */
  public static final String HELP_FORMATTER_KEY = "discourse.HelpFormatter";

  /**
   * The key for the version formatter. The value should be an instance of {@link VersionFormatter}.
   * All InvocationContext implementations must provide a default value for this key, though the
   * user may overwrite it later.
   */
  public static final String VERSION_FORMATTER_KEY = "discourse.VersionFormatter";

  /**
   * The key for the output stream to which to write error information. The value should be an
   * instance of {@link java.io.PrintStream}. All InvocationContext implementations must provide a
   * default value for this key, though the user may overwrite it later.
   */
  public static final String ERROR_STREAM_KEY = "discourse.ErrorStream";

  public void set(String key, Object value);

  public <T> Optional<T> get(String key);
}
