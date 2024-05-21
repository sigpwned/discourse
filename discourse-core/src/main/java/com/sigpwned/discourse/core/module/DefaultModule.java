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
import com.sigpwned.discourse.core.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleEvaluator;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.module.scan.naming.BeanGetterNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.BeanSetterNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.DefaultConstructorNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.DiscourseAttributeAnnotationNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.DiscourseIgnoreAnnotationNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.FieldNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.ParameterNamingScheme;
import com.sigpwned.discourse.core.module.scan.naming.RecordAccessorNamingScheme;
import com.sigpwned.discourse.core.module.scan.rules.detect.DefaultConstructorRuleDetector;
import com.sigpwned.discourse.core.module.scan.rules.detect.FieldRuleDetector;
import com.sigpwned.discourse.core.module.scan.rules.detect.SetterMethodRuleDetector;
import com.sigpwned.discourse.core.module.scan.rules.eval.DefaultConstructorCallRuleEvaluator;
import com.sigpwned.discourse.core.module.scan.rules.eval.FieldAssignmentRuleEvaluator;
import com.sigpwned.discourse.core.module.scan.rules.eval.SetterMethodCallRuleEvaluator;
import com.sigpwned.discourse.core.module.scan.rules.nominate.DefaultConstructorRuleNominator;
import com.sigpwned.discourse.core.module.scan.rules.nominate.FieldRuleNominator;
import com.sigpwned.discourse.core.module.scan.rules.nominate.SetterMethodRuleNominator;
import com.sigpwned.discourse.core.module.scan.syntax.detect.OptionSyntaxDetector;
import com.sigpwned.discourse.core.module.scan.syntax.detect.PositionalSyntaxDetector;
import com.sigpwned.discourse.core.module.scan.syntax.nominate.FieldSyntaxNominator;
import com.sigpwned.discourse.core.module.scan.syntax.nominate.GetterMethodSyntaxNominator;
import com.sigpwned.discourse.core.module.scan.syntax.nominate.SetterMethodSyntaxNominator;
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
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactory;

