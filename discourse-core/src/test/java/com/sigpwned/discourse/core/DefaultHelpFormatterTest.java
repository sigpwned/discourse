package com.sigpwned.discourse.core;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.DiscourseAttribute;
import com.sigpwned.discourse.core.annotation.DiscourseDefaultValue;
import com.sigpwned.discourse.core.annotation.DiscourseDescription;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.Undocumented;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.dialect.UnixDialect;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriberChain;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatterChain;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.MessageLocalizerChain;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.SynopsisEditorChain;
import com.sigpwned.discourse.core.format.help.SynopsisFormatter;
import com.sigpwned.discourse.core.format.help.TextFormatterChain;
import com.sigpwned.discourse.core.format.help.coordinate.FlagParameterCoordinateFormatter;
import com.sigpwned.discourse.core.format.help.coordinate.OptionParameterCoordinateFormatter;
import com.sigpwned.discourse.core.format.help.coordinate.PositionalParameterCoordinateFormatter;
import com.sigpwned.discourse.core.format.help.describe.property.DefaultValueCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.describe.property.DescriptionCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.describe.property.ExampleCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.describe.property.RequiredCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.describe.property.UndocumentedCommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.localize.message.AnnotationBundleMessageLocalizer;
import com.sigpwned.discourse.core.format.help.localize.message.ApplicationBundleMessageLocalizer;
import com.sigpwned.discourse.core.format.help.synopsis.editor.CommandNameAndDiscriminatorsSynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.editor.OptionsPlaceholderSynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.editor.PositionalArgumentsSynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.format.DefaultSynopsisFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleBoldTextFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleItalicTextFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleStrikethruTextFormatter;
import com.sigpwned.discourse.core.module.CoreModule;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.BooleanValueDeserializerFactory;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.StringValueDeserializerFactory;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.module.core.plan.value.sink.AssignValueSinkFactory;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ListAddValueSinkFactory;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.module.parameter.environmentvariable.EnvironmentVariableCoordinateFormatter;
import com.sigpwned.discourse.core.module.parameter.environmentvariable.EnvironmentVariableParameter;
import com.sigpwned.discourse.core.module.parameter.systemproperty.SystemPropertyCoordinateFormatter;
import com.sigpwned.discourse.core.module.parameter.systemproperty.SystemPropertyParameter;
import com.sigpwned.discourse.core.optional.OptionalInvocationContextProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

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

  @DiscourseDescription("Test @Configurable #4")
  @Configurable(name = "test4")
  public static class TestConfigurable4 {
    @DiscourseDefaultValue("foobar")
    @DiscourseRequired
    @DiscourseDescription("This is a foo.")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;

    @DiscourseRequired
    @DiscourseDescription("This is a bar.")
    @PositionalParameter(position = 0)
    public String bar;

    @DiscourseDescription("This is a quux.")
    @PositionalParameter(position = 1)
    public String quux;
  }

  /**
   * Application resource bundle localization test
   */
  @Test
  public void test4() {
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

    LeafCommand<TestConfigurable4> leaf = (LeafCommand<TestConfigurable4>) InvocationPipeline
        .builder().dialect(UnixDialect.INSTANCE).build().scan(TestConfigurable4.class).getRoot();

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

  @DiscourseDescription("Test @Configurable #5")
  @Configurable(name = "test5")
  public static class TestConfigurable5 {
    @DiscourseDefaultValue("foobar")
    @DiscourseRequired
    @DiscourseDescription("This is a foo.")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;

    @DiscourseRequired
    @DiscourseDescription("This is a bar.")
    @PositionalParameter(position = 0)
    public String bar;

    @DiscourseAttribute("quux")
    @DiscourseDescription("This is a quux.")
    @PositionalParameter(position = 1)
    public List<String> quuxes;
  }

  /**
   * Application resource bundle localization test
   */
  @Test
  public void test5() {
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

    ValueSinkFactoryChain sinkFactory = new ValueSinkFactoryChain();
    sinkFactory.addLast(ListAddValueSinkFactory.INSTANCE);
    sinkFactory.addLast(AssignValueSinkFactory.INSTANCE);

    LeafCommand<TestConfigurable5> leaf = (LeafCommand<TestConfigurable5>) InvocationPipeline
        .builder().dialect(UnixDialect.INSTANCE).build().scan(TestConfigurable5.class).getRoot();

    ResolvedCommand<?> resolved = new ResolvedCommand<>("name", "version", leaf);

    InvocationContext context = mock(InvocationContext.class);
    when(context.get(InvocationPipelineStep.DIALECT_KEY))
        .thenReturn(OptionalInvocationContextProperty.of(InvocationPipelineStep.DIALECT_KEY,
            UnixDialect.INSTANCE));
    when(context.get(InvocationPipelineStep.APPLICATION_BUNDLE_KEY)).thenReturn(
        OptionalInvocationContextProperty.empty(InvocationPipelineStep.APPLICATION_BUNDLE_KEY));
    when(context.get(PlanStep.VALUE_SINK_FACTORY_KEY)).thenReturn(
        OptionalInvocationContextProperty.of(PlanStep.VALUE_SINK_FACTORY_KEY, sinkFactory));

    HelpFormatter help =
        new DefaultHelpFormatter(100, syntaxChain, describerChain, localizer, textFormatter);

    System.out.println(help.formatHelp(UnixDialect.INSTANCE, resolved, context));
  }

  @DiscourseDescription("Test @Configurable #6")
  @Configurable(name = "test6")
  public static class TestConfigurable6 {
    @DiscourseDefaultValue("foobar")
    @DiscourseRequired
    @DiscourseDescription("This is a foo.")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;

    @DiscourseRequired
    @DiscourseDescription("This is a bar.")
    @PositionalParameter(position = 0)
    public String bar;

    @DiscourseAttribute("quux")
    @DiscourseDescription("This is a quux.")
    @PositionalParameter(position = 1)
    public List<String> quuxes;

    @DiscourseDescription("This is a baz.")
    @FlagParameter(shortName = "b", longName = "baz")
    public boolean baz;

    @DiscourseDescription("This is a variable.")
    @EnvironmentVariableParameter(variable = "VARIABLE")
    public String variable;

    @Undocumented
    @DiscourseDescription("This is a property.")
    @SystemPropertyParameter(property = "PROPERTY")
    public String property;
  }

  /**
   * Application resource bundle localization test
   */
  @Test
  public void test6() {
    CommandPropertySyntaxFormatterChain syntaxChain = new CommandPropertySyntaxFormatterChain();
    syntaxChain.addLast(FlagParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(OptionParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(PositionalParameterCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(EnvironmentVariableCoordinateFormatter.INSTANCE);
    syntaxChain.addLast(SystemPropertyCoordinateFormatter.INSTANCE);

    CommandPropertyDescriberChain describerChain = new CommandPropertyDescriberChain();
    describerChain.addFirst(UndocumentedCommandPropertyDescriber.INSTANCE);
    describerChain.addLast(DescriptionCommandPropertyDescriber.INSTANCE);
    describerChain.addLast(RequiredCommandPropertyDescriber.INSTANCE);
    describerChain.addLast(ExampleCommandPropertyDescriber.INSTANCE);
    describerChain.addLast(DefaultValueCommandPropertyDescriber.INSTANCE);

    MessageLocalizerChain localizer = new MessageLocalizerChain();
    localizer.addLast(AnnotationBundleMessageLocalizer.INSTANCE);
    localizer.addLast(ApplicationBundleMessageLocalizer.INSTANCE);

    TextFormatterChain textFormatter = new TextFormatterChain();
    textFormatter.addLast(ConsoleBoldTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleItalicTextFormatter.INSTANCE);
    textFormatter.addLast(ConsoleStrikethruTextFormatter.INSTANCE);

    ValueSinkFactoryChain sinkFactory = new ValueSinkFactoryChain();
    sinkFactory.addLast(ListAddValueSinkFactory.INSTANCE);
    sinkFactory.addLast(AssignValueSinkFactory.INSTANCE);

    ValueDeserializerFactoryChain deserializerFactory = new ValueDeserializerFactoryChain();
    deserializerFactory.addLast(StringValueDeserializerFactory.INSTANCE);
    deserializerFactory.addLast(BooleanValueDeserializerFactory.INSTANCE);

    SynopsisEditorChain synopsisFactory = new SynopsisEditorChain();
    synopsisFactory.addLast(new CommandNameAndDiscriminatorsSynopsisEditor());
    synopsisFactory.addLast(new OptionsPlaceholderSynopsisEditor());
    synopsisFactory.addLast(new PositionalArgumentsSynopsisEditor());

    SynopsisFormatter synopsisFormatter = new DefaultSynopsisFormatter();

    CommandResolution<? extends TestConfigurable6> resolution = InvocationPipeline.builder()
        .register(new CoreModule()).build().resolve(TestConfigurable6.class, emptyList());

    ResolvedCommand<? extends TestConfigurable6> resolved = resolution.getCommand();

    InvocationContext context = mock(InvocationContext.class);
    when(context.get(SynopsisEditor.class)).thenReturn(OptionalInvocationContextProperty
        .of(InvocationContext.Key.of(SynopsisEditor.class), synopsisFactory));
    when(context.get(SynopsisFormatter.class)).thenReturn(OptionalInvocationContextProperty
        .of(InvocationContext.Key.of(SynopsisFormatter.class), synopsisFormatter));
    when(context.get(InvocationPipelineStep.DIALECT_KEY))
        .thenReturn(OptionalInvocationContextProperty.of(InvocationPipelineStep.DIALECT_KEY,
            UnixDialect.INSTANCE));
    when(context.get(InvocationPipelineStep.APPLICATION_BUNDLE_KEY)).thenReturn(
        OptionalInvocationContextProperty.empty(InvocationPipelineStep.APPLICATION_BUNDLE_KEY));
    when(context.get(PlanStep.VALUE_SINK_FACTORY_KEY)).thenReturn(
        OptionalInvocationContextProperty.of(PlanStep.VALUE_SINK_FACTORY_KEY, sinkFactory));
    when(context.get(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY))
        .thenReturn(OptionalInvocationContextProperty.of(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY,
            deserializerFactory));

    HelpFormatter help =
        new DefaultHelpFormatter(100, syntaxChain, describerChain, localizer, textFormatter);

    System.out.println(help.formatHelp(UnixDialect.INSTANCE, resolved, context));
  }
}
