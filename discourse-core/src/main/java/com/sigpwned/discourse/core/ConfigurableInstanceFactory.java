package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import java.util.List;
import java.util.Map;

/**
 * A factory for creating instances of a {@link SingleCommand} class from a set of arguments. This
 * is an abstraction of a class constructor, although it may create instances in other ways, e.g.,
 * using factory methods, dynamic proxies, etc.
 *
 * @param <T> the type of object that the factory creates
 */
public interface ConfigurableInstanceFactory<T> {

  /**
   * Returns the set of parameters this factory requires to create an instance. The parameters are
   * provided as a list of {@link InputConfigurableComponent} objects, which includes various and
   * sundry information about the parameter, such as its name, type, default value, etc.
   *
   * @return the required parameter names
   */
  public List<InputConfigurableComponent> getInputs();

  /**
   * Creates an instance of the class using the given arguments. The arguments are provided as a map
   * from parameter names to values.
   *
   * @param arguments the arguments
   * @return the instance
   */
  public T createInstance(Map<String, Object> arguments);
}
