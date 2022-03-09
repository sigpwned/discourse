package com.sigpwned.discourse.core;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.espresso.BeanInstance;

public abstract class Command<T> {
  public static <T> Command<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);

    if (configurable == null)
      throw new IllegalArgumentException("Root configurable is not configurable");

    if (!configurable.discriminator().isEmpty())
      throw new UnexpectedDiscriminatorConfigurationException(rawType);

    if (configurable.subcommands().length == 0) {
      // This is a single command.
      return SingleCommand.scan(storage, serialization, rawType);
    } else {
      // This is a multi command.
      return MultiCommand.scan(storage, serialization, rawType);
    }
  }
  
  @FunctionalInterface
  /* default */ static interface EnvironmentVariables {
    public String get(String name);
  }
  
  @FunctionalInterface
  /* default */ static interface SystemProperties {
    public String get(String name);
  }

  public static enum Type {
    SINGLE, MULTI;
  }

  private final Type type;
  private EnvironmentVariables getEnv;
  private SystemProperties getProperty;

  public Command(Type type) {
    this.type = type;
    this.getEnv = System::getenv;
    this.getProperty = System::getProperty;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  public SingleCommand<T> asSingle() {
    return (SingleCommand<T>) this;
  }

  public MultiCommand<T> asMulti() {
    return (MultiCommand<T>) this;
  }

  public T args(String... args) {
    return args(asList(args));
  }
  
  /**
   * @return the getEnv
   */
  private EnvironmentVariables getGetEnv() {
    return getEnv;
  }

  /**
   * @param getEnv the getEnv to set
   */
  /* default */ void setGetEnv(EnvironmentVariables getEnv) {
    this.getEnv = getEnv;
  }

  /**
   * @return the getProperty
   */
  private SystemProperties getGetProperty() {
    return getProperty;
  }

  /**
   * @param getProperty the getProperty to set
   */
  /* default */ void setGetProperty(SystemProperties getProperty) {
    this.getProperty = getProperty;
  }

  public abstract T args(List<String> args);

  @SuppressWarnings("unchecked")
  protected T args(ConfigurationClass configurationClass, List<String> args) {
    BeanInstance instance;
    try {
      instance = configurationClass.newInstance();
    } catch (InvocationTargetException e) {
      throw new NewInstanceFailureArgumentException(e);
    }

    Set<String> required = new HashSet<>(
        configurationClass.getParameters().stream().filter(ConfigurationParameter::isRequired)
            .map(ConfigurationParameter::getName).collect(toList()));

    // Handle CLI arguments
    new ArgumentsParser(configurationClass, new ArgumentsParser.Handler() {
      @Override
      public void flag(FlagConfigurationParameter property) {
        try {
          property.set(instance.getInstance(), "true");
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        required.remove(property.getName());
      }

      @Override
      public void option(OptionConfigurationParameter property, String text) {
        try {
          property.set(instance.getInstance(), text);
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        required.remove(property.getName());
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String text) {
        try {
          property.set(instance.getInstance(), text);
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        required.remove(property.getName());
      }
    }).parse(args);

    // Handle environment variable arguments
    configurationClass.getParameters().stream()
        .filter(p -> p.getType() == ConfigurationParameter.Type.ENVIRONMENT)
        .map(ConfigurationParameter::asEnvironment).forEach(property -> {
          String variableName = property.getVariableName().toString();
          String text = getGetEnv().get(variableName);
          if (text != null) {
            try {
              property.set(instance.getInstance(), text);
            } catch (InvocationTargetException e) {
              throw new AssignmentFailureArgumentException(property.getName(), e);
            }
            required.remove(property.getName());
          }
        });

    // Handle system property arguments
    configurationClass.getParameters().stream()
        .filter(p -> p.getType() == ConfigurationParameter.Type.PROPERTY)
        .map(ConfigurationParameter::asProperty).forEach(property -> {
          String propertyName = property.getPropertyName().toString();
          String text = getGetProperty().get(propertyName);
          if (text != null) {
            try {
              property.set(instance.getInstance(), text);
            } catch (InvocationTargetException e) {
              throw new AssignmentFailureArgumentException(property.getName(), e);
            }
            required.remove(property.getName());
          }
        });

    if (!required.isEmpty())
      throw new UnassignedRequiredParametersArgumentException(required);

    return (T) instance.getInstance();
  }
}
