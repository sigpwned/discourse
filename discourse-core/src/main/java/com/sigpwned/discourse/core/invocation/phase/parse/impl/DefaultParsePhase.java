package com.sigpwned.discourse.core.invocation.phase.parse.impl;

import static com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase.FLAG_TYPE;
import static com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase.OPTION_TYPE;
import static java.util.stream.Collectors.toUnmodifiableSet;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ArgumentsParser.Handler;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.SwitchClassifier;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.model.coordinate.PositionArgumentCoordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.model.coordinate.SwitchNameArgumentCoordinate;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DefaultParsePhase implements ParsePhase {


  @Override
  public <T> List<Entry<String, String>> parse(Command<T> command, List<String> args) {
    List<Map.Entry<Object, String>> parsedArgs = parseStep(command, args);

    List<Map.Entry<String, String>> attributedArgs = attributeStep(command, parsedArgs);

    return attributedArgs;

  }

  protected <T> List<Map.Entry<Object, String>> parseStep(Command<T> command, List<String> args) {
    Map<String, String> vocabulary = command.getVocabulary();

    // TODO is FLAG_TYPE in the right place?
    // TODO is OPTION_TYPE in the right place?
    Set<SwitchNameArgumentCoordinate> flags = vocabulary.entrySet().stream()
        .filter(e -> e.getValue().equals(FLAG_TYPE))
        .map(e -> SwitchNameArgumentCoordinate.fromString(e.getKey())).collect(toUnmodifiableSet());
    Set<SwitchNameArgumentCoordinate> options = vocabulary.entrySet().stream()
        .filter(e -> e.getValue().equals(OPTION_TYPE))
        .map(e -> SwitchNameArgumentCoordinate.fromString(e.getKey())).collect(toUnmodifiableSet());

    List<Map.Entry<Object, String>> result = new ArrayList<>();
    ArgumentsParser parser = newArgumentsParser(SwitchClassifier.fromVocabulary(flags, options));
    parser.parse(args, new Handler() {
      @Override
      public void flag(SwitchNameArgumentCoordinate s) {
        result.add(new SimpleEntry<>(s.toString(), "true"));
      }

      @Override
      public void option(SwitchNameArgumentCoordinate s, String value) {
        result.add(new SimpleEntry<>(s.toString(), value));
      }

      @Override
      public void positional(PositionArgumentCoordinate position, String value) {
        result.add(new SimpleEntry<>(position.getIndex(), value));
      }
    });

    return result;
  }

  /**
   * test hook
   */
  protected ArgumentsParser newArgumentsParser(SwitchClassifier classifier) {
    return new ArgumentsParser(classifier);
  }

  protected <T> List<Map.Entry<String, String>> attributeStep(Command<T> command,
      List<Map.Entry<Object, String>> parsedArgs) {
    List<Map.Entry<String, String>> attributedArgs = new ArrayList<>(parsedArgs.size());

    for (Map.Entry<Object, String> parsedArg : parsedArgs) {
      String key = parsedArg.getKey().toString();
      String value = parsedArg.getValue();

      attributedArgs.add(new SimpleEntry<>(key, value));
    }

    return attributedArgs;
  }
}
