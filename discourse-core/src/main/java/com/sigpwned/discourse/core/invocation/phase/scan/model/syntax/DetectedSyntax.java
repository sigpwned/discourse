package com.sigpwned.discourse.core.invocation.phase.scan.model.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;

public record DetectedSyntax(Object nominated, Type genericType, List<Annotation> annotations,
    boolean required, boolean help, boolean version, Set<Coordinate> coordinates) {
  public static DetectedSyntax fromCandidateAndDetection(CandidateSyntax candidate,
      SyntaxDetection detection) {
    return new DetectedSyntax(candidate.nominated(), candidate.genericType(),
        candidate.annotations(), detection.required(), detection.help(), detection.version(),
        detection.coordinates());
  }
}
