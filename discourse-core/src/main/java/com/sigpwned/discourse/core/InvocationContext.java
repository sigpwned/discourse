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
package com.sigpwned.discourse.core;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.chain.AccessorNamingSchemeChain;
import com.sigpwned.discourse.core.chain.ConfigurableComponentScannerChain;
import com.sigpwned.discourse.core.chain.ConfigurableInstanceFactoryScannerChain;
import com.sigpwned.discourse.core.chain.DiscourseListenerChain;
import com.sigpwned.discourse.core.chain.ExceptionFormatterChain;
import com.sigpwned.discourse.core.chain.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.chain.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.error.ExitError;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import com.sigpwned.discourse.core.format.version.VersionFormatter;
import com.sigpwned.discourse.core.listener.DiscourseListener;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

/**
 * An invocation context that provides information about the current invocation environment. This is
 * a collection of key-value pairs, where the keys are strings and the values are arbitrary objects.
 * This is used to pass information between different parts of the system, such as the help
 * formatter, the version formatter, and the error stream.
 */
public interface InvocationContext {

  public static record Key<T>(String name, Class<T> type) {

    public static <T> Key<T> of(String name, Class<T> type) {
      return new Key<>(name, type);
    }

    public Key {
      name = requireNonNull(name);
      type = requireNonNull(type);
    }
  }

  /**
   * The key for the {@link HelpFormatter help formatter}. This is a required value.
   */
  public static final Key<HelpFormatter> HELP_FORMATTER_KEY = Key.of("discourse.HelpFormatter",
      HelpFormatter.class);

  /**
   * The key for the {@link VersionFormatter version formatter}. This is a required value.
   */
  public static final Key<VersionFormatter> VERSION_FORMATTER_KEY = Key.of(
      "discourse.VersionFormatter", VersionFormatter.class);

  /**
   * The key for the {@link PrintStream error stream}. This is a required value.
   */
  public static final Key<PrintStream> ERROR_STREAM_KEY = Key.of("discourse.ErrorStream",
      PrintStream.class);

  /**
   * The key for the {@link ValueDeserializerFactoryChain value deserializer resolver}. This is a
   * required value.
   */
  public static final Key<ValueDeserializerFactoryChain> VALUE_DESERIALIZER_FACTORY_CHAIN_KEY = Key.of(
      "discourse.ValueDeserializerResolver", ValueDeserializerFactoryChain.class);

  /**
   * The key for the {@link ValueSinkFactoryChain value sink resolver}. This is a required value.
   */
  public static final Key<ValueSinkFactoryChain> VALUE_SINK_FACTORY_CHAIN_KEY = Key.of(
      "discourse.ValueSinkResolver", ValueSinkFactoryChain.class);

  public static final Key<DiscourseListenerChain> DISCOURSE_LISTENER_CHAIN_KEY = Key.of(
      "discourse.DiscourseListenerChain", DiscourseListenerChain.class);

  public static final Key<ConfigurableInstanceFactoryScannerChain> CONFIGURABLE_INSTANCE_FACTORY_PROVIDER_CHAIN_KEY = Key.of(
      "discourse.ConfigurableInstanceFactoryProviderChain",
      ConfigurableInstanceFactoryScannerChain.class);

  public static final Key<ConfigurableComponentScannerChain> CONFIGURABLE_COMPONENT_SCANNER_CHAIN_KEY = Key.of(
      "discourse.ConfigurableComponentScannerChain", ConfigurableComponentScannerChain.class);

  public static final Key<AccessorNamingSchemeChain> ACCESSOR_NAMING_SCHEME_CHAIN_KEY = Key.of(
      "discourse.AccessorNamingSchemeChain", AccessorNamingSchemeChain.class);

  public static final Key<ExceptionFormatterChain> EXCEPTION_FORMATTER_CHAIN_KEY = Key.of(
      "discourse.ExceptionFormatterChain", ExceptionFormatterChain.class);

  /**
   * The key for the arguments to the current invocation. This value is added during the resolve
   * step, and is available via the context from
   * {@link DiscourseListener#beforeResolve(Command, List, InvocationContext)} onward.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final Key<List<String>> ARGUMENTS_KEY = (Key) Key.of("discourse.Arguments",
      List.class);

  public static final Key<ExitError.Factory> EXIT_ERROR_FACTORY_KEY = Key.of(
      "discourse.ExitErrorFactory", ExitError.Factory.class);

  /**
   * Registers a module with this invocation context. This is used to register additional resources
   * with the context. May add additional keys to the context, reassign existing keys in the
   * context, or modify existing values in the context.
   *
   * @param module the module to register
   */
  void register(Module module);

  <T> void set(Key<T> key, T value);

  <T> Optional<T> get(Key<T> key);
}
