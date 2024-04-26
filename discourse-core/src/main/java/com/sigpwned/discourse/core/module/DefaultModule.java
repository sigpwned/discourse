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
package com.sigpwned.discourse.core.module;

import com.sigpwned.discourse.core.AccessorNamingSchemeChain;
import com.sigpwned.discourse.core.ConfigurableInstanceFactoryProviderChain;
import com.sigpwned.discourse.core.ConfigurableParameterScannerChain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.ValueDeserializerResolver;
import com.sigpwned.discourse.core.ValueSinkResolver;
import com.sigpwned.discourse.core.configurable.instance.factory.DefaultConstructorConfigurableInstanceFactory;
import com.sigpwned.discourse.core.value.deserializer.BigDecimalValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.BooleanValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.ByteValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.CharValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.DoubleValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.EnumValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FileValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FloatValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FromStringValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.InstantValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.IntValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalDateTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalDateValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LongValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.PathValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.PatternValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.ShortValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.StringValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.UriValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.UrlValueDeserializerFactory;
import com.sigpwned.discourse.core.value.sink.ArrayAppendValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.ListAddValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.SetAddValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.SortedSetAddValueSinkFactory;

/**
 * <p>
 * The default module for the core library. Registers the default deserializers and sinks.
 * </p>
 */
public class DefaultModule extends Module {

  /**
   * <p>
   * Registers the default deserializers.
   * </p>
   *
   * <h4>Dedicated</h4>
   *
   * <p>
   * Supports a wide variety of built-in types specifically:
   * </p>
   *
   * <ul>
   *   <li>{@link BigDecimalValueDeserializerFactory}</li>
   *   <li>{@link BooleanValueDeserializerFactory}</li>
   *   <li>{@link ByteValueDeserializerFactory}</li>
   *   <li>{@link CharValueDeserializerFactory}</li>
   *   <li>{@link DoubleValueDeserializerFactory}</li>
   *   <li>{@link EnumValueDeserializerFactory}</li>
   *   <li>{@link FileValueDeserializerFactory}</li>
   *   <li>{@link FloatValueDeserializerFactory}</li>
   *   <li>{@link InstantValueDeserializerFactory}</li>
   *   <li>{@link IntValueDeserializerFactory}</li>
   *   <li>{@link LocalDateTimeValueDeserializerFactory}</li>
   *   <li>{@link LocalDateValueDeserializerFactory}</li>
   *   <li>{@link LocalTimeValueDeserializerFactory}</li>
   *   <li>{@link LongValueDeserializerFactory}</li>
   *   <li>{@link PathValueDeserializerFactory}</li>
   *   <li>{@link PatternValueDeserializerFactory}</li>
   *   <li>{@link ShortValueDeserializerFactory}</li>
   *   <li>{@link StringValueDeserializerFactory}</li>
   *   <li>{@link UriValueDeserializerFactory}</li>
   *   <li>{@link UrlValueDeserializerFactory}</li>
   * </ul>
   *
   * <h4>General</h4>
   *
   * <p>
   *   Supports general deserialization of any class with the following features:
   * </p>
   *
   * <ul>
   *   <li>
   *     {@link FromStringValueDeserializerFactory} -- Any class that defines a method with the
   *     signature {@code public static T fromString(String)}
   *   </li>
   * </ul>
   *
   * @param resolver the serialization resolver to register the deserializers into
   */
  @Override
  public void registerValueDeserializerFactories(ValueDeserializerResolver resolver) {
    resolver.addLast(StringValueDeserializerFactory.INSTANCE);
    resolver.addLast(LongValueDeserializerFactory.INSTANCE);
    resolver.addLast(IntValueDeserializerFactory.INSTANCE);
    resolver.addLast(CharValueDeserializerFactory.INSTANCE);
    resolver.addLast(ShortValueDeserializerFactory.INSTANCE);
    resolver.addLast(ByteValueDeserializerFactory.INSTANCE);
    resolver.addLast(DoubleValueDeserializerFactory.INSTANCE);
    resolver.addLast(FloatValueDeserializerFactory.INSTANCE);
    resolver.addLast(BigDecimalValueDeserializerFactory.INSTANCE);
    resolver.addLast(BooleanValueDeserializerFactory.INSTANCE);
    resolver.addLast(InstantValueDeserializerFactory.INSTANCE);
    resolver.addLast(LocalDateTimeValueDeserializerFactory.INSTANCE);
    resolver.addLast(LocalDateValueDeserializerFactory.INSTANCE);
    resolver.addLast(LocalTimeValueDeserializerFactory.INSTANCE);
    resolver.addLast(UriValueDeserializerFactory.INSTANCE);
    resolver.addLast(UrlValueDeserializerFactory.INSTANCE);
    resolver.addLast(EnumValueDeserializerFactory.INSTANCE);
    resolver.addLast(FileValueDeserializerFactory.INSTANCE);
    resolver.addLast(PathValueDeserializerFactory.INSTANCE);
    resolver.addLast(PatternValueDeserializerFactory.INSTANCE);

    // This should be the last resort.
    resolver.addLast(FromStringValueDeserializerFactory.INSTANCE);
  }

  /**
   * <p>
   * Registers the default sinks.
   * </p>
   *
   * <ul>
   *   <li>{@link AssignValueSinkFactory}</li>
   *   <li>{@link ArrayAppendValueSinkFactory}</li>
   *   <li>{@link ListAddValueSinkFactory}</li>
   *   <li>{@link SetAddValueSinkFactory}</li>
   *   <li>{@link SortedSetAddValueSinkFactory}</li>
   * </ul>
   *
   * @param resolver the sink resolver to register the sinks into
   */
  @Override
  public void registerValueSinkFactories(ValueSinkResolver resolver) {
    resolver.addLast(SortedSetAddValueSinkFactory.INSTANCE);
    resolver.addLast(SetAddValueSinkFactory.INSTANCE);
    resolver.addLast(ListAddValueSinkFactory.INSTANCE);
    resolver.addLast(ArrayAppendValueSinkFactory.INSTANCE);
  }

  @Override
  public void registerInstanceFactoryProviders(ConfigurableInstanceFactoryProviderChain chain) {
    chain.addLast(new DefaultConstructorConfigurableInstanceFactory.Provider());
  }

  @Override
  public void registerParameterScanners(ConfigurableParameterScannerChain chain) {
  }

  @Override
  public void registerAccessorNamingSchemes(AccessorNamingSchemeChain chain) {
    chain.addLast();
  }
}
