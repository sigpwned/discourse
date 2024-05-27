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
import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineBuilder;

/**
 * A utility class for creating configuration objects from resolvedCommand line arguments.
 */
public final class Discourse {

  private Discourse() {}

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, String[] args) {
    return configuration(rawType, asList(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, List<String> args) {
    return configuration(rawType, InvocationPipeline.builder(), args);
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * resolvedCommand builder.
   */
  public static <T> T configuration(Class<T> rawType, InvocationPipelineBuilder builder,
      List<String> args) {
    T result;
    try {
      result = builder.build().invoke(rawType, args);
    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }
    return result;
  }
}
