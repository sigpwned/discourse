/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.module.core.scan.subcommands;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SubCommandScanner;
import com.sigpwned.discourse.core.util.Discriminators;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.MoreCollectors;

/**
 * Scans a class for subcommands. This implementation uses the
 * {@link Configurable#subcommands() @Configurable} annotation to find subcommands.
 */
public class ConfigurableSubCommandScanner implements SubCommandScanner {
  public static final ConfigurableSubCommandScanner INSTANCE = new ConfigurableSubCommandScanner();

  @Override
  public <T> Maybe<List<Map.Entry<String, Class<? extends T>>>> scanForSubCommands(Class<T> clazz) {
    Configurable annotation = clazz.getAnnotation(Configurable.class);
    if (annotation == null) {
      return Maybe.maybe();
    }

    if (annotation.subcommands().length == 0) {
      return Maybe.yes(emptyList());
    }

    List<Map.Entry<String, Class<? extends T>>> subcommands =
        Arrays.stream(annotation.subcommands()).peek(subcommand -> {
          // Is the discriminator valid?
          if (!Discriminators.isValid(subcommand.discriminator())) {
            throw new IllegalArgumentException(
                "Invalid discriminator: " + subcommand.discriminator());
          }
        }).peek(subcommand -> {
          // Is the subcommand a subclass of the configurable class?
          if (!clazz.isAssignableFrom(subcommand.configurable())) {
            throw new IllegalArgumentException(
                "Subcommand " + subcommand.configurable() + " is not a subclass of " + clazz);
          }
        }).map(subcommand -> {
          // This cast is safe because we checked it above.
          return Map.<String, Class<? extends T>>entry(subcommand.discriminator(),
              (Class<? extends T>) subcommand.configurable());
        }).toList();

    subcommands.stream().map(Map.Entry::getKey).collect(MoreCollectors.duplicates())
        .ifPresent(duplicates -> {
          throw new IllegalArgumentException("Duplicate discriminator: " + duplicates);
        });

    return Maybe.yes(unmodifiableList(subcommands));
  }
}
