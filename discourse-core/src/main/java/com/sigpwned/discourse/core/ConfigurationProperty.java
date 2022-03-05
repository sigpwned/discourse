package com.sigpwned.discourse.core;

import static java.lang.String.format;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import com.sigpwned.discourse.core.util.Types;
import com.sigpwned.espresso.BeanProperty;

public abstract class ConfigurationProperty {
  private final ConfigurationClass configurationClass;
  private final BeanProperty beanProperty;
  private final ValueStorer storer;
  private final String description;
  private final boolean required;

  protected ConfigurationProperty(ConfigurationClass configurationClass, BeanProperty beanProperty,
      ValueStorer storer, String description, boolean required) {
    this.configurationClass = configurationClass;
    this.beanProperty = beanProperty;
    this.storer = storer;
    this.description = description;
    this.required = required;
    if (!Types.isConcrete(getGenericType()))
      throw new IllegalArgumentException(
          format("Cannot configure unresolved property of type %s", getGenericType()));
  }

  /**
   * @return the configurationClass
   */
  public ConfigurationClass getConfigurationClass() {
    return configurationClass;
  }

  /**
   * @return the beanProperty
   */
  private BeanProperty getBeanProperty() {
    return beanProperty;
  }

  /**
   * @return the storer
   */
  private ValueStorer getStorer() {
    return storer;
  }

  public boolean isCollection() {
    return getStorer().isCollection();
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  public String getName() {
    return getBeanProperty().getName();
  }

  public abstract boolean isValued();

  public boolean isRequired() {
    return required;
  }

  /**
   * Must be either a {@link Class}, {@link ParameterizedType}, or {@link GenericArrayType}.
   */
  public Type getGenericType() {
    return getStorer().unpack(getBeanProperty());
  }

  public List<Annotation> getAnnotations() {
    return getBeanProperty().getAnnotations();
  }

  public void set(Object instance, Object value) throws InvocationTargetException {
    getStorer().store(instance, getBeanProperty(), value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beanProperty, configurationClass, description, storer);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConfigurationProperty other = (ConfigurationProperty) obj;
    return Objects.equals(beanProperty, other.beanProperty)
        && Objects.equals(configurationClass, other.configurationClass)
        && Objects.equals(description, other.description) && Objects.equals(storer, other.storer);
  }

  @Override
  public String toString() {
    return "ConfigurationProperty [configurationClass=" + configurationClass + ", beanProperty="
        + beanProperty + ", storer=" + storer + ", description=" + description + "]";
  }
}
