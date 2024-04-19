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

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.exception.configuration.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidCollectionParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidLongNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPropertyNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidShortNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidVariableNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultiCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleHelpFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleVersionFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedSubcommandsConfigurationException;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class ConfigurationExceptionTest {

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class GapInPositionsExample {

    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 1)
    public String position1;
  }

  @Test(expected = MissingPositionConfigurationException.class)
  public void givenClassWithGapInPositionalArguments_whenScan_thenFailWithMissingPositionException() {
    Command.scan(GapInPositionsExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class TooManyAnnotationsExample {

    // Note that we have two annotations on this field, which is illegal
    @OptionParameter(shortName = "o", longName = "option")
    @FlagParameter(shortName = "f", longName = "flag")
    public boolean example;
  }

  @Test(expected = TooManyAnnotationsConfigurationException.class)
  public void givenClassWithFieldWithMultipleAnnotations_whenScan_thenFailWithTooManyAnnotationsException() {
    Command.scan(TooManyAnnotationsExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////

  // Note that this is not marked @Configurable
  public static class NotConfigurableExample {

    @OptionParameter(shortName = "o", longName = "option")
    public boolean example;
  }

  @Test(expected = NotConfigurableConfigurationException.class)
  public void givenClassWithoutConfigurableAnnotation_whenScan_thenFailWithNotConfigurableException() {
    Command.scan(NotConfigurableExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidOptionShortNameExample {

    @OptionParameter(shortName = "-")
    public String example;
  }

  @Test(expected = InvalidShortNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidOptionShortNameAnnotation_whenScan_thenFailWithInvalidShortNameException() {
    Command.scan(InvalidOptionShortNameExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidOptionLongNameExample {

    @OptionParameter(longName = "-")
    public String example;
  }

  @Test(expected = InvalidLongNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidOptionLongNameAnnotation_whenScan_thenFailWithInvalidLongNameException() {
    Command.scan(InvalidOptionLongNameExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class NoNameOptionExample {

    @OptionParameter
    public String example;
  }

  @Test(expected = NoNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidOptionAnnotation_whenScan_thenFailWithNoNameException() {
    Command.scan(NoNameOptionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidFlagShortNameExample {

    @FlagParameter(shortName = "-")
    public boolean example;
  }

  @Test(expected = InvalidShortNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidFlagShortNameAnnotation_whenScan_thenFailWithInvalidShortNameException() {
    Command.scan(InvalidFlagShortNameExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidFlagLongNameExample {

    @FlagParameter(longName = "-")
    public boolean example;
  }

  @Test(expected = InvalidLongNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidFlagLongNameAnnotation_whenScan_thenFailWithInvalidLongNameException() {
    Command.scan(InvalidFlagLongNameExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class NoNameFlagExample {

    @FlagParameter
    public boolean example;
  }

  @Test(expected = NoNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidFlagAnnotation_whenScan_thenFailWithNoNameException() {
    Command.scan(NoNameFlagExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidVariableExample {

    @EnvironmentParameter(variableName = "")
    public String example;
  }

  @Test(expected = InvalidVariableNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidVariableNameAnnotation_whenScan_thenFailWithInvalidVariableNameException() {
    Command.scan(InvalidVariableExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidPropertyExample {

    @PropertyParameter(propertyName = "")
    public String example;
  }

  @Test(expected = InvalidPropertyNameConfigurationException.class)
  public void givenClassWithFieldWithInvalidPropertyNameAnnotation_whenScan_thenFailWithInvalidPropertyNameException() {
    Command.scan(InvalidPropertyExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class DuplicateCoordinateExample {

    @OptionParameter(shortName = "x")
    public String example1;

    @OptionParameter(shortName = "x")
    public String example2;
  }

  @Test(expected = DuplicateCoordinateConfigurationException.class)
  public void givenClassWithFieldWithDuplicateShortNames_whenScan_thenFailWithDuplicateCoordinateException() {
    Command.scan(DuplicateCoordinateExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidPositionExample {

    @PositionalParameter(position = -1)
    public String example;
  }

  @Test(expected = InvalidPositionConfigurationException.class)
  public void givenClassWithFieldWithInvalidPosition_whenScan_thenFailWithInvalidPositionException() {
    Command.scan(InvalidPositionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class SkipPositionExample {

    @PositionalParameter(position = 0)
    public String example1;

    @PositionalParameter(position = 2)
    public String example2;
  }

  @Test(expected = MissingPositionConfigurationException.class)
  public void givenClassWithFieldWithMissingPosition_whenScan_thenFailWithMissingPositionException() {
    Command.scan(SkipPositionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class NoZeroPositionExample {

    @PositionalParameter(position = 1)
    public String example1;
  }

  @Test(expected = MissingPositionConfigurationException.class)
  public void givenClassWithFieldWithNoZeroPosition_whenScan_thenFailWithMissingPositionException() {
    Command.scan(SkipPositionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidCollectionPositionExample {

    @PositionalParameter(position = 0)
    public List<String> example1;

    @PositionalParameter(position = 1)
    public String example2;
  }

  @Test(expected = InvalidCollectionParameterPlacementConfigurationException.class)
  public void givenClassWithCollectionFieldNotInLastPosition_whenScan_thenFailWithInvalidCollectionParameterPlacementException() {
    Command.scan(InvalidCollectionPositionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class InvalidRequiredPositionExample {

    @PositionalParameter(position = 0, required = false)
    public String position0;

    @PositionalParameter(position = 1, required = true)
    public String position1;
  }

  @Test(expected = InvalidRequiredParameterPlacementConfigurationException.class)
  public void givenClassWithRequiredFieldBeforeOptionalField_whenScan_thenFailWithInvalidRequiredParameterPlacementException() {
    Command.scan(InvalidRequiredPositionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = DiscriminatorMismatchMultiSubcommandExample.class)})
  public abstract static class DiscriminatorMismatchMultiCommandExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "bravo")
  public static class DiscriminatorMismatchMultiSubcommandExample extends
      DiscriminatorMismatchMultiCommandExample {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = DiscriminatorMismatchConfigurationException.class)
  public void givenClassWithSubcommandDiscriminatorMismatch_whenScan_thenFailWithDiscriminatorMismatchException() {
    Command.scan(DiscriminatorMismatchMultiCommandExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = NoExtendMultiSubcommandExample.class)})
  public abstract static class NoExtendMultiCommandExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "bravo")
  public static class NoExtendMultiSubcommandExample {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = SubcommandDoesNotExtendRootCommandConfigurationException.class)
  public void givenClassWithSubcommandNotExtendsCommand_whenScan_thenFailWithSubcommandDoesNotExtendRootCommandException() {
    Command.scan(NoExtendMultiCommandExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "-", configurable = InvalidDiscriminatorMultiSubcommandExample1.class)})
  public abstract static class InvalidDiscriminatorMultiCommandExample1 {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "bravo")
  public static class InvalidDiscriminatorMultiSubcommandExample1 extends
      InvalidDiscriminatorMultiCommandExample1 {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = InvalidDiscriminatorConfigurationException.class)
  public void givenClassWithInvalidDiscriminatorInCommand_whenScan_thenFailWithInvalidDiscriminatorException() {
    Command.scan(InvalidDiscriminatorMultiCommandExample1.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = InvalidDiscriminatorMultiSubcommandExample2.class)})
  public abstract static class InvalidDiscriminatorMultiCommandExample2 {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "-")
  public static class InvalidDiscriminatorMultiSubcommandExample2 extends
      InvalidDiscriminatorMultiCommandExample2 {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = InvalidDiscriminatorConfigurationException.class)
  public void givenClassWithInvalidDiscriminatorInSubcommand_whenScan_thenFailWithInvalidDiscriminatorException() {
    Command.scan(InvalidDiscriminatorMultiCommandExample2.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "", configurable = NoDiscriminatorMultiSubcommandExample1.class)})
  public abstract static class NoDiscriminatorMultiCommandExample1 {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "alpha")
  public static class NoDiscriminatorMultiSubcommandExample1 extends
      NoDiscriminatorMultiCommandExample1 {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = NoDiscriminatorConfigurationException.class)
  public void givenClassWithEmptyDiscriminatorInCommand_whenScan_thenFailWithNoDiscriminatorException() {
    Command.scan(NoDiscriminatorMultiCommandExample1.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = NoDiscriminatorMultiSubcommandExample2.class)})
  public abstract static class NoDiscriminatorMultiCommandExample2 {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "")
  public static class NoDiscriminatorMultiSubcommandExample2 extends
      NoDiscriminatorMultiCommandExample2 {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = NoDiscriminatorConfigurationException.class)
  public void givenClassWithEmptyDiscriminatorInSubcommand_whenScan_thenFailWithNoDiscriminatorException() {
    Command.scan(NoDiscriminatorMultiCommandExample2.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(discriminator = "foo", subcommands = {
      @Subcommand(discriminator = "alpha", configurable = UnexpectedDiscriminatorSubcommandExample.class)})
  public abstract static class UnexpectedDiscriminatorRootCommandExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "alpha")
  public static class UnexpectedDiscriminatorSubcommandExample extends
      UnexpectedDiscriminatorRootCommandExample {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = UnexpectedDiscriminatorConfigurationException.class)
  public void givenClassWithDiscriminatorInCommandAndSubcommands_whenScan_thenFailWithUnexpectedDiscriminatorException() {
    Command.scan(UnexpectedDiscriminatorRootCommandExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(discriminator = "foo")
  public abstract static class UnexpectedDiscriminatorExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Test(expected = UnexpectedDiscriminatorConfigurationException.class)
  public void givenClassWithDiscriminatorInCommandAndNoSubcommands_whenScan_thenFailWithUnexpectedDiscriminatorException() {
    Command.scan(UnexpectedDiscriminatorExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = UnexpectedSubcommandsSubcommandExample.class)})
  public abstract static class UnexpectedSubcommandsRootCommandExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "alpha", subcommands = {
      @Subcommand(discriminator = "foo", configurable = UnexpectedSubcommandsSubcommand2Example.class)})
  public static class UnexpectedSubcommandsSubcommandExample extends
      UnexpectedSubcommandsRootCommandExample {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Configurable(discriminator = "foo")
  public static class UnexpectedSubcommandsSubcommand2Example extends
      UnexpectedSubcommandsSubcommandExample {

    @PositionalParameter(position = 1)
    public String bravo;
  }

  @Ignore("This is no longer valid since subcommands can now have subcommands")
  @Test(expected = UnexpectedSubcommandsConfigurationException.class)
  public void unexpectedSubcommandsExample() {
    Command.scan(UnexpectedSubcommandsRootCommandExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = NotAbstractSubcommandExample.class)})
  public static class NotAbstractCommandExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "alpha")
  public static class NotAbstractSubcommandExample extends NotAbstractCommandExample {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = MultiCommandNotAbstractConfigurationException.class)
  public void givenConcreteClassWithSubcommands_whenScan_thenFailWithMultiCommandNotAbstractException() {
    Command.scan(NotAbstractCommandExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class MultipleHelpExample {

    @FlagParameter(longName = "help1", help = true)
    public boolean help1;

    @FlagParameter(longName = "help2", help = true)
    public boolean help2;
  }

  @Test(expected = MultipleHelpFlagsConfigurationException.class)
  public void givenClassWithMultipleHelpFields_whenScan_thenFailWithMultipleHelpFlagsException() {
    Command.scan(MultipleHelpExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class MultipleVersionExample {

    @FlagParameter(longName = "version1", version = true)
    public boolean version1;

    @FlagParameter(longName = "version2", version = true)
    public boolean version2;
  }

  @Test(expected = MultipleVersionFlagsConfigurationException.class)
  public void givenClassWithMultipleVersionFields_whenScan_thenFailWithMultipleVersionFlagsException() {
    Command.scan(MultipleVersionExample.class);
  }
}