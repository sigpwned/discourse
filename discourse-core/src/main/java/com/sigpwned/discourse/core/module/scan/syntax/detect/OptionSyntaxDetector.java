package com.sigpwned.discourse.core.module.scan.syntax.detect;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.util.Streams;

public class OptionSyntaxDetector implements SyntaxDetector {
  @Override
  public Optional<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate) {
    OptionParameter option = candidate.annotations().stream()
        .mapMulti(Streams.filterAndCast(OptionParameter.class)).findFirst().orElse(null);
    if (option == null) {
      // This is fine. Not every candidate is actually syntax.
      return Optional.empty();
    }

    boolean required = option.required();

    Set<Coordinate> coordinates = new HashSet<>(2);
    if (!option.longName().equals("")) {
      coordinates.add(new OptionCoordinate(SwitchName.fromString(option.longName())));
    }
    if (!option.shortName().equals("")) {
      coordinates.add(new OptionCoordinate(SwitchName.fromString(option.shortName())));
    }

    if (coordinates.isEmpty()) {
      // TODO better exception
      throw new IllegalArgumentException("option must have a long or short name");
    }

    return Optional.of(new SyntaxDetection(required, false, false, coordinates));
  }
}
