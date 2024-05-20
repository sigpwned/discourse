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
import static java.util.Objects.requireNonNull;
import java.lang.reflect.Modifier;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanException;

public class SuperCommandNotAbstractScanException extends ScanException {
  private static final long serialVersionUID = 884920591000494293L;

  private final Class<?> clazz;

  public SuperCommandNotAbstractScanException(Class<?> clazz) {
    super(format("Configurable %s not abstract", clazz.getName()));
    this.clazz = requireNonNull(clazz);
    if (Modifier.isAbstract(clazz.getModifiers())) {
      // TODO Throwing an exception inside an exception constructor may not be a good idea...
      throw new IllegalArgumentException("clazz must be abstract");
    }
  }

  /**
   * @return the rawType
   */
  public Class<?> getClazz() {
    return clazz;
  }
}
