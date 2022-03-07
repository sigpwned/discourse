package com.sigpwned.discourse.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public abstract class ConfigurationProperty {
  private final ConfigurationClass configurationClass;
  private final String name;
  private final String description;
  private final boolean required;
  private final ValueDeserializer<?> deserializer;
  private final ValueSink sink;

  protected ConfigurationProperty(ConfigurationClass configurationClass, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink) {
    this.configurationClass = configurationClass;
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
}
