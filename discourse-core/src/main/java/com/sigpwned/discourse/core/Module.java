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

import com.sigpwned.discourse.core.accessor.naming.AccessorNamingScheme;
import com.sigpwned.discourse.core.chain.AccessorNamingSchemeChain;
import com.sigpwned.discourse.core.chain.ConfigurableComponentScannerChain;
import com.sigpwned.discourse.core.chain.ConfigurableInstanceFactoryScannerChain;
import com.sigpwned.discourse.core.chain.DiscourseListenerChain;
import com.sigpwned.discourse.core.chain.ExceptionFormatterChain;
import com.sigpwned.discourse.core.chain.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.chain.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.configurable.component.scanner.ConfigurableComponentScanner;
import com.sigpwned.discourse.core.configurable.instance.factory.scanner.ConfigurableInstanceFactoryScanner;
import com.sigpwned.discourse.core.format.exception.ExceptionFormatter;
import com.sigpwned.discourse.core.listener.DiscourseListener;
import com.sigpwned.discourse.core.value.sink.ValueSinkFactory;

/**
 * Container for registering various components of a Discourse application.
 */
public abstract class Module {

  /**
   * Perform any necessary registration for the given {@link InvocationContext}.
   */
  public void register(InvocationContext context) {
  }

  /**
   * Register new {@link ValueSinkFactory} instances with the given {@link ValueSinkFactoryChain}.
   */
  public void registerValueDeserializerFactories(ValueDeserializerFactoryChain resolver) {
  }

  /**
   * Register new {@link ValueSinkFactory} instances with the given {@link ValueSinkFactoryChain}.
   */
  public void registerValueSinkFactories(ValueSinkFactoryChain resolver) {
  }

  /**
   * Register new {@link ConfigurableInstanceFactoryScanner} instances with the given chain
   */
  public void registerInstanceFactoryScanners(ConfigurableInstanceFactoryScannerChain chain) {
  }

  /**
   * Register new {@link ConfigurableComponentScanner} instances with the given chain
   */
  public void registerConfigurableComponentScanners(ConfigurableComponentScannerChain chain) {
  }

  /**
   * Register new {@link AccessorNamingScheme} instances with the given chain
   */
  public void registerAccessorNamingSchemes(AccessorNamingSchemeChain chain) {
  }

  /**
   * Register new {@link ExceptionFormatter} instances with the given chain
   */
  public void registerExceptionFormatters(ExceptionFormatterChain chain) {
  }

  /**
   * Register new {@link DiscourseListener} instances with the given chain
   */
  public void registerDiscourseListeners(DiscourseListenerChain chain) {
  }
}
