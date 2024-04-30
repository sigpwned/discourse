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
package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.error.exit.DefaultExitErrorFactory;
import com.sigpwned.discourse.core.exception.ArgumentException;
import com.sigpwned.discourse.core.exception.BeanException;
import com.sigpwned.discourse.core.exception.ConfigurationException;
import com.sigpwned.discourse.core.exception.SyntaxException;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import java.util.List;

/**
 * A utility class for creating configuration objects from command line arguments.
 */
public final class Discourse {

  private Discourse() {
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, String[] args) {
    return configuration(rawType, asList(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, InvocationContext context, String[] args) {
    return configuration(rawType, context, List.of(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, List<String> args) {
    return configuration(rawType, new DefaultInvocationContext(), args);
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, InvocationContext context,
      List<String> args) {
    T result;
    try {
      result = Invocation.builder().scan(rawType, context).resolve(args, context).parse(context)
          .deserialize(context).prepare(context).build(context).getConfiguration();
    } catch (ConfigurationException e) {
      // In this case, there was a problem with the application configuration. This is probably a
      // bug in the application, and therefore the developer's fault. Exit code 10.
      context.get(InvocationContext.EXCEPTION_FORMATTER_CHAIN_KEY).orElseThrow()
          .getExceptionFormatter(e, context).formatException(e, context);
      throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
          .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(10);
    } catch (SyntaxException e) {
      // In this case, there was a problem with the structure of the user's CLI arguments. This is
      // the user's fault. Exit code 20.
      context.get(InvocationContext.EXCEPTION_FORMATTER_CHAIN_KEY).orElseThrow()
          .getExceptionFormatter(e, context).formatException(e, context);
      throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
          .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(20);
    } catch (ArgumentException e) {
      // In this case, there was a problem with the content of the user's CLI arguments. This is
      // the user's fault. Exit code 30.
      context.get(InvocationContext.EXCEPTION_FORMATTER_CHAIN_KEY).orElseThrow()
          .getExceptionFormatter(e, context).formatException(e, context);
      throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
          .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(30);
    } catch (BeanException e) {
      // In this case, there was a problem with creating the configuration bean. This is the
      // developer's fault. Exit code 40.
      context.get(InvocationContext.EXCEPTION_FORMATTER_CHAIN_KEY).orElseThrow()
          .getExceptionFormatter(e, context).formatException(e, context);
      throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
          .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(40);
    } catch (Throwable e) {
      // Welp, you got me. This is probably a bug in the application? We print a helpful error message.
      context.get(InvocationContext.EXCEPTION_FORMATTER_CHAIN_KEY).orElseThrow()
          .getExceptionFormatter(e, context).formatException(e, context);
      throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
          .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(100);
    }

    return result;
  }
}
