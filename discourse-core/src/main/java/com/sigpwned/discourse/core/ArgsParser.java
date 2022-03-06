package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.ListIterator;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.argument.InvalidLongNameValueArgumentException;
import com.sigpwned.discourse.core.exception.argument.MissingLongNameValueArgumentException;
import com.sigpwned.discourse.core.exception.argument.MissingShortNameValueArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnrecognizedLongNameArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnrecognizedShortNameArgumentException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.property.FlagConfigurationProperty;
import com.sigpwned.discourse.core.property.OptionConfigurationProperty;
import com.sigpwned.discourse.core.property.PositionalConfigurationProperty;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;

public class ArgsParser {
  public static interface Handler {
    default void flag(FlagConfigurationProperty property) {}

    default void option(OptionConfigurationProperty property, String value) {}

    default void positional(PositionalConfigurationProperty property, String value) {}
  }

  private final ConfigurationClass configurationClass;
  private final Handler handler;
  private ListIterator<String> iterator;

  public ArgsParser(ConfigurationClass configurationClass, Handler handler) {
    this.configurationClass = configurationClass;
    this.handler = handler;
  }

  public void parse(List<String> args) {
    iterator = unmodifiableList(args).listIterator();
    try {
      parse();
    } finally {
      iterator = null;
    }
  }

  private void parse() {
    PositionCoordinate position = PositionCoordinate.ZERO;
    boolean positionals = false;
    while (peek() != null) {
      String next = next();
      if (positionals || !next.startsWith("-")) {
        final int index = position.getIndex();

        PositionalConfigurationProperty positionalProperty =
            (PositionalConfigurationProperty) getConfigurationClass().resolve(position)
                .orElseThrow(() -> new MissingPositionConfigurationException(index));

        getHandler().positional(positionalProperty, next);

        positionals = true;

        if (!positionalProperty.isCollection())
          position = position.next();
      } else {
        ArgumentToken token = ArgumentToken.fromString(next);
        switch (token.getType()) {
          case BUNDLE:
            handleBundle(token.asBundle());
            break;
          case LONG_NAME:
            handleLongName(token.asLongName());
            break;
          case LONG_NAME_VALUE:
            handleLongNameValue(token.asLongNameValue());
            break;
          case SEPARATOR:
            positionals = true;
            break;
          case SHORT_NAME:
            handleShortName(token.asShortName());
            break;
          case VALUE:
          case EOF:
            throw new AssertionError("unexpected token type: " + token.getType());
          default:
            throw new AssertionError("unrecognized token type: " + token.getType());
        }
      }
    }
  }

  private void handleBundle(BundleArgumentToken bundle) {
    for (int index = 0; index < bundle.getShortNames().size(); index++) {
      boolean lastIndex = index == bundle.getShortNames().size() - 1;

      ShortSwitchNameCoordinate shortName =
          ShortSwitchNameCoordinate.fromString(bundle.getShortNames().get(index));

      ConfigurationProperty shortNameProperty = getConfigurationClass().resolve(shortName)
          .orElseThrow(() -> new UnrecognizedShortNameArgumentException(shortName.toString()));

      if (shortNameProperty.isValued()) {
        OptionConfigurationProperty optionProperty =
            (OptionConfigurationProperty) shortNameProperty;
        if (!lastIndex)
          throw new MissingShortNameValueArgumentException(shortNameProperty.getName(),
              shortName.toString());
        if (peek() == null)
          throw new MissingShortNameValueArgumentException(shortNameProperty.getName(),
              shortName.toString());
        String value = next();
        getHandler().option(optionProperty, value);
      } else {
        FlagConfigurationProperty flagProperty = (FlagConfigurationProperty) shortNameProperty;
        getHandler().flag(flagProperty);
      }
    }
  }

  private void handleShortName(ShortNameArgumentToken token) {
    ShortSwitchNameCoordinate shortName =
        ShortSwitchNameCoordinate.fromString(token.getShortName());

    ConfigurationProperty shortNameProperty = getConfigurationClass().resolve(shortName)
        .orElseThrow(() -> new UnrecognizedShortNameArgumentException(shortName.toString()));

    if (shortNameProperty.isValued()) {
      OptionConfigurationProperty optionProperty = (OptionConfigurationProperty) shortNameProperty;
      if (peek() == null)
        throw new MissingShortNameValueArgumentException(optionProperty.getName(),
            shortName.toString());
      String value = next();
      getHandler().option(optionProperty, value);
    } else {
      FlagConfigurationProperty flagProperty = (FlagConfigurationProperty) shortNameProperty;
      getHandler().flag(flagProperty);
    }
  }

  private void handleLongName(LongNameArgumentToken token) {
    LongSwitchNameCoordinate longName = LongSwitchNameCoordinate.fromString(token.getLongName());

    ConfigurationProperty longNameProperty = getConfigurationClass().resolve(longName)
        .orElseThrow(() -> new UnrecognizedLongNameArgumentException(longName.toString()));

    if (longNameProperty.isValued()) {
      OptionConfigurationProperty optionProperty = (OptionConfigurationProperty) longNameProperty;
      if (peek() == null)
        throw new MissingLongNameValueArgumentException(optionProperty.getName(),
            longName.toString());
      String value = next();
      getHandler().option(optionProperty, value);
    } else {
      FlagConfigurationProperty flagProperty = (FlagConfigurationProperty) longNameProperty;
      getHandler().flag(flagProperty);
    }
  }

  private void handleLongNameValue(LongNameValueArgumentToken token) {
    LongSwitchNameCoordinate longName = LongSwitchNameCoordinate.fromString(token.getLongName());
    String value = token.getValue();

    ConfigurationProperty longNameValueProperty = getConfigurationClass().resolve(longName)
        .orElseThrow(() -> new UnrecognizedLongNameArgumentException(longName.toString()));

    if (longNameValueProperty.isValued()) {
      OptionConfigurationProperty optionProperty =
          (OptionConfigurationProperty) longNameValueProperty;
      getHandler().option(optionProperty, value);
    } else {
      throw new InvalidLongNameValueArgumentException(longNameValueProperty.getName(),
          longName.toString());
    }
  }

  private String peek() {
    if (iterator.hasNext()) {
      String result = iterator.next();
      iterator.previous();
      return result;
    } else {
      return null;
    }
  }

  private String next() {
    return iterator.hasNext() ? iterator.next() : null;
  }

  /**
   * @return the configurationClass
   */
  private ConfigurationClass getConfigurationClass() {
    return configurationClass;
  }

  /**
   * @return the handler
   */
  private Handler getHandler() {
    return handler;
  }
}
