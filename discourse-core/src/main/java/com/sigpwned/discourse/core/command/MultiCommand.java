package com.sigpwned.discourse.core.command;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.exception.argument.InvalidDiscriminatorArgumentException;
import com.sigpwned.discourse.core.exception.argument.NoSubcommandArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnrecognizedSubcommandArgumentException;
import com.sigpwned.discourse.core.exception.configuration.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.RootCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedSubcommandsConfigurationException;

public class MultiCommand<T> extends Command<T> {
  public static <T> MultiCommand<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawCommandType) {
    Configurable configurable = rawCommandType.getAnnotation(Configurable.class);

    if (configurable == null)
      throw new NotConfigurableConfigurationException(rawCommandType);

    final String name = configurable.name().isEmpty() ? null : configurable.name();
    final String description =
        configurable.description().isEmpty() ? null : configurable.description();
    final String version = configurable.version().isEmpty() ? null : configurable.version();

    if (configurable.subcommands().length == 0)
      throw new IllegalArgumentException(
          format("Configurable %s has no subcommands", rawCommandType.getName()));
    if (!configurable.discriminator().isEmpty())
      throw new UnexpectedDiscriminatorConfigurationException(rawCommandType);
    if (!Modifier.isAbstract(rawCommandType.getModifiers()))
      throw new RootCommandNotAbstractConfigurationException(rawCommandType);

    Map<Discriminator, ConfigurationClass> configurationClasses = new LinkedHashMap<>();
    for (Subcommand subcommand : configurable.subcommands()) {
      if (subcommand.discriminator().isEmpty())
        throw new NoDiscriminatorConfigurationException(rawCommandType);

      Discriminator commandDiscriminator;
      try {
        commandDiscriminator = Discriminator.fromString(subcommand.discriminator());
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorConfigurationException(rawCommandType,
            subcommand.discriminator());
      }

      Class<?> rawSubcommandType = subcommand.configurable();

      Configurable subconfigurable = rawSubcommandType.getAnnotation(Configurable.class);
      if (subconfigurable == null)
        throw new NotConfigurableConfigurationException(rawSubcommandType);

      if (!Objects.equals(rawSubcommandType.getSuperclass(), rawCommandType))
        throw new SubcommandDoesNotExtendRootCommandConfigurationException(rawCommandType,
            rawSubcommandType);

      if (subconfigurable.discriminator().isEmpty())
        throw new NoDiscriminatorConfigurationException(rawSubcommandType);

      Discriminator subcommandDiscriminator;
      try {
        subcommandDiscriminator = Discriminator.fromString(subconfigurable.discriminator());
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorConfigurationException(rawSubcommandType,
            subconfigurable.discriminator());
      }

      if (!subcommandDiscriminator.equals(commandDiscriminator))
        throw new DiscriminatorMismatchConfigurationException(rawSubcommandType,
            commandDiscriminator, subcommandDiscriminator);

      if (subconfigurable.subcommands().length != 0)
        throw new UnexpectedSubcommandsConfigurationException(rawSubcommandType);

      ConfigurationClass configurationClass =
          ConfigurationClass.scan(storage, serialization, rawSubcommandType);

      configurationClasses.put(commandDiscriminator, configurationClass);
    }

    return new MultiCommand<T>(name, description, version, configurationClasses);
  }

  private final String name;
  private final String description;
  private final String version;
  private final Map<Discriminator, ConfigurationClass> subcommands;

  public MultiCommand(String name, String description, String version,
      Map<Discriminator, ConfigurationClass> subcommands) {
    super(Type.MULTI);
    if (subcommands.isEmpty())
      throw new IllegalArgumentException("no subcommands");
    this.name = name;
    this.description = description;
    this.version = version;
    this.subcommands = unmodifiableMap(subcommands);
  }

  /**
   * @return the name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  public Set<Discriminator> listSubcommands() {
    return getSubcommands().keySet();
  }

  public Optional<ConfigurationClass> getSubcommand(Discriminator discriminator) {
    return Optional.ofNullable(getSubcommands().get(discriminator));
  }

  /**
   * Returns all unique parameters from any subcommand
   */
  @Override
  public Set<ConfigurationParameter> getParameters() {
    return getSubcommands().values().stream().flatMap(c -> c.getParameters().stream()).distinct()
        .collect(toSet());
  }

  /**
   * Returns all option and flag parameters shared by all subcommands
   */
  public Set<ConfigurationParameter> getCommonParameters() {
    return getSubcommands().values().stream().flatMap(c -> c.getParameters().stream())
        .filter(p -> p.getType() == ConfigurationParameter.Type.OPTION
            || p.getType() == ConfigurationParameter.Type.FLAG)
        .collect(groupingBy(p -> p, counting())).entrySet().stream()
        .filter(e -> e.getValue() == getSubcommands().size()).map(e -> e.getKey()).collect(toSet());
  }

  /**
   * @return the subcommands
   */
  private Map<Discriminator, ConfigurationClass> getSubcommands() {
    return subcommands;
  }

  @Override
  public Invocation<T> args(List<String> args) {
    if (args.isEmpty())
      throw new NoSubcommandArgumentException();

    Discriminator subcommand;
    try {
      subcommand = Discriminator.fromString(args.get(0));
    } catch (IllegalArgumentException e) {
      throw new InvalidDiscriminatorArgumentException(args.get(0));
    }

    ConfigurationClass configurationClass = getSubcommand(subcommand)
        .orElseThrow(() -> new UnrecognizedSubcommandArgumentException(subcommand));

    return new Invocation<T>(this, configurationClass, args.subList(1, args.size()));
  }
}
