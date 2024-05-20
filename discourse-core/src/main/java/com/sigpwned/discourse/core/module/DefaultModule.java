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

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.annotation.DiscourseIgnore;
import com.sigpwned.discourse.core.chain.AccessorNamingSchemeChain;
import com.sigpwned.discourse.core.chain.ConfigurableComponentScannerChain;
import com.sigpwned.discourse.core.chain.ConfigurableInstanceFactoryScannerChain;
import com.sigpwned.discourse.core.chain.DiscourseListenerChain;
import com.sigpwned.discourse.core.chain.ExceptionFormatterChain;
import com.sigpwned.discourse.core.chain.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.configurable.component.scanner.FieldConfigurableCandidateComponentScanner;
import com.sigpwned.discourse.core.configurable.component.scanner.GetterConfigurableCandidateComponentScanner;
import com.sigpwned.discourse.core.configurable.component.scanner.SetterConfigurableCandidateComponentScanner;
import com.sigpwned.discourse.core.configurable.instance.factory.scanner.AnnotatedConstructorConfigurableInstanceFactoryScanner;
import com.sigpwned.discourse.core.configurable.instance.factory.scanner.DefaultConstructorConfigurableInstanceFactoryScanner;
import com.sigpwned.discourse.core.format.exception.ArgumentExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.BeanExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.CatchAllErrorFormatter;
import com.sigpwned.discourse.core.format.exception.CatchAllExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.ConfigurationExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.EmptyArgsRequiredParametersMissingSyntaxExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter;
import com.sigpwned.discourse.core.format.exception.SyntaxExceptionFormatter;
import com.sigpwned.discourse.core.listener.EmptyArgsToMultiCommandInterceptingDiscourseListener;
import com.sigpwned.discourse.core.listener.HelpFlagInterceptingDiscourseListener;
import com.sigpwned.discourse.core.listener.VersionFlagInterceptingDiscourseListener;
import com.sigpwned.discourse.core.module.scan.naming.BeanAccessorNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.DiscourseAttributeAnnotationNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.DiscourseIgnoreAnnotationNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.FieldNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.ParameterNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.RecordAccessorNamingScheme;
import com.sigpwned.discourse.core.module.value.deserializer.BigDecimalValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.BooleanValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.ByteValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.CharValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.DiscourseDeserializeValueSerializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.DoubleValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.EnumValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.FileValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.FloatValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.FromStringValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.InstantValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.IntValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.LocalDateTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.LocalDateValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.LocalTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.LongValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.PathValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.PatternValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.ShortValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.StringValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.UriValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.UrlValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.sink.ArrayAppendValueSinkFactory;
import com.sigpwned.discourse.core.module.value.sink.AssignValueSinkFactory;
import com.sigpwned.discourse.core.module.value.sink.ListAddValueSinkFactory;
import com.sigpwned.discourse.core.module.value.sink.SetAddValueSinkFactory;
import com.sigpwned.discourse.core.module.value.sink.SortedSetAddValueSinkFactory;

