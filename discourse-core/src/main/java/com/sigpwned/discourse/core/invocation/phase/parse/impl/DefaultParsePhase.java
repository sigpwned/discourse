package com.sigpwned.discourse.core.invocation.phase.parse.impl;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.args.ArgumentsParser;
import com.sigpwned.discourse.core.args.impl.UnixStyleArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandBody;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

public class DefaultParsePhase implements ParsePhase {

  private final Supplier<ArgumentsParser> argumentsParserFactory;

  public DefaultParsePhase() {
    this(UnixStyleArgumentsParser::new);
  }

  public DefaultParsePhase(Supplier<ArgumentsParser> argumentsParserFactory) {
    this.argumentsParserFactory = requireNonNull(argumentsParserFactory);
  }

  @Override
  public <T> List<Entry<String, String>> parse(Command<T> command, List<String> args) {
    CommandBody<T> body = command.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });

    List<Map.Entry<String, String>> parsedArgs = parseStep(body.getSyntax(), args);

    List<Map.Entry<String, String>> attributedArgs = attributeStep(body.getNames(), parsedArgs);

    return unmodifiableList(attributedArgs);

  }

  protected <T> List<Map.Entry<String, String>> parseStep(Map<String, String> vocabulary,
      List<String> args) {
    ArgumentsParser parser = getArgumentsParserFactory().get();
    return parser.parse(vocabulary, args);
  }

  protected <T> List<Map.Entry<String, String>> attributeStep(Map<String, String> names,
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
}
