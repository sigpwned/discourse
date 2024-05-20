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
package com.sigpwned.discourse.core.invocation.phase.scan.exception.internal;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;

/**
 * Thrown when one or more syntax candidates (e.g., a {@link Configurable @Configurable} class field
 * or method) are nominated multiple times for a configuration.
 */
public class DuplicateSyntaxNomineesException extends InternalDiscourseException {
  private static final long serialVersionUID = -7861125213377616536L;

  private final Class<?> clazz;
  private final Set<Object> syntaxNominees;

  public DuplicateSyntaxNomineesException(Class<?> clazz, Set<Object> syntaxNominees) {
    super(format("Configuration %s syntax candidates %s were nominated multiple times",
        clazz.getName(), syntaxNominees));
    this.clazz = requireNonNull(clazz);
    this.syntaxNominees = unmodifiableSet(syntaxNominees);
    if (syntaxNominees.isEmpty()) {
      // TODO Throwing an exception inside an exception constructor may not be a good idea...
      throw new IllegalArgumentException("no syntax nominees");
    }
  }

  /**
   * @return the clazz
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * @return the coordinate
   */
  public Set<Object> getSyntaxNominees() {
    return syntaxNominees;
  }
}
