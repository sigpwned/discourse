package com.sigpwned.discourse.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.DiscourseDefaultValue;
import com.sigpwned.discourse.core.annotation.DiscourseDescription;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.dialect.UnixDialect;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriberChain;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatterChain;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.MessageLocalizerChain;
import com.sigpwned.discourse.core.format.help.TextFormatterChain;
import com.sigpwned.discourse.core.format.help.coordinate.FlagParameterCoordinateFormatter;
import com.sigpwned.discourse.core.format.help.coordinate.OptionParameterCoordinateFormatter;
import com.sigpwned.discourse.core.format.help.coordinate.PositionalParameterCoordinateFormatter;
import com.sigpwned.discourse.core.format.help.describe.property.DefaultValueCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.describe.property.DescriptionCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.describe.property.RequiredCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.localize.message.AnnotationBundleMessageLocalizer;
import com.sigpwned.discourse.core.format.help.localize.message.ApplicationBundleMessageLocalizer;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleBoldTextFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleItalicTextFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleStrikethruTextFormatter;
import com.sigpwned.discourse.core.optional.OptionalInvocationContextProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;

public class DefaultHelpFormatterTest {
  @DiscourseDescription("Test @Configurable #1")
  @Configurable(name = "test1")
  public static class TestConfigurable1 {
    @DiscourseDescription("This is a foo.")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;
  }

