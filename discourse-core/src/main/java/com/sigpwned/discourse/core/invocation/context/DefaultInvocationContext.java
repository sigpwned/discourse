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
package com.sigpwned.discourse.core.invocation.context;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A default implementation of {@link InvocationContext}. The default values for the required keys
 * are as follows:
 * </p>
 *
 * <ul>
 *   <li>{@link InvocationContext#HELP_FORMATTER_KEY} - {@link DefaultHelpFormatter#INSTANCE}</li>
 *   <li>{@link InvocationContext#VERSION_FORMATTER_KEY} - {@link DefaultVersionFormatter#INSTANCE}</li>
 *   <li>{@link InvocationContext#ERROR_STREAM_KEY} - {@link System#err}</li>
 * </ul>
 */
public class DefaultInvocationContext implements InvocationContext {

  private final Map<String, Object> values;

  public DefaultInvocationContext() {
    this.values = new HashMap<>();
    this.values.put(InvocationContext.HELP_FORMATTER_KEY, DefaultHelpFormatter.INSTANCE);
    this.values.put(InvocationContext.VERSION_FORMATTER_KEY, DefaultVersionFormatter.INSTANCE);
    this.values.put(InvocationContext.ERROR_STREAM_KEY, System.err);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> Optional<T> get(String key) {
    return (Optional) Optional.ofNullable(getValues().get(key));
  }

  @Override
  public void set(String key, Object value) {
    if (value == null) {
      getValues().put(key, value);
    } else {
      getValues().remove(key);
    }
  }

  private Map<String, Object> getValues() {
    return values;
  }
}
