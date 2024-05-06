package com.sigpwned.discourse.core.phase.parse;

import static java.util.stream.Collectors.toMap;

import com.sigpwned.discourse.core.phase.parse.model.SwitchType;
import com.sigpwned.discourse.core.phase.parse.model.coordinate.SwitchNameArgumentCoordinate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@FunctionalInterface
public interface SwitchClassifier {

  public static SwitchClassifier fromVocabulary(Set<SwitchNameArgumentCoordinate> flags,
      Set<SwitchNameArgumentCoordinate> options) {
    return fromVocabulary(
        Stream.concat(flags.stream(), options.stream()).collect(toMap(s -> s, s -> {
          if (flags.contains(s)) {
            return SwitchType.FLAG;
          }
          if (options.contains(s)) {
            return SwitchType.OPTION;
          }
          // This should never happen because we're only using switches from those sets
          throw new AssertionError("unrecognized switch %s".formatted(s));
        }, (a, b) -> {
          // TODO better exception?
          throw new IllegalArgumentException("duplicate switch");
        }, HashMap::new)));
  }

  public static SwitchClassifier fromVocabulary(Map<SwitchNameArgumentCoordinate, SwitchType> vocabulary) {
    return s -> Optional.ofNullable(vocabulary.get(s));
  }

  public Optional<SwitchType> classifySwitch(SwitchNameArgumentCoordinate s);
}
