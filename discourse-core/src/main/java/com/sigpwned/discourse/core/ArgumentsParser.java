package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.ListIterator;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.syntax.InvalidLongNameValueSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.MissingLongNameValueSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.MissingShortNameValueSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedLongNameSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedShortNameSyntaxException;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;

public class ArgumentsParser {
  public static interface Handler {
    default void flag(FlagConfigurationParameter property) {}

    default void option(OptionConfigurationParameter property, String value) {}

    default void positional(PositionalConfigurationParameter property, String value) {}
  }

  private final ConfigurationClass configurationClass;
  private final Handler handler;
  private ListIterator<String> iterator;

  public ArgumentsParser(ConfigurationClass configurationClass, Handler handler) {
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

        PositionalConfigurationParameter positionalProperty =
            (PositionalConfigurationParameter) getConfigurationClass().resolve(position)
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

      ConfigurationParameter shortNameProperty = getConfigurationClass().resolve(shortName)
          .orElseThrow(() -> new UnrecognizedShortNameSyntaxException(shortName.toString()));

      if (shortNameProperty.isValued()) {
        OptionConfigurationParameter optionProperty =
            (OptionConfigurationParameter) shortNameProperty;
        if (!lastIndex)
          throw new MissingShortNameValueSyntaxException(shortNameProperty.getName(),
              shortName.toString());
        if (peek() == null)
          throw new MissingShortNameValueSyntaxException(shortNameProperty.getName(),
              shortName.toString());
        String value = next();
        getHandler().option(optionProperty, value);
      } else {
        FlagConfigurationParameter flagProperty = (FlagConfigurationParameter) shortNameProperty;
        getHandler().flag(flagProperty);
      }
    }
  }

  private void handleShortName(ShortNameArgumentToken token) {
    ShortSwitchNameCoordinate shortName =
        ShortSwitchNameCoordinate.fromString(token.getShortName());

    ConfigurationParameter shortNameProperty = getConfigurationClass().resolve(shortName)
        .orElseThrow(() -> new UnrecognizedShortNameSyntaxException(shortName.toString()));

    if (shortNameProperty.isValued()) {
      OptionConfigurationParameter optionProperty = (OptionConfigurationParameter) shortNameProperty;
      if (peek() == null)
        throw new MissingShortNameValueSyntaxException(optionProperty.getName(),
            shortName.toString());
      String value = next();
      getHandler().option(optionProperty, value);
    } else {
      FlagConfigurationParameter flagProperty = (FlagConfigurationParameter) shortNameProperty;
      getHandler().flag(flagProperty);
    }
  }

  private void handleLongName(LongNameArgumentToken token) {
    LongSwitchNameCoordinate longName = LongSwitchNameCoordinate.fromString(token.getLongName());

    ConfigurationParameter longNameProperty = getConfigurationClass().resolve(longName)
        .orElseThrow(() -> new UnrecognizedLongNameSyntaxException(longName.toString()));

    if (longNameProperty.isValued()) {
      OptionConfigurationParameter optionProperty = (OptionConfigurationParameter) longNameProperty;
      if (peek() == null)
        throw new MissingLongNameValueSyntaxException(optionProperty.getName(),
            longName.toString());
      String value = next();
      getHandler().option(optionProperty, value);
    } else {
      FlagConfigurationParameter flagProperty = (FlagConfigurationParameter) longNameProperty;
      getHandler().flag(flagProperty);
    }
  }

  private void handleLongNameValue(LongNameValueArgumentToken token) {
    LongSwitchNameCoordinate longName = LongSwitchNameCoordinate.fromString(token.getLongName());
    String value = token.getValue();

    ConfigurationParameter longNameValueProperty = getConfigurationClass().resolve(longName)
        .orElseThrow(() -> new UnrecognizedLongNameSyntaxException(longName.toString()));

    if (longNameValueProperty.isValued()) {
      OptionConfigurationParameter optionProperty =
          (OptionConfigurationParameter) longNameValueProperty;
      getHandler().option(optionProperty, value);
    } else {
      throw new InvalidLongNameValueSyntaxException(longNameValueProperty.getName(),
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
