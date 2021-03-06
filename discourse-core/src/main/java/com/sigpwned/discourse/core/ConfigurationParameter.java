/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.Generated;

public abstract class ConfigurationParameter {
  public static enum Type {
    ENVIRONMENT, FLAG, OPTION, POSITIONAL, PROPERTY;
  }

  private final Type type;
  private final String name;
  private final String description;
  private final boolean required;
  private final ValueDeserializer<?> deserializer;
  private final ValueSink sink;

  protected ConfigurationParameter(Type type, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink) {
    this.type = type;
    this.name = name;
    this.description = description;
    this.required = required;
    this.deserializer = deserializer;
    this.sink = sink;
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
  
  public java.lang.reflect.Type getGenericType() {
    return getSink().getGenericType();
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

  /*
   * Not generated!
   */
  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConfigurationParameter other = (ConfigurationParameter) obj;
    return Objects.equals(description, other.description)
        && Objects.equals(deserializer, other.deserializer) && Objects.equals(name, other.name)
        && required == other.required && Objects.equals(sink, other.sink) && type == other.type;
  }
}
