package com.sigpwned.discourse.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;

public abstract class ConfigurationParameter {
  public static enum Type {
    ENVIRONMENT, FLAG, OPTION, POSITIONAL, PROPERTY;
  }

  private final ConfigurationClass configurationClass;
  private final Type type;
  private final String name;
  private final String description;
  private final boolean required;
  private final ValueDeserializer<?> deserializer;
  private final ValueSink sink;

  protected ConfigurationParameter(ConfigurationClass configurationClass, Type type, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink) {
    this.configurationClass = configurationClass;
    this.type = type;
    this.name = name;
    this.description = description;
    this.required = required;
    this.deserializer = deserializer;
    this.sink = sink;
  }

  /**
   * @return the configurationClass
   */
  public ConfigurationClass getConfigurationClass() {
    return configurationClass;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the deserializer
   */
  private ValueDeserializer<?> getDeserializer() {
    return deserializer;
  }

  /**
   * @return the sink
   */
  private ValueSink getSink() {
    return sink;
  }

  public void set(Object instance, String value) throws InvocationTargetException {
    Object deserializedValue = getDeserializer().deserialize(value);
    getSink().write(instance, deserializedValue);
  }

  public boolean isCollection() {
    return getSink().isCollection();
  }

  public boolean isRequired() {
    return required;
  }

  public abstract Set<Coordinate> getCoordinates();

  public abstract boolean isValued();
  
  public EnvironmentConfigurationParameter asEnvironment() {
    return (EnvironmentConfigurationParameter) this;
  }
  
  public FlagConfigurationParameter asFlag() {
    return (FlagConfigurationParameter) this;
  }
  
  public OptionConfigurationParameter asOption() {
    return (OptionConfigurationParameter) this;
  }
  
  public PositionalConfigurationParameter asPositional() {
    return (PositionalConfigurationParameter) this;
  }
  
  public PropertyConfigurationParameter asProperty() {
    return (PropertyConfigurationParameter) this;
  }
}
