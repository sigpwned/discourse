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
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanException;

/**
 *
 */
public class DiscriminatorMismatchConfigurationException extends ScanException {
  private static final long serialVersionUID = 8577338787358628238L;

  private final Class<?> clazz;
  private final Discriminator expectedDiscriminator;
  private final Discriminator actualDiscriminator;

  public DiscriminatorMismatchConfigurationException(Class<?> clazz,
      Discriminator expectedDiscriminator, Discriminator actualDiscriminator) {
    super(format("Configuration %s should have discriminator %s, but has discriminator %s",
        clazz.getName(), expectedDiscriminator, actualDiscriminator));
    this.clazz = clazz;
    this.expectedDiscriminator = expectedDiscriminator;
    this.actualDiscriminator = actualDiscriminator;
  }

  /**
   * @return the rawType
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * @return the expectedDiscriminator
   */
  public Discriminator getExpectedDiscriminator() {
    return expectedDiscriminator;
  }

  /**
   * @return the actualDiscriminator
   */
  public Discriminator getActualDiscriminator() {
    return actualDiscriminator;
  }
}
