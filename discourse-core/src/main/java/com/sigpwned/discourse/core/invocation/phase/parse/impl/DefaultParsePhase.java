package com.sigpwned.discourse.core.invocation.phase.parse.impl;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import com.sigpwned.discourse.core.args.ArgumentsParser;
import com.sigpwned.discourse.core.args.impl.UnixStyleArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;

public class DefaultParsePhase implements ParsePhase {
  private final Supplier<ArgumentsParser> argumentsParserFactory;
  private final DefaultParsePhaseListener listener;

  public DefaultParsePhase(DefaultParsePhaseListener listener) {
    this(UnixStyleArgumentsParser::new, listener);
  }

  public DefaultParsePhase(Supplier<ArgumentsParser> argumentsParserFactory,
      DefaultParsePhaseListener listener) {
    this.argumentsParserFactory = requireNonNull(argumentsParserFactory);
    this.listener = requireNonNull(listener);
  }

  @Override
  public final List<Entry<String, String>> parse(Map<String, String> vocabulary,
      Map<String, String> naming, List<String> args) {
    List<Map.Entry<String, String>> parsedArgs = doParseStep(vocabulary, args);

    List<Map.Entry<String, String>> attributedArgs = doAttributeStep(naming, parsedArgs);

    return unmodifiableList(attributedArgs);
  }

  private List<Map.Entry<String, String>> doParseStep(Map<String, String> vocabulary,
      List<String> args) {
    List<Map.Entry<String, String>> parsedArgs;
    try {
      getListener().beforeParsePhaseParseStep(vocabulary, args);
      parsedArgs = parseStep(vocabulary, args);
      getListener().afterParsePhaseParseStep(vocabulary, args, parsedArgs);
    } catch (Throwable problem) {
      getListener().catchParsePhaseParseStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhaseParseStep();
    }
    return parsedArgs;
  }

  protected List<Map.Entry<String, String>> parseStep(Map<String, String> vocabulary,
      List<String> args) {
    ArgumentsParser parser = getArgumentsParserFactory().get();
    return parser.parse(vocabulary, args);
  }

  private List<Map.Entry<String, String>> doAttributeStep(Map<String, String> names,
      List<Map.Entry<String, String>> parsedArgs) {
    List<Map.Entry<String, String>> attributedArgs;
    try {
      getListener().beforeParsePhaseAttributeStep(names, parsedArgs);
      attributedArgs = attributeStep(names, parsedArgs);
      getListener().afterParsePhaseAttributeStep(names, parsedArgs, attributedArgs);
    } catch (Throwable problem) {
      getListener().catchParsePhaseAttributeStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhaseAttributeStep();
    }
    return attributedArgs;
  }

  protected List<Map.Entry<String, String>> attributeStep(Map<String, String> names,
      List<Map.Entry<String, String>> parsedArgs) {
    List<Map.Entry<String, String>> attributedArgs = new ArrayList<>(parsedArgs.size());

    for (Map.Entry<String, String> parsedArg : parsedArgs) {
      String coordinate = parsedArg.getKey();
      String name = Optional.ofNullable(names.get(coordinate)).orElseThrow(() -> {
        // TODO better exception
        return new IllegalArgumentException("Unknown coordinate: " + coordinate);
      });
      String value = parsedArg.getValue();

      attributedArgs.add(Map.entry(name, value));
    }

    return unmodifiableList(attributedArgs);
  }

  private Supplier<ArgumentsParser> getArgumentsParserFactory() {
    return argumentsParserFactory;
  }

  private DefaultParsePhaseListener getListener() {
    return listener;
  }
}