  @Test
  public void test1() {
    CommandPropertySyntaxFormatterChain syntaxChain = new CommandPropertySyntaxFormatterChain();
    syntaxChain.addLast(FlagParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(OptionParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(PositionalParameterCoordinateFormatter.INSTANCE);

    CommandPropertyDescriberChain describerChain = new CommandPropertyDescriberChain();
    describerChain.addLast(DescriptionCommandPropertyDescriber.INSTANCE);

    MessageLocalizerChain localizer = new MessageLocalizerChain();
    localizer.addLast(AnnotationBundleMessageLocalizer.INSTANCE);
    localizer.addLast(ApplicationBundleMessageLocalizer.INSTANCE);

    TextFormatterChain textFormatter = new TextFormatterChain();
    textFormatter.addLast(ConsoleBoldTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleItalicTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleStrikethruTextFormatter.INSTANCE);

    LeafCommand<TestConfigurable1> leaf = (LeafCommand<TestConfigurable1>) InvocationPipeline
        .builder().dialect(UnixDialect.INSTANCE).build().scan(TestConfigurable1.class).getRoot();

    ResolvedCommand<?> resolved = new ResolvedCommand<>("name", "version", leaf);

    InvocationContext context = mock(InvocationContext.class);
    when(context.get(InvocationPipelineStep.DIALECT_KEY))
        .thenReturn(OptionalInvocationContextProperty.of(InvocationPipelineStep.DIALECT_KEY,
            UnixDialect.INSTANCE));
    when(context.get(InvocationPipelineStep.APPLICATION_BUNDLE_KEY)).thenReturn(
        OptionalInvocationContextProperty.empty(InvocationPipelineStep.APPLICATION_BUNDLE_KEY));

    HelpFormatter help =
        new DefaultHelpFormatter(100, syntaxChain, describerChain, localizer, textFormatter);

    System.out.println(help.formatHelp(UnixDialect.INSTANCE, resolved, context));
  }


  @DiscourseDescription("Test @Configurable #2")
  @Configurable(name = "test2")
  public static class TestConfigurable2 {
    @DiscourseDescription("FOO")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;
  }

  /**
   * Application resource bundle localization test
   */
  @Test
  public void test2() {
    ResourceBundle applicationBundle = new ResourceBundle() {
      final Map<String, String> map = Map.of("FOO", "This is a foo.");

      @Override
      protected Object handleGetObject(String key) {
        return map.get(key);
      }

      @Override
      public Enumeration<String> getKeys() {
        final Iterator<String> iterator = map.keySet().iterator();
        return new Enumeration<String>() {
          @Override
          public boolean hasMoreElements() {
            return iterator.hasNext();
          }

          @Override
          public String nextElement() {
            return iterator.next();
          }
        };
      }
    };

    CommandPropertySyntaxFormatterChain syntaxChain = new CommandPropertySyntaxFormatterChain();
    syntaxChain.addLast(FlagParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(OptionParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(PositionalParameterCoordinateFormatter.INSTANCE);

    CommandPropertyDescriberChain describerChain = new CommandPropertyDescriberChain();
    describerChain.addLast(DescriptionCommandPropertyDescriber.INSTANCE);

    MessageLocalizerChain localizer = new MessageLocalizerChain();
    localizer.addLast(AnnotationBundleMessageLocalizer.INSTANCE);
    localizer.addLast(ApplicationBundleMessageLocalizer.INSTANCE);

    TextFormatterChain textFormatter = new TextFormatterChain();
    textFormatter.addLast(ConsoleBoldTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleItalicTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleStrikethruTextFormatter.INSTANCE);

    LeafCommand<TestConfigurable2> leaf = (LeafCommand<TestConfigurable2>) InvocationPipeline
        .builder().dialect(UnixDialect.INSTANCE).build().scan(TestConfigurable2.class).getRoot();

    ResolvedCommand<?> resolved = new ResolvedCommand<>("name", "version", leaf);

    InvocationContext context = mock(InvocationContext.class);
    when(context.get(InvocationPipelineStep.DIALECT_KEY))
        .thenReturn(OptionalInvocationContextProperty.of(InvocationPipelineStep.DIALECT_KEY,
            UnixDialect.INSTANCE));
    when(context.get(InvocationPipelineStep.APPLICATION_BUNDLE_KEY))
        .thenReturn(OptionalInvocationContextProperty
            .of(InvocationPipelineStep.APPLICATION_BUNDLE_KEY, applicationBundle));

    HelpFormatter help =
        new DefaultHelpFormatter(100, syntaxChain, describerChain, localizer, textFormatter);

    System.out.println(help.formatHelp(UnixDialect.INSTANCE, resolved, context));
  }


  @DiscourseDescription("Test @Configurable #3")
  @Configurable(name = "test3")
  public static class TestConfigurable3 {
    @DiscourseDefaultValue("foobar")
    @DiscourseRequired
    @DiscourseDescription("This is a foo.")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;
  }

  /**
   * Application resource bundle localization test
   */
  @Test
  public void test3() {
    CommandPropertySyntaxFormatterChain syntaxChain = new CommandPropertySyntaxFormatterChain();
    syntaxChain.addLast(FlagParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(OptionParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(PositionalParameterCoordinateFormatter.INSTANCE);

    CommandPropertyDescriberChain describerChain = new CommandPropertyDescriberChain();
    describerChain.addLast(DescriptionCommandPropertyDescriber.INSTANCE);
    describerChain.addLast(RequiredCommandPropertyDescriber.INSTANCE);
    describerChain.addLast(DefaultValueCommandPropertyDescriber.INSTANCE);

    MessageLocalizerChain localizer = new MessageLocalizerChain();
    localizer.addLast(AnnotationBundleMessageLocalizer.INSTANCE);
    localizer.addLast(ApplicationBundleMessageLocalizer.INSTANCE);

    TextFormatterChain textFormatter = new TextFormatterChain();
    textFormatter.addLast(ConsoleBoldTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleItalicTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleStrikethruTextFormatter.INSTANCE);

    LeafCommand<TestConfigurable3> leaf = (LeafCommand<TestConfigurable3>) InvocationPipeline
        .builder().dialect(UnixDialect.INSTANCE).build().scan(TestConfigurable3.class).getRoot();

    ResolvedCommand<?> resolved = new ResolvedCommand<>("name", "version", leaf);

    InvocationContext context = mock(InvocationContext.class);
    when(context.get(InvocationPipelineStep.DIALECT_KEY))
        .thenReturn(OptionalInvocationContextProperty.of(InvocationPipelineStep.DIALECT_KEY,
            UnixDialect.INSTANCE));
    when(context.get(InvocationPipelineStep.APPLICATION_BUNDLE_KEY)).thenReturn(
        OptionalInvocationContextProperty.empty(InvocationPipelineStep.APPLICATION_BUNDLE_KEY));

    HelpFormatter help =
        new DefaultHelpFormatter(100, syntaxChain, describerChain, localizer, textFormatter);

    System.out.println(help.formatHelp(UnixDialect.INSTANCE, resolved, context));
  }
}
