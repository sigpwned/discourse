package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class SubcommandDoesNotExtendRootCommandConfigurationException extends ConfigurationException {
  private final Class<?> commandType;
  private final Class<?> subcommandType;

  public SubcommandDoesNotExtendRootCommandConfigurationException(Class<?> commandType,
      Class<?> subcommandType) {
    super(format("Subcommand configurable %s does not extend root configurable %s",
        subcommandType.getName(), commandType.getName()));
    this.commandType = commandType;
    this.subcommandType = subcommandType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return commandType;
  }

  /**
   * @return the subcommandType
   */
  public Class<?> getSubcommandType() {
    return subcommandType;
  }
}
