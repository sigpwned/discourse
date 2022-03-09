package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.ArgumentException;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Configurator;
import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;

public class Configurations {
  public static <T> T configure(Class<T> rawType, String[] args) {
    return configure(rawType, asList(args));
  }

  public static <T> T configure(Class<T> rawType, List<String> args) {
    Command<T> command = new Configurator<>(rawType).done();

    Optional<FlagConfigurationParameter> maybeHelpFlag = command.getParameters().stream()
        .filter(p -> p.getType() == ConfigurationParameter.Type.FLAG)
        .map(ConfigurationParameter::asFlag).filter(FlagConfigurationParameter::isHelp).findFirst();

    Optional<FlagConfigurationParameter> maybeVersionFlag = command.getParameters().stream()
        .filter(p -> p.getType() == ConfigurationParameter.Type.FLAG)
        .map(ConfigurationParameter::asFlag).filter(FlagConfigurationParameter::isVersion)
        .findFirst();

    boolean printedHelp = false;
    if (maybeHelpFlag.isPresent()) {
      FlagConfigurationParameter helpFlag = maybeHelpFlag.get();
      if (containsFlag(args, helpFlag.getShortName(), helpFlag.getLongName())) {
        printHelp(command);
        printedHelp = true;
      }
    }

    boolean printedVersion = false;
    if (maybeVersionFlag.isPresent()) {
      FlagConfigurationParameter versionFlag = maybeVersionFlag.get();
      if (versionFlag.getLongName() != null
          && args.contains(versionFlag.getLongName().toSwitchString())) {
        printVersion(command);
        printedVersion = true;
      }
    }

    if (printedHelp || printedVersion) {
      //System.exit(0);
      //throw new AssertionError("Failed to exit");
    }

    T result;
    try {
      result = command.args(args);
    } catch (SyntaxException e) {
      System.err.println(e.getMessage());
      if (args.size() == 0)
        printHelp(command);
      System.exit(1);
      throw new AssertionError("Failed to exit");
    } catch (ArgumentException e) {
      System.err.println(e.getMessage());
      System.exit(2);
      throw new AssertionError("Failed to exit");
    }

    return result;
  }

  public static void printHelp(Command<?> command) {
    System.err.println(new DefaultHelpFormatter().formatHelp(command));
  }

  public static void printVersion(Command<?> command) {
    System.err.println(new DefaultVersionFormatter().formatVersion(command));
  }


  /**
   * Makes a best-effort attempt to determine if the given args contain the given switches. It is
   * not -- and cannot be -- perfect because it does not know whether or not each switch is valued.
   * That would require a ConfigurationClass, and we're trying to short-circuit that here. We could
   * get some false positives if a flag is passed as a value to an option.
   */
  /* default */ static boolean containsFlag(List<String> args, ShortSwitchNameCoordinate shortName,
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
