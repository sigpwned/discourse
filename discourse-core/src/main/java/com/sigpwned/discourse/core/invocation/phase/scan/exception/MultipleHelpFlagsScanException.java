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
package com.sigpwned.discourse.core.invocation.phase.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import javax.naming.ConfigurationException;

public class MultipleHelpFlagsScanException extends ConfigurationException {
  private static final long serialVersionUID = -8200927332222082693L;

  private final Class<?> clazz;
  private final Set<String> propertyNames;

  public MultipleHelpFlagsScanException(Class<?> clazz, Set<String> propertyNames) {
    super(format("Configuration %s has multiple help flag properties %s", clazz.getName(),
        propertyNames));
    this.clazz = clazz;
    this.propertyNames = unmodifiableSet(propertyNames);
    if (propertyNames.isEmpty()) {
      // TODO Throwing an exception inside an exception constructor may not be a good idea...
      throw new IllegalArgumentException("no property names");
    }
  }

  /**
   * @return the rawType
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * @return the propertyNames
   */
  public Set<String> getPropertyNames() {
    return propertyNames;
  }
}
