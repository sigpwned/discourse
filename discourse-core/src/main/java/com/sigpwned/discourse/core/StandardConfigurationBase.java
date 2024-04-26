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
package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.invocation.InvocationBuilder;
import java.util.Objects;

/**
 * A base class for configuration objects that have standard options. Provides {@code --help} and
 * {@code --version} options that are common to many command-line tools and handled appropriately by
 * {@link InvocationBuilder}
 */
public class StandardConfigurationBase {

  @FlagParameter(longName = "help", help = true, description = "Print this help message")
  private boolean help = false;

  /**
   * @return the help
   */
  public boolean isHelp() {
    return help;
  }

  /**
   * @param help the help to set
   */
  public void setHelp(boolean help) {
    this.help = help;
  }

  @FlagParameter(longName = "version", version = true, description = " The current version of this software")
  private boolean version = false;

  /**
   * @return the version
   */
  public boolean isVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(boolean version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    return Objects.hash(help, version);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StandardConfigurationBase other = (StandardConfigurationBase) obj;
    return help == other.help && version == other.version;
  }
}
