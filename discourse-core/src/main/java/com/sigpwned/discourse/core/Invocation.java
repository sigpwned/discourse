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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.util.Args;
import com.sigpwned.espresso.BeanInstance;

public class Invocation<T> {
  @FunctionalInterface
  /* default */ static interface EnvironmentVariables {
    public String get(String name);
  }

  @FunctionalInterface
  /* default */ static interface SystemProperties {
    public String get(String name);
  }
  
  private final Command<T> command;
  private final ConfigurationClass configurationClass;
  private final List<String> args;
  private EnvironmentVariables getEnv;
  private SystemProperties getProperty;


  public Invocation(Command<T> command, ConfigurationClass configurationClass, List<String> args) {
    this.command = command;
    this.configurationClass = configurationClass;
    this.args = unmodifiableList(args);
    this.getEnv = System::getenv;
    this.getProperty = System::getProperty;
  }

  /**
   * Prints the help message using {@link DefaultHelpFormatter}.
   * 
   * @see #printHelp(HelpFormatter)
   */
  public Invocation<T> printHelp() {
    return printHelp(new DefaultHelpFormatter());
  }

  /**
   * If a help flag is configured and given in the arguments, then print the help message produced
   * by the given formatter and exit. Otherwise, do nothing. Returns this object.
   */
  public Invocation<T> printHelp(HelpFormatter formatter) {
    getCommand().getParameters().stream()
        .filter(p -> p.getType() == ConfigurationParameter.Type.FLAG)
        .map(ConfigurationParameter::asFlag).filter(FlagConfigurationParameter::isHelp).findFirst()
        .ifPresent(helpFlag -> {
          if (Args.containsFlag(args, helpFlag.getShortName(), helpFlag.getLongName())) {
            System.err.println(formatter.formatHelp(command));
            System.exit(0);
          }
        });
    return this;
  }

  /**
   * Prints the version message using {@link DefaultVersionFormatter}.
   * 
   * @see #printVersion(VersionFormatter)
   */
  public Invocation<T> printVersion() {
    return printVersion(new DefaultVersionFormatter());
  }

  /**
   * If a version flag is configured and given in the arguments, then print the version message
   * produced by the given formatter and exit. Otherwise, do nothing. Returns this object.
   */
  public Invocation<T> printVersion(VersionFormatter formatter) {
    getCommand().getParameters().stream()
        .filter(p -> p.getType() == ConfigurationParameter.Type.FLAG)
        .map(ConfigurationParameter::asFlag).filter(FlagConfigurationParameter::isVersion)
        .findFirst().ifPresent(versionFlag -> {
          if (Args.containsFlag(args, versionFlag.getShortName(), versionFlag.getLongName())) {
            System.err.println(formatter.formatVersion(command));
            System.exit(0);
          }
        });
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public T configuration() {
    BeanInstance instance;
    try {
      instance = getConfigurationClass().newInstance();
    } catch (InvocationTargetException e) {
      throw new NewInstanceFailureArgumentException(e);
    }

    Set<String> required = new HashSet<>(
        getConfigurationClass().getParameters().stream().filter(ConfigurationParameter::isRequired)
            .map(ConfigurationParameter::getName).collect(toList()));

    // Handle CLI arguments
    new ArgumentsParser(getConfigurationClass(), new ArgumentsParser.Handler() {
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
    getConfigurationClass().getParameters().stream()
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
    getConfigurationClass().getParameters().stream()
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

  /**
   * @return the command
   */
  public Command<T> getCommand() {
    return command;
  }
  
  /**
   * @return the configurationClass
   */
  private ConfigurationClass getConfigurationClass() {
    return configurationClass;
  }

  /**
   * @return the args
   */
  public List<String> getArgs() {
    return args;
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
}
