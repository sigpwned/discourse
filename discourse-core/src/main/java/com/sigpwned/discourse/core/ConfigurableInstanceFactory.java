package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A factory for creating instances of a {@link SingleCommand} class from a set of arguments. This
 * is an abstraction of a class constructor, although it may create instances in other ways, e.g.,
 * using factory methods, dynamic proxies, etc.
 *
 * @param <T> the type of object that the factory creates
 */
public interface ConfigurableInstanceFactory<T> {

  /**
   * Returns the names of the parameters that are required to create an instance of the class. For
   * example, if this factory creates instances of a class with a constructor, then this method
   * would return the names of the parameters of that constructor.
   *
   * @return the names of the required parameters
   */
  public Set<String> getRequiredParameterNames();

  /**
   * Returns the definitions of any parameters defined by the factory. For example, if this factory
   * creates instances of a class with a constructor, then this method would return information
   * about the parameters of that constructor that have been annotated with
   * {@link com.sigpwned.discourse.core.annotation.OptionParameter},
   * {@link com.sigpwned.discourse.core.annotation.FlagParameter}, etc.
   *
   * @return the names of the optional parameters
   */
  public List<ConfigurationParameter> getDefinedParameters();

  /**
   * Creates an instance of the class using the given arguments. The arguments are provided as a map
   * from parameter names to values.
   *
   * @param arguments the arguments
   * @return the instance
   */
  public T createInstance(Map<String, Object> arguments);
}
