package com.sigpwned.discourse.core.util;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.property.EnvironmentConfigurationProperty;
import com.sigpwned.discourse.core.property.FlagConfigurationProperty;
import com.sigpwned.discourse.core.property.OptionConfigurationProperty;
import com.sigpwned.discourse.core.property.PositionalConfigurationProperty;
import com.sigpwned.discourse.core.property.PropertyConfigurationProperty;

public final class Parameters {
  private Parameters() {}

  public static final Pattern SHORT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]");

  public static final Pattern LONG_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_][-a-zA-Z0-9_]*");

  public static Optional<String> shortName(ConfigurationProperty p) {
    if (p instanceof FlagConfigurationProperty) {
      FlagConfigurationProperty flag = (FlagConfigurationProperty) p;
      return Optional.ofNullable(flag.getShortName());
    } else if (p instanceof OptionConfigurationProperty) {
      OptionConfigurationProperty option = (OptionConfigurationProperty) p;
      return Optional.ofNullable(option.getShortName());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> longName(ConfigurationProperty p) {
    if (p instanceof FlagConfigurationProperty) {
      FlagConfigurationProperty flag = (FlagConfigurationProperty) p;
      return Optional.ofNullable(flag.getLongName());
    } else if (p instanceof OptionConfigurationProperty) {
      OptionConfigurationProperty option = (OptionConfigurationProperty) p;
      return Optional.ofNullable(option.getLongName());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> variableName(ConfigurationProperty p) {
    if (p instanceof EnvironmentConfigurationProperty) {
      EnvironmentConfigurationProperty environment = (EnvironmentConfigurationProperty) p;
      return Optional.ofNullable(environment.getVariableName());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> propertyName(ConfigurationProperty p) {
    if (p instanceof PropertyConfigurationProperty) {
      PropertyConfigurationProperty property = (PropertyConfigurationProperty) p;
      return Optional.ofNullable(property.getPropertyName());
    } else {
      return Optional.empty();
    }
  }

  public static OptionalInt position(ConfigurationProperty p) {
    if (p instanceof PositionalConfigurationProperty) {
      PositionalConfigurationProperty positional = (PositionalConfigurationProperty) p;
      return OptionalInt.of(positional.getPosition());
    } else {
      return OptionalInt.empty();
    }
  }
}
