package com.sigpwned.discourse.core.command;

import java.util.List;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedSubcommandsConfigurationException;

public class SingleCommand<T> extends Command<T> {
  public static <T> SingleCommand<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);

    if (configurable == null)
      throw new NotConfigurableConfigurationException(rawType);
    if (!configurable.discriminator().isEmpty())
      throw new UnexpectedDiscriminatorConfigurationException(rawType);
    if (configurable.subcommands().length != 0)
      throw new UnexpectedSubcommandsConfigurationException(rawType);

    ConfigurationClass configurationClass =
        ConfigurationClass.scan(storage, serialization, rawType);

    return new SingleCommand<T>(configurationClass);
  }

  private final ConfigurationClass configurationClass;

  public SingleCommand(ConfigurationClass configurationClass) {
    super(Type.SINGLE);
    if (configurationClass == null)
      throw new NullPointerException();
    this.configurationClass = configurationClass;
  }

  /**
   * @return the configurationClass
   */
  public ConfigurationClass getConfigurationClass() {
    return configurationClass;
  }

  public T build(List<String> args) {
    return args(getConfigurationClass(), args);
  }
}
