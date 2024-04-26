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

import com.sigpwned.discourse.core.command.Command;

/**
 * Container for registering new functionality for the {@link Command} phase. The
 * {@link CommandBuilder} uses this class to register new functionality.
 */
public abstract class Module {

  /**
   * Perform any necessary registration for the given {@link InvocationContext}.
   */
  public void register(InvocationContext context) {
  }

  /**
   * Register new {@link ValueSinkFactory} instances with the given {@link ValueSinkResolver}.
   */
  public void registerValueDeserializerFactories(ValueDeserializerResolver resolver) {
  }

  /**
   * Register new {@link ValueSinkFactory} instances with the given {@link ValueSinkResolver}.
   */
  public void registerValueSinkFactories(ValueSinkResolver resolver) {
  }

  /**
   * Register new {@link ConfigurableInstanceFactoryProvider} instances with the given chain
   */
  public void registerInstanceFactoryProviders(ConfigurableInstanceFactoryProviderChain chain) {
  }

  /**
   * Register new {@link ConfigurableParameterScanner} instances with the given chain
   */
  public void registerParameterScanners(ConfigurableParameterScannerChain chain) {
  }

  /**
   * Register new {@link AccessorNamingScheme} instances with the given chain
   */
  public void registerAccessorNamingSchemes(AccessorNamingSchemeChain chain) {
  }
}
