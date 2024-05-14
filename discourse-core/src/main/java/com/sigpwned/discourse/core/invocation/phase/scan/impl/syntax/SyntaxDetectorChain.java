package com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax;

import com.sigpwned.discourse.core.Chain;
import java.util.Optional;

public class SyntaxDetectorChain extends Chain<SyntaxDetector> implements SyntaxDetector {

  @Override
  public Optional<DetectedSyntax> detectSyntax(Class<?> clazz, CandidateSyntax candidate) {
    for (SyntaxDetector detector : this) {
      Optional<DetectedSyntax> syntax = detector.detectSyntax(clazz, candidate);
      if (syntax.isPresent()) {
        return syntax;
      }
    }
    return Optional.empty();
  }
}
