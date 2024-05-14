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

import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.model.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.args.impl.model.token.ArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.BundleArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.ShortNameArgumentToken;
import java.util.List;

public final class Args {

  private Args() {
  }

  /**
   * Makes a best-effort attempt to determine if the given args contain the given switches. It is
   * not -- and cannot be -- perfect because it does not know whether or not each switch is valued,
   * i.e., takes a value. Knowing this would require a {@link SingleCommand}, and we're trying to
   * short-circuit that here. We could get some false positives if a flag is passed as a value to an
   * option, but the user can always put in a separator to disambiguate.
   */
  public static boolean containsFlag(List<String> args, ShortSwitchNameCoordinate shortName,
      LongSwitchNameCoordinate longName) {
    for (String arg : args) {
      try {
        ArgumentToken at = ArgumentToken.fromString(arg);
        if (at instanceof BundleArgumentToken bat) {
          if (shortName != null && bat.getShortNames().contains(shortName.getText())) {
            return true;
          }
        } else if (at instanceof LongNameArgumentToken lnat) {
          if (longName != null && lnat.getLongName().equals(longName.getText())) {
            return true;
          }
        } else if (at instanceof ShortNameArgumentToken snat) {
          if (shortName != null && snat.getShortName().equals(shortName.getText())) {
            return true;
          }
        } else if (at instanceof SeparatorArgumentToken sat) {
          return false;
        }
      } catch (Exception e) {
        // Ignore this...
      }
    }
    return false;
  }
}
