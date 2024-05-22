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

import com.sigpwned.discourse.core.invocation.phase.parse.args.exception.ArgumentException;

/**
 * Tests for all causes of {@link ArgumentException}.
 */
public class ArgumentExceptionTest {

  // public InvocationContext context;
  //
  // @Before
  // public void setupArgumentExceptionTest() {
  // context = new DefaultInvocationContext();
  // context.register(new TestModule());
  // }
  //
  // @After
  // public void cleanupArgumentExceptionTest() {
  // context = null;
  // }
  //
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // @Configurable
  // public static class ConstructorFailureExample {
  //
  // public ConstructorFailureExample() {
  // throw new RuntimeException("simulated failure");
  // }
  //
  // @PositionalParameter(position = 0)
  // public String example;
  // }
  //
  // @Test(expected = NewInstanceFailureBeanException.class)
  // public void givenFailingConstructor_whenInvoke_thenFailWithNoInstanceFailureException() {
  // new InvocationBuilder().scan(ArgumentExceptionTest.ConstructorFailureExample.class, context)
  // .resolve(List.of("hello"), context).parse(context).deserialize(context).prepare(context)
  // .build(context).getConfiguration();
  // }
  //
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // @Configurable
  // public static class PositionalAssignmentFailureExample {
  //
  // @PositionalParameter(position = 0)
  // private String example;
  //
  // public String getExample() {
  // return example;
  // }
  //
  // public void setExample(String example) {
  // throw new RuntimeException("simulated failiure");
  // }
  // }
  //
  // @Test(expected = AssignmentFailureBeanException.class)
  // public void
  // givenFailingSetterForPositionalArgument_whenInvoke_thenFailWithAssignmentFailureException() {
  // Invocation.builder().scan(PositionalAssignmentFailureExample.class, context)
  // .resolve(List.of("hello"), context).parse(context).deserialize(context).prepare(context)
  // .build(context).getConfiguration();
  // }
  //
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // @Configurable
  // public static class OptionAssignmentFailureExample {
  //
  // @OptionParameter(shortName = "x")
  // private String example;
  //
  // public String getExample() {
  // return example;
  // }
  //
  // public void setExample(String example) {
  // throw new RuntimeException("simulated failiure");
  // }
  // }
  //
  // @Test(expected = AssignmentFailureBeanException.class)
  // public void
  // givenFailingSetterForOptionArgument_whenInvoke_thenFailWithAssignmentFailureException() {
  // Invocation.builder().scan(OptionAssignmentFailureExample.class, context)
  // .resolve(List.of("-x", "hello"), context).parse(context).deserialize(context)
  // .prepare(context).build(context).getConfiguration();
  // }
  //
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // @Configurable
  // public static class FlagAssignmentFailureExample {
  //
  // @FlagParameter(shortName = "x")
  // private boolean example;
  //
  // public boolean isExample() {
  // return example;
  // }
  //
  // public void setExample(boolean example) {
  // throw new RuntimeException("simulated failiure");
  // }
  // }
  //
  // @Test(expected = AssignmentFailureBeanException.class)
  // public void
  // givenFailingSetterForFlagArgument_whenInvoke_thenFailWithAssignmentFailureException() {
  // Invocation.builder().scan(FlagAssignmentFailureExample.class, context)
  // .resolve(List.of("-x"), context).parse(context).deserialize(context).prepare(context)
  // .build(context).getConfiguration();
  // }
  //
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // @Configurable
  // public static class MissingRequiredExample {
  //
  // @PositionalParameter(position = 0, required = true)
  // public String example;
  // }
  //
  // @Test(expected = RequiredParametersMissingSyntaxException.class)
  // public void
  // givenArgsWithoutRequiredArgument_whenInvoke_thenFailWithUnassignedRequiredParametersException()
  // {
  // Invocation.builder().scan(MissingRequiredExample.class, context).resolve(List.of(), context)
  // .parse(context).deserialize(context).prepare(context).build(context).getConfiguration();
  // }
  //
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////
  // @Configurable(subcommands = {
  // @Subcommand(discriminator = "alpha", configurable = AlphaMultiExample.class),
  // @Subcommand(discriminator = "bravo", configurable = BravoMultiExample.class)})
  // public abstract static class MultiExample {
  //
  // @OptionParameter(shortName = "o", longName = "option")
  // public String option;
  //
  // @Override
  // public int hashCode() {
  // return Objects.hash(option);
  // }
  //
  // @Override
  // public boolean equals(Object obj) {
  // if (this == obj) {
  // return true;
  // }
  // if (obj == null) {
  // return false;
  // }
  // if (getClass() != obj.getClass()) {
  // return false;
  // }
  // MultiExample other = (MultiExample) obj;
  // return Objects.equals(option, other.option);
  // }
  // }
  //
  // @Configurable(discriminator = "alpha")
  // public static class AlphaMultiExample extends MultiExample {
  //
  // @PositionalParameter(position = 0)
  // public String alpha;
  //
  // @Override
  // public int hashCode() {
  // final int prime = 31;
  // int result = super.hashCode();
  // result = prime * result + Objects.hash(alpha);
  // return result;
  // }
  //
  // @Override
  // public boolean equals(Object obj) {
  // if (this == obj) {
  // return true;
  // }
  // if (!super.equals(obj)) {
  // return false;
  // }
  // if (getClass() != obj.getClass()) {
  // return false;
  // }
  // AlphaMultiExample other = (AlphaMultiExample) obj;
  // return Objects.equals(alpha, other.alpha);
  // }
  // }
  //
  // @Configurable(discriminator = "bravo")
  // public static class BravoMultiExample extends MultiExample {
  //
  // @PositionalParameter(position = 0)
  // public String bravo;
  //
  // @Override
  // public int hashCode() {
  // final int prime = 31;
  // int result = super.hashCode();
  // result = prime * result + Objects.hash(bravo);
  // return result;
  // }
  //
  // @Override
  // public boolean equals(Object obj) {
  // if (this == obj) {
  // return true;
  // }
  // if (!super.equals(obj)) {
  // return false;
  // }
  // if (getClass() != obj.getClass()) {
  // return false;
  // }
  // BravoMultiExample other = (BravoMultiExample) obj;
  // return Objects.equals(bravo, other.bravo);
  // }
  // }
  //
  // @Test
  // public void
  // givenMultiCommandAndArgsWithoutDiscriminators_whenInvoke_thenFailWithNoSubcommandException() {
  // final AtomicBoolean threw = new AtomicBoolean(false);
  // context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).orElseThrow()
  // .addFirst(new EmptyArgsToMultiCommandInterceptingDiscourseListener() {
  // @Override
  // public void checkArgs(Command<?> rootCommand, List<String> args,
  // InvocationContext context) {
  // try {
  // super.checkArgs(rootCommand, args, context);
  // } catch (ExitError e) {
  // threw.set(true);
  // throw e;
  // }
  // }
  // });
  // boolean caught = false;
  // try {
  // Invocation.builder().scan(MultiExample.class, context).resolve(List.of(), context);
  // } catch (ExitError e) {
  // caught = true;
  // }
  // assertThat(threw.get(), is(true));
  // assertThat(caught, is(true));
  // }
  //
  // @Test(expected = UnrecognizedDiscriminatorSyntaxException.class)
  // public void
  // givenMultiCommandAndArgsWithUnrecognizedDiscriminator_whenInvoke_thenFailWithUnrecognizedSubcommandException()
  // {
  // Invocation.builder().scan(MultiExample.class, context).resolve(List.of("charlie"), context);
  // }
  //
  // @Test(expected = InvalidDiscriminatorSyntaxException.class)
  // public void
  // givenMultiCommandAndArgsWithInvalidDiscriminator_whenInvoke_thenFailWithInvalidDiscriminatorException()
  // {
  // Invocation.builder().scan(MultiExample.class, context).resolve(List.of("-"), context);
  // }
}
