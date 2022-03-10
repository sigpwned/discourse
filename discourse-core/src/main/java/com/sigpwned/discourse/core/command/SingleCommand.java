package com.sigpwned.discourse.core.command;

import static java.util.stream.Collectors.toSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Invocation;
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

  public Invocation<T> args(List<String> args) {
    return new Invocation<T>(this, getConfigurationClass(), args);
  }

  @Override
  public String getName() {
    return getConfigurationClass().getName();
  }

  @Override
  public String getDescription() {
    return getConfigurationClass().getDescription();
  }
  
  @Override
  public String getVersion() {
    return getConfigurationClass().getVersion();
  }

  @Override
  public Set<ConfigurationParameter> getParameters() {
    return getConfigurationClass().getParameters().stream().distinct().collect(toSet());
  }
}
