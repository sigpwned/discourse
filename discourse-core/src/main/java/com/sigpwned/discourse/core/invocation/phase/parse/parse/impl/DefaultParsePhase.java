package com.sigpwned.discourse.core.invocation.phase.parse.parse.impl;

import static java.util.stream.Collectors.toUnmodifiableSet;

import com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ArgumentsParser.Handler;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.SwitchClassifier;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.model.coordinate.PositionArgumentCoordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.model.coordinate.SwitchNameArgumentCoordinate;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Default implementation of {@link ParsePhase}. Recognizes the following syntax:
 * </p>
 *
 * <ul>
 *   <li>Short switches: {@code -a}, {@code -b}, ...</li>
 *   <li>Long switches: {@code --alpha}, {@code --bravo-charlie}, {@code --delta.echo}, {@code --foxtrot_golf}, ...</li>
 *   <li>Flags: boolean-valued arguments whose value is determined by the presence or absence of a switch</li>
 *   <li>Options: arguments whose value is given explicitly as an argument to a switch</li>
 *   <li>Long switch with attached values: {@code --alpha=bravo}</li>
 *   <li>Short switch batches: {@code -abcd value} ({@code -a -b -c -d value})</li>
 *   <li>Positionals: {@code value}</li>
 * </ul>
 */
public class DefaultParsePhase implements ParsePhase {

  @Override
  public List<Map.Entry<Object, String>> parse(Map<String, String> vocabulary, List<String> args) {
    Set<SwitchNameArgumentCoordinate> flags = vocabulary.entrySet().stream()
        .filter(e -> e.getValue().equals(FLAG_TYPE))
        .map(e -> SwitchNameArgumentCoordinate.fromString(e.getKey())).collect(toUnmodifiableSet());
    Set<SwitchNameArgumentCoordinate> options = vocabulary.entrySet().stream()
        .filter(e -> e.getValue().equals(OPTION_TYPE))
        .map(e -> SwitchNameArgumentCoordinate.fromString(e.getKey())).collect(toUnmodifiableSet());

    List<Map.Entry<Object, String>> result = new ArrayList<>();
    ArgumentsParser parser = new ArgumentsParser(SwitchClassifier.fromVocabulary(flags, options));
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
}
