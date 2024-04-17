/*-
 * =================================LICENSE_START==================================
 * discourse-validation
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
package com.sigpwned.discourse.validation;

import static java.util.stream.Collectors.toMap;

import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.validation.command.ValidatingMultiCommand;
import com.sigpwned.discourse.validation.command.ValidatingSingleCommand;
import java.util.Optional;

public class ValidatingCommandBuilder extends CommandBuilder {

  /**
   * discourse-validation performs some logging configuration by default to suppress logging
   * messages from hibernate-validator. Use this system property to disable this configuration.
   */
  public static final String CONFIGURE_LOGGING_PROPERTY_NAME = "com.sigpwned.discourse.validation.configureLogging";

  // This code attempts to suppress some default logging messages from Hibernate Validation. If we
  // don't suppress, then we get messages like the following in logs at program startup:
  //
  // May 09, 2022 9:31:28 AM org.hibernate.validator.internal.util.Version <clinit>
  // INFO: HV000001: Hibernate Validator null
  //
  // It would be really nice if there was a way to suppress these default logging messages at
  // startup that didn't involve so many assumptions about JBOSS and application logging choices and
  // implementations.
  //
  // This is currently untested, which is also unfortunate. Because the logging happens at class
  // load, there is no good way to capture that in a test. There might be some solution using custom
  // class loaders, but I was not able to quickly come up with a good, reliable solution quickly.
  // 
  // TODO Create test for hibernate validator logging
  static {
    boolean configureLogging = Optional.ofNullable(
            System.getProperty(CONFIGURE_LOGGING_PROPERTY_NAME)).map(Boolean::parseBoolean)
        .orElse(true);
    if (configureLogging) {
      // Hibernate Validation uses JBOSS logging, which uses log4j by default.
      // We want to tell JBOSS to use slf4j instead.
      // https://stackoverflow.com/a/14742191/2103602
      // https://stackoverflow.com/a/19488546/2103602
      System.setProperty("org.jboss.logging.provider", "slf4j");

      // We want to suppress a couple of messages that print out by default at
      // INFO log level. We'll set the log level for these classes to WARN. This only works if the
      // underlying log binding is slf4j-simple. We may want to add additional configuration here
      // later, for example for logback.
      // https://stackoverflow.com/a/23391374/2103602
      System.setProperty("org.slf4j.simpleLogger.log.org.hibernate.validator.internal.util.Version",
          "warn");
    }
  }

  @Override
  public ValidatingCommandBuilder register(Module module) {
    return (ValidatingCommandBuilder) super.register(module);
  }

  @Override
  public <T> Command<T> build(Class<T> rawType) {
    Command<T> result = super.build(rawType);
    if (result instanceof SingleCommand<T> single) {
      return new ValidatingSingleCommand<T>(single.getConfigurationClass());
    } else if (result instanceof MultiCommand<T> multi) {
      return new ValidatingMultiCommand<T>(multi.getName(), multi.getDescription(),
          multi.getVersion(), multi.listSubcommands().stream()
          .collect(toMap(d -> d, d -> multi.getSubcommand(d).get())));
    }
    throw new AssertionError("unrecognized command type: " + result.getClass());
  }
}