/**
 * The default module for the core library. Any new functionality that is added to the core library
 * should be registered here. All values are added to the ends of their respectivee chains so that
 * they do not supersede existing values already registered by other modules unless otherwise noted.
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
   * <li>{@link BigDecimalValueDeserializerFactory}</li>
   * <li>{@link BooleanValueDeserializerFactory}</li>
   * <li>{@link ByteValueDeserializerFactory}</li>
   * <li>{@link CharValueDeserializerFactory}</li>
   * <li>{@link DiscourseDeserializeValueSerializerFactory}</li>
   * <li>{@link DoubleValueDeserializerFactory}</li>
   * <li>{@link EnumValueDeserializerFactory}</li>
   * <li>{@link FileValueDeserializerFactory}</li>
   * <li>{@link FloatValueDeserializerFactory}</li>
   * <li>{@link InstantValueDeserializerFactory}</li>
   * <li>{@link IntValueDeserializerFactory}</li>
   * <li>{@link LocalDateTimeValueDeserializerFactory}</li>
   * <li>{@link LocalDateValueDeserializerFactory}</li>
   * <li>{@link LocalTimeValueDeserializerFactory}</li>
   * <li>{@link LongValueDeserializerFactory}</li>
   * <li>{@link PathValueDeserializerFactory}</li>
   * <li>{@link PatternValueDeserializerFactory}</li>
   * <li>{@link ShortValueDeserializerFactory}</li>
   * <li>{@link StringValueDeserializerFactory}</li>
   * <li>{@link UriValueDeserializerFactory}</li>
   * <li>{@link UrlValueDeserializerFactory}</li>
   * </ul>
   *
   * <h4>General</h4>
   *
   * <p>
   * Supports general deserialization of any class with the following features:
   * </p>
   *
   * <ul>
   * <li>{@link FromStringValueDeserializerFactory} -- Any class that defines a method with the
   * signature {@code public static T fromString(String)}</li>
   * </ul>
   *
   * @param chain the serialization resolver to register the deserializers into
   */
  @Override
  public void registerValueDeserializerFactories(Chain<ValueDeserializerFactory<?>> chain) {
    chain.addLast(StringValueDeserializerFactory.INSTANCE);
    chain.addLast(LongValueDeserializerFactory.INSTANCE);
    chain.addLast(IntValueDeserializerFactory.INSTANCE);
    chain.addLast(CharValueDeserializerFactory.INSTANCE);
    chain.addLast(ShortValueDeserializerFactory.INSTANCE);
    chain.addLast(ByteValueDeserializerFactory.INSTANCE);
    chain.addLast(DoubleValueDeserializerFactory.INSTANCE);
    chain.addLast(FloatValueDeserializerFactory.INSTANCE);
    chain.addLast(BigDecimalValueDeserializerFactory.INSTANCE);
    chain.addLast(BooleanValueDeserializerFactory.INSTANCE);
    chain.addLast(InstantValueDeserializerFactory.INSTANCE);
    chain.addLast(LocalDateTimeValueDeserializerFactory.INSTANCE);
    chain.addLast(LocalDateValueDeserializerFactory.INSTANCE);
    chain.addLast(LocalTimeValueDeserializerFactory.INSTANCE);
    chain.addLast(UriValueDeserializerFactory.INSTANCE);
    chain.addLast(UrlValueDeserializerFactory.INSTANCE);
    chain.addLast(EnumValueDeserializerFactory.INSTANCE);
    chain.addLast(FileValueDeserializerFactory.INSTANCE);
    chain.addLast(PathValueDeserializerFactory.INSTANCE);
    chain.addLast(PatternValueDeserializerFactory.INSTANCE);

    // This should be the first resort.
    chain.addFirst(DiscourseDeserializeValueSerializerFactory.INSTANCE);

    // This should be the last resort.
    chain.addLast(FromStringValueDeserializerFactory.INSTANCE);
  }



  /**
   * <p>
   * Registers the default sinks.
   * </p>
   *
   * <ul>
   * <li>{@link AssignValueSinkFactory}</li>
   * <li>{@link ArrayAppendValueSinkFactory}</li>
   * <li>{@link ListAddValueSinkFactory}</li>
   * <li>{@link SetAddValueSinkFactory}</li>
   * <li>{@link SortedSetAddValueSinkFactory}</li>
   * </ul>
   *
   * @param resolver the sink resolver to register the sinks into
   */
  @Override
  public void registerValueSinkFactories(Chain<ValueSinkFactory> chain) {
    chain.addLast(SortedSetAddValueSinkFactory.INSTANCE);
    chain.addLast(SetAddValueSinkFactory.INSTANCE);
    chain.addLast(ListAddValueSinkFactory.INSTANCE);
    chain.addLast(ArrayAppendValueSinkFactory.INSTANCE);
    chain.addLast(AssignValueSinkFactory.INSTANCE);
  }


  /**
   * <p>
   * Registers the default accessor naming schemes.
   * </p>
   *
   * <ul>
   * <li>{@link DiscourseIgnoreAnnotationNamingScheme}</li>
   * <li>{@link DiscourseAttributeAnnotationNamingScheme}</li>
   * <li>{@link BeanAccessorNamingScheme}</li>
   * <li>{@link FieldNamingScheme}</li>
   * <li>{@link ParameterNamingScheme}</li>
   * <li>{@link RecordAccessorNamingScheme}</li>
   * </ul>
   *
   * <p>
   * {@link DiscourseIgnoreAnnotationNamingScheme} is registered at the front of the chain so that
   * it can be used to ignore fields that are annotated with {@link DiscourseIgnore}. This means it
   * will supersede any existing schemes already in the chain.
   * </p>
   *
   * @param chain the chain to register the accessor naming schemes into
   */
  @Override
  public void registerNamingSchemes(Chain<NamingScheme> chain) {
    chain.addFirst(DiscourseIgnoreAnnotationNamingScheme.INSTANCE);
    chain.addLast(DiscourseAttributeAnnotationNamingScheme.INSTANCE);
    chain.addLast(FieldNamingScheme.INSTANCE);
    chain.addLast(DefaultConstructorNamingScheme.INSTANCE);
    chain.addLast(BeanGetterNamingScheme.INSTANCE);
    chain.addLast(BeanSetterNamingScheme.INSTANCE);
    // chain.addLast(ParameterNamingScheme.INSTANCE);
    // chain.addLast(RecordAccessorNamingScheme.INSTANCE);
  }

  @Override
  public void registerSyntaxNominators(Chain<SyntaxNominator> chain) {
    chain.addLast(FieldSyntaxNominator.INSTANCE);
    chain.addLast(SetterMethodSyntaxNominator.INSTANCE);
    chain.addLast(GetterMethodSyntaxNominator.INSTANCE);
  }

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {
    chain.addLast(OptionSyntaxDetector.INSTANCE);
    chain.addLast(PositionalSyntaxDetector.INSTANCE);
  }

  @Override
  public void registerRuleNominators(Chain<RuleNominator> chain) {
    chain.addLast(FieldRuleNominator.INSTANCE);
    chain.addLast(SetterMethodRuleNominator.INSTANCE);
    chain.addLast(DefaultConstructorRuleNominator.INSTANCE);
  }

  @Override
  public void registerRuleDetectors(Chain<RuleDetector> chain) {
    chain.addLast(FieldRuleDetector.INSTANCE);
    chain.addLast(SetterMethodRuleDetector.INSTANCE);
    chain.addLast(DefaultConstructorRuleDetector.INSTANCE);
  }



  @Override
  public void registerRuleEvaluators(Chain<RuleEvaluator> chain) {
    chain.addLast(DefaultConstructorCallRuleEvaluator.INSTANCE);
    chain.addLast(SetterMethodCallRuleEvaluator.INSTANCE);
    chain.addLast(FieldAssignmentRuleEvaluator.INSTANCE);
  }



  /**
   * <p>
   * Registers the default discourse listeners.
   * </p>
   *
   * <ul>
   * <li>{@link EmptyArgsToMultiCommandInterceptingDiscourseListener}</li>
   * <li>{@link HelpFlagInterceptingDiscourseListener}</li>
   * <li>{@link VersionFlagInterceptingDiscourseListener}</li>
   * </ul>
   *
   * @param chain the chain to register the discourse listeners into
   */
  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {}
}
