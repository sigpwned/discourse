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
package com.sigpwned.discourse.core.util;

import java.util.List;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;

public final class Args {
  private Args() {}

  /**
   * Makes a best-effort attempt to determine if the given args contain the given switches. It is
   * not -- and cannot be -- perfect because it does not know whether or not each switch is valued.
   * That would require a ConfigurationClass, and we're trying to short-circuit that here. We could
   * get some false positives if a flag is passed as a value to an option.
   */
  public static boolean containsFlag(List<String> args, ShortSwitchNameCoordinate shortName,
      LongSwitchNameCoordinate longName) {
    for (String arg : args) {
      try {
        ArgumentToken at = ArgumentToken.fromString(arg);
        switch (at.getType()) {
          case BUNDLE:
            BundleArgumentToken bat = at.asBundle();
            if (shortName != null && bat.getShortNames().contains(shortName.toString()))
              return true;
            break;
          case SHORT_NAME:
            ShortNameArgumentToken snat = at.asShortName();
            if (shortName != null && snat.getShortName().equals(shortName.toString()))
              return true;
            break;
          case LONG_NAME:
            LongNameArgumentToken lnat = at.asLongName();
            if (longName != null && lnat.getLongName().equals(longName.toString()))
              return true;
            break;
          case SEPARATOR:
            return false;
          case LONG_NAME_VALUE:
          case VALUE:
          case EOF:
          default:
            // We can safely ignore these.
            break;
        }
      } catch (Exception e) {
        // Ignore this...
      }
    }
    return false;
  }
}
