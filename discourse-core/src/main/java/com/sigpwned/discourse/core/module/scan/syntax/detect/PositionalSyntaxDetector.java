package com.sigpwned.discourse.core.module.scan.syntax.detect;

import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.util.Streams;

public class PositionalSyntaxDetector implements SyntaxDetector {
  @Override
  public Optional<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate) {
    PositionalParameter positional = candidate.annotations().stream()
        .mapMulti(Streams.filterAndCast(PositionalParameter.class)).findFirst().orElse(null);
    if (positional == null) {
      // This is fine. Not every candidate is actually syntax.
      return Optional.empty();
    }

    boolean required = positional.required();

    Set<Coordinate> coordinates = Set.of(PositionalCoordinate.of(positional.position()));

    return Optional.of(new SyntaxDetection(required, false, false, coordinates));
  }
}
