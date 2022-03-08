package com.sigpwned.discourse.core;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.espresso.BeanInstance;

public class Configurator<T> {
  private final Class<T> rawType;
  private final SerializationContext serializationContext;
  private final SinkContext storageContext;
  private final List<String> args;

  public Configurator(Class<T> rawType) {
    this.rawType = rawType;
    this.serializationContext = new SerializationContext();
    this.storageContext = new SinkContext(AssignValueSinkFactory.INSTANCE);
    this.args = new ArrayList<>();
  }

  public Class<T> getRawType() {
    return rawType;
  }

  public Configurator<T> registerDeserializer(ValueDeserializerFactory<?> deserializer) {
    getSerializationContext().addFirst(deserializer);
    return this;
  }

  public Configurator<T> registerSink(ValueSinkFactory storer) {
    getStorageContext().addFirst(storer);
    return this;
  }

  public Configurator<T> arg(String arg) {
    args.add(arg);
    return this;
  }

  public Configurator<T> args(String... args) {
    args(asList(args));
    return this;
  }

  public Configurator<T> args(List<String> args) {
    this.args.addAll(args);
    return this;
  }

  @SuppressWarnings({"unchecked"})
  public T done() {
    ConfigurationClass configurationClass =
        ConfigurationClass.scan(getStorageContext(), getSerializationContext(), getRawType());

    BeanInstance instance;
    try {
      instance = configurationClass.newInstance();
    }
    catch(InvocationTargetException e) {
      throw new NewInstanceFailureArgumentException(e);
    }

    Set<String> required = new HashSet<>(
        configurationClass.getProperties().stream().filter(ConfigurationParameter::isRequired)
            .map(ConfigurationParameter::getName).collect(toList()));

    // Handle CLI arguments
    new ArgumentsParser(configurationClass, new ArgumentsParser.Handler() {
      @Override
      public void flag(FlagConfigurationParameter property) {
        try {
          property.set(instance.getInstance(), "true");
        }
        catch(InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        required.remove(property.getName());
      }

      @Override
      public void option(OptionConfigurationParameter property, String text) {
        try {
          property.set(instance.getInstance(), text);
        }
        catch(InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        required.remove(property.getName());
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String text) {
        try {
          property.set(instance.getInstance(), text);
        }
        catch(InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        required.remove(property.getName());
      }
    }).parse(getArgs());
    
    // Handle environment variable arguments
    configurationClass.getProperties().stream()
      .filter(p -> p.getType() == ConfigurationParameter.Type.ENVIRONMENT)
      .map(ConfigurationParameter::asEnvironment)
      .forEach(property -> {
        String variableName=property.getVariableName().toString();
        String text=systemGetenv(variableName);
        if(text != null) {
          try {
            property.set(instance.getInstance(), text);
          }
          catch(InvocationTargetException e) {
            throw new AssignmentFailureArgumentException(property.getName(), e);
          }
          required.remove(property.getName());
        }
      });
    
    // Handle system property arguments
    configurationClass.getProperties().stream()
      .filter(p -> p.getType() == ConfigurationParameter.Type.PROPERTY)
      .map(ConfigurationParameter::asProperty)
      .forEach(property -> {
        String propertyName=property.getPropertyName().toString();
        String text=systemGetProperty(propertyName);
        if(text != null) {
          try {
            property.set(instance.getInstance(), text);
          }
          catch(InvocationTargetException e) {
            throw new AssignmentFailureArgumentException(property.getName(), e);
          }
          required.remove(property.getName());
        }
      });

    if (!required.isEmpty())
      throw new UnassignedRequiredParametersArgumentException(required);

    return (T) instance.getInstance();
  }

  /**
   * test hook
   */
  protected String systemGetenv(String name) {
    return System.getenv(name);
  }
  
  /**
   * test hook
   */
  protected String systemGetProperty(String key) {
    return System.getProperty(key);
  }

  /**
   * @return the args
   */
  private List<String> getArgs() {
    return args;
  }

  /**
   * @return the serializationContext
   */
  private SerializationContext getSerializationContext() {
    return serializationContext;
  }

  /**
   * @return the storageContext
   */
  private SinkContext getStorageContext() {
    return storageContext;
  }

}
