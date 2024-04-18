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
package com.sigpwned.discourse.core.parameter;

import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.util.Generated;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Set;

public abstract sealed class ConfigurationParameter permits EnvironmentConfigurationParameter,
    FlagConfigurationParameter, OptionConfigurationParameter, PositionalConfigurationParameter,
    PropertyConfigurationParameter {

  private final String name;
  private final String description;
  private final boolean required;
  private final ValueDeserializer<?> deserializer;
  private final ValueSink sink;

  protected ConfigurationParameter(String name, String description, boolean required,
      ValueDeserializer<?> deserializer, ValueSink sink) {
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
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConfigurationParameter other = (ConfigurationParameter) obj;
    return Objects.equals(description, other.description) && Objects.equals(deserializer,
        other.deserializer) && Objects.equals(name, other.name) && required == other.required
        && Objects.equals(sink, other.sink);
  }
}
