package com.sigpwned.discourse.core.configurable.component.element;

import com.sigpwned.discourse.core.configurable.ConfigurableClass;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A code element (e.g., field, method, etc.) that is related to a logical attribute in a
 * {@link ConfigurableClass}.
 */
public interface ConfigurableElement {

  public static final String INSTANCE_NAME = "";

  /**
   * Returns the literal name of this element. For example, for a getter method {@code getX()}, this
   * method would return {@code "getX"}.
   *
   * @return the name of this element
   */
  public String getName();

  /**
   * Returns the {@link Type generic type} of this element.
   *
   * @return the type of this element
   * @see com.sigpwned.discourse.core.util.JodaBeanUtils#eraseToClass(Type)
   */
  public Type getGenericType();

  /**
   * Returns the {@link Annotation annotations} of this element. The result is immutable.
   *
   * @return the raw type of this element
   */
  public List<Annotation> getAnnotations();
}
