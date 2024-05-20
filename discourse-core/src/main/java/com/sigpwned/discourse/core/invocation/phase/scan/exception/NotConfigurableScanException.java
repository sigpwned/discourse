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
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanException;

public class NotConfigurableScanException extends ScanException {
  private static final long serialVersionUID = -4498156206766927045L;

  private final Class<?> clazz;

  public NotConfigurableScanException(Class<?> clazz) {
    super(format("Class %s is not @Configurable", clazz.getName()));
    this.clazz = requireNonNull(clazz);
    if (clazz.getAnnotation(Configurable.class) != null) {
      // TODO Throwing an exception inside an exception constructor may not be a good idea...
      throw new IllegalArgumentException("clazz must not be @Configurable");
    }
  }

  /**
   * @return the rawType
   */
  public Class<?> getClazz() {
    return clazz;
  }
}
