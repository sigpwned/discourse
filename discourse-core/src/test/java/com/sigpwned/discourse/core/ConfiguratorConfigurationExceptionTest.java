package com.sigpwned.discourse.core;

import java.util.List;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.annotation.Subcommand;
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
import com.sigpwned.discourse.core.exception.configuration.MultipleHelpFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleVersionFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.RootCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedSubcommandsConfigurationException;

public class ConfiguratorConfigurationExceptionTest {
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
  public void missingPositionTest() {
    new CommandBuilder().build(GapInPositionsExample.class);
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
  public void tooManyAnnotationsTest() {
    new CommandBuilder().build(TooManyAnnotationsExample.class);
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
  public void notConfigurableTest() {
    new CommandBuilder().build(NotConfigurableExample.class).args();
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
  public void invalidOptionShortNameExample() {
    new CommandBuilder().build(InvalidOptionShortNameExample.class);
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
  public void invalidOptionLongNameExample() {
    new CommandBuilder().build(InvalidOptionLongNameExample.class);
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
  public void optionNoNameExample() {
    new CommandBuilder().build(NoNameOptionExample.class);
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
  public void invalidFlagShortNameExample() {
    new CommandBuilder().build(InvalidFlagShortNameExample.class);
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
  public void invalidFlagLongNameExample() {
    new CommandBuilder().build(InvalidFlagLongNameExample.class);
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
  public void flagNoNameExample() {
    new CommandBuilder().build(NoNameFlagExample.class);
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
  public void invalidVariableExample() {
    new CommandBuilder().build(InvalidVariableExample.class);
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
  public void invalidPropertyExample() {
    new CommandBuilder().build(InvalidPropertyExample.class);
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
  public void duplicateShortNameExample() {
    new CommandBuilder().build(DuplicateCoordinateExample.class);
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
  public void invalidPositionExample() {
    new CommandBuilder().build(InvalidPositionExample.class);
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
  public void skipPositionExample() {
    new CommandBuilder().build(SkipPositionExample.class);
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
  public void noZeroPositionExample() {
    new CommandBuilder().build(SkipPositionExample.class);
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
  public void invalidCollectionPositionExample() {
    new CommandBuilder().build(InvalidCollectionPositionExample.class);
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
  public void invalidRequiredPositionExample() {
    new CommandBuilder().build(InvalidRequiredPositionExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {@Subcommand(discriminator = "alpha",
      configurable = DiscriminatorMismatchMultiSubcommandExample.class)})
  public abstract static class DiscriminatorMismatchMultiCommandExample {
    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "bravo")
  public static class DiscriminatorMismatchMultiSubcommandExample
      extends DiscriminatorMismatchMultiCommandExample {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = DiscriminatorMismatchConfigurationException.class)
  public void discriminatorMismatchExample() {
    new CommandBuilder().build(DiscriminatorMismatchMultiCommandExample.class);
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
  public void noExtendExample() {
    new CommandBuilder().build(NoExtendMultiCommandExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {@Subcommand(discriminator = "-",
      configurable = InvalidDiscriminatorMultiSubcommandExample1.class)})
  public abstract static class InvalidDiscriminatorMultiCommandExample1 {
    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "bravo")
  public static class InvalidDiscriminatorMultiSubcommandExample1
      extends InvalidDiscriminatorMultiCommandExample1 {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = InvalidDiscriminatorConfigurationException.class)
  public void invalidDiscriminatorCommandExample() {
    new CommandBuilder().build(InvalidDiscriminatorMultiCommandExample1.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {@Subcommand(discriminator = "alpha",
      configurable = InvalidDiscriminatorMultiSubcommandExample2.class)})
  public abstract static class InvalidDiscriminatorMultiCommandExample2 {
    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "-")
  public static class InvalidDiscriminatorMultiSubcommandExample2
      extends InvalidDiscriminatorMultiCommandExample2 {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = InvalidDiscriminatorConfigurationException.class)
  public void invalidDiscriminatorSubcommandExample() {
    new CommandBuilder().build(InvalidDiscriminatorMultiCommandExample2.class);
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
  public static class NoDiscriminatorMultiSubcommandExample1
      extends NoDiscriminatorMultiCommandExample1 {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = NoDiscriminatorConfigurationException.class)
  public void noDiscriminatorCommandExample() {
    new CommandBuilder().build(NoDiscriminatorMultiCommandExample1.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {@Subcommand(discriminator = "alpha",
      configurable = NoDiscriminatorMultiSubcommandExample2.class)})
  public abstract static class NoDiscriminatorMultiCommandExample2 {
    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "")
  public static class NoDiscriminatorMultiSubcommandExample2
      extends NoDiscriminatorMultiCommandExample2 {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = NoDiscriminatorConfigurationException.class)
  public void noDiscriminatorSubcommandExample() {
    new CommandBuilder().build(NoDiscriminatorMultiCommandExample2.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(discriminator = "foo", subcommands = {@Subcommand(discriminator = "alpha",
      configurable = UnexpectedDiscriminatorSubcommandExample.class)})
  public abstract static class UnexpectedDiscriminatorRootCommandExample {
    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "")
  public static class UnexpectedDiscriminatorSubcommandExample
      extends UnexpectedDiscriminatorRootCommandExample {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Test(expected = UnexpectedDiscriminatorConfigurationException.class)
  public void unexpectedCommandDiscriminatorExample() {
    new CommandBuilder().build(UnexpectedDiscriminatorRootCommandExample.class);
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
  public void unexpectedDiscriminatorExample() {
    new CommandBuilder().build(UnexpectedDiscriminatorExample.class);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(subcommands = {@Subcommand(discriminator = "alpha",
      configurable = UnexpectedSubcommandsSubcommandExample.class)})
  public abstract static class UnexpectedSubcommandsRootCommandExample {
    @OptionParameter(shortName = "o", longName = "option")
    public String option;
  }

  @Configurable(discriminator = "alpha", subcommands = {@Subcommand(discriminator = "foo",
      configurable = UnexpectedSubcommandsSubcommand2Example.class)})
  public static class UnexpectedSubcommandsSubcommandExample
      extends UnexpectedSubcommandsRootCommandExample {
    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Configurable(discriminator = "foo")
  public static class UnexpectedSubcommandsSubcommand2Example
      extends UnexpectedSubcommandsSubcommandExample {
    @PositionalParameter(position = 1)
    public String bravo;
  }

  @Test(expected = UnexpectedSubcommandsConfigurationException.class)
  public void unexpectedSubcommandsExample() {
    new CommandBuilder().build(UnexpectedSubcommandsRootCommandExample.class);
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

  @Test(expected = RootCommandNotAbstractConfigurationException.class)
  public void notAbstractDiscriminatorExample() {
    new CommandBuilder().build(NotAbstractCommandExample.class);
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
  public void multipleHelpExample() {
    new CommandBuilder().build(MultipleHelpExample.class);
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
  public void multipleVersionExample() {
    new CommandBuilder().build(MultipleVersionExample.class);
  }
}
