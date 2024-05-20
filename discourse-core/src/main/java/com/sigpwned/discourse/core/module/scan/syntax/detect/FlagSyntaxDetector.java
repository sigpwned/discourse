package com.sigpwned.discourse.core.module.scan.syntax.detect;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.util.Streams;

public class FlagSyntaxDetector implements SyntaxDetector {
  @Override
  public Optional<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate) {
    FlagParameter flag = candidate.annotations().stream()
        .mapMulti(Streams.filterAndCast(FlagParameter.class)).findFirst().orElse(null);
    if (flag == null) {
      // This is fine. Not every candidate is actually syntax.
      return Optional.empty();
    }

    Set<Coordinate> coordinates = new HashSet<>(2);
    if (!flag.longName().equals("")) {
      coordinates.add(new OptionCoordinate(SwitchName.fromString(flag.longName())));
    }
    if (!flag.shortName().equals("")) {
      coordinates.add(new OptionCoordinate(SwitchName.fromString(flag.shortName())));
    }

    if (coordinates.isEmpty()) {
      // TODO better exception
      throw new IllegalArgumentException("flag must have a long or short name");
    }

    return Optional.of(new SyntaxDetection(false, flag.help(), flag.version(), coordinates));
  }
}