/**
 * The default module for the core library. Any new functionality that is added to the core library
 * should be registered here. All values are added to the ends of their respectivee chains so that
 * they do not supersede existing values already registered by other modules unless otherwise
 * noted.
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
   *   <li>{@link DiscourseDeserializeValueSerializerFactory}</li>
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
  public void registerValueDeserializerFactories(Chain<ValueDeserializerFactory> resolver) {
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

    // This should be the first resort.
    resolver.addFirst(DiscourseDeserializeValueSerializerFactory.INSTANCE);

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
  public void registerValueSinkFactories(ValueSinkFactoryChain resolver) {
    resolver.addLast(SortedSetAddValueSinkFactory.INSTANCE);
    resolver.addLast(SetAddValueSinkFactory.INSTANCE);
    resolver.addLast(ListAddValueSinkFactory.INSTANCE);
    resolver.addLast(ArrayAppendValueSinkFactory.INSTANCE);
  }

  /**
   * <p>
   * Registers the default instance factory providers.
   * </p>
   *
   * <ul>
   *   <li>{@link DefaultConstructorConfigurableInstanceFactoryScanner}</li>
   *   <li>{@link AnnotatedConstructorConfigurableInstanceFactoryScanner}</li>
   * </ul>
   *
   * @param chain the chain to register the instance factory providers into
   */
  @Override
  public void registerInstanceFactoryScanners(ConfigurableInstanceFactoryScannerChain chain) {
    chain.addLast(new DefaultConstructorConfigurableInstanceFactoryScanner());
    chain.addLast(new AnnotatedConstructorConfigurableInstanceFactoryScanner());
  }

  /**
   * <p>
   * Registers the default component scanners.
   * </p>
   *
   * <ul>
   *   <li>{@link FieldConfigurableCandidateComponentScanner}</li>
   *   <li>{@link GetterConfigurableCandidateComponentScanner}</li>
   *   <li>{@link SetterConfigurableCandidateComponentScanner}</li>
   * </ul>
   *
   * @param chain the chain to register the component scanners into
   */
  @Override
  public void registerConfigurableComponentScanners(ConfigurableComponentScannerChain chain) {
    chain.addLast(FieldConfigurableCandidateComponentScanner.INSTANCE);
    chain.addLast(GetterConfigurableCandidateComponentScanner.INSTANCE);
    chain.addLast(SetterConfigurableCandidateComponentScanner.INSTANCE);
  }

  /**
   * <p>
   * Registers the default accessor naming schemes.
   * </p>
   *
   * <ul>
   *   <li>{@link DiscourseIgnoreAnnotationNamingScheme}</li>
   *   <li>{@link DiscourseAttributeAnnotationNamingScheme}</li>
   *   <li>{@link BeanAccessorNamingScheme}</li>
   *   <li>{@link FieldNamingScheme}</li>
   *   <li>{@link ParameterNamingScheme}</li>
   *   <li>{@link RecordAccessorNamingScheme}</li>
   * </ul>
   *
   * <p>
   *   {@link DiscourseIgnoreAnnotationNamingScheme} is registered at the front of the chain
   *   so that it can be used to ignore fields that are annotated with {@link DiscourseIgnore}.
   *   This means it will supersede any existing schemes already in the chain.
   * </p>
   *
   * @param chain the chain to register the accessor naming schemes into
   */
  @Override
  public void registerAccessorNamingSchemes(AccessorNamingSchemeChain chain) {
    chain.addFirst(DiscourseIgnoreAnnotationNamingScheme.INSTANCE);
    chain.addLast(DiscourseAttributeAnnotationNamingScheme.INSTANCE);
    chain.addLast(BeanAccessorNamingScheme.INSTANCE);
    chain.addLast(FieldNamingScheme.INSTANCE);
    chain.addLast(ParameterNamingScheme.INSTANCE);
    chain.addLast(RecordAccessorNamingScheme.INSTANCE);
  }

  /**
   * <p>
   * Registers the default exception formatters.
   * </p>
   *
   * <ul>
   *   <li>{@link EmptyArgsRequiredParametersMissingSyntaxExceptionFormatter}</li>
   *   <li>{@link HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter}</li>
   *   <li>{@link ConfigurationExceptionFormatter}</li>
   *   <li>{@link SyntaxExceptionFormatter}</li>
   *   <li>{@link BeanExceptionFormatter}</li>
   *   <li>{@link ArgumentExceptionFormatter}</li>
   *   <li>{@link CatchAllExceptionFormatter}</li>
   *   <li>{@link CatchAllErrorFormatter}</li>
   * </ul>
   *
   * @param chain the chain to register the exception formatters into
   */
  @Override
  public void registerExceptionFormatters(ExceptionFormatterChain chain) {
    chain.addLast(EmptyArgsRequiredParametersMissingSyntaxExceptionFormatter.INSTANCE);
    chain.addLast(HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter.INSTANCE);
    chain.addLast(ConfigurationExceptionFormatter.INSTANCE);
    chain.addLast(SyntaxExceptionFormatter.INSTANCE);
    chain.addLast(BeanExceptionFormatter.INSTANCE);
    chain.addLast(ArgumentExceptionFormatter.INSTANCE);
    chain.addLast(CatchAllExceptionFormatter.INSTANCE);
    chain.addLast(CatchAllErrorFormatter.INSTANCE);
  }

  /**
   * <p>
   * Registers the default discourse listeners.
   * </p>
   *
   * <ul>
   *   <li>{@link EmptyArgsToMultiCommandInterceptingDiscourseListener}</li>
   *   <li>{@link HelpFlagInterceptingDiscourseListener}</li>
   *   <li>{@link VersionFlagInterceptingDiscourseListener}</li>
   * </ul>
   *
   * @param chain the chain to register the discourse listeners into
   */
  @Override
  public void registerDiscourseListeners(DiscourseListenerChain chain) {
    chain.addLast(EmptyArgsToMultiCommandInterceptingDiscourseListener.INSTANCE);
    chain.addLast(HelpFlagInterceptingDiscourseListener.INSTANCE);
    chain.addLast(VersionFlagInterceptingDiscourseListener.INSTANCE);
  }
}
