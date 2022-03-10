package com.sigpwned.discourse.core.util;

import java.util.List;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
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
