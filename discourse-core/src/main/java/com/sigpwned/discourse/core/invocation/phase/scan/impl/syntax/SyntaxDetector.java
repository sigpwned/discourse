package com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax;

import java.util.Optional;

public interface SyntaxDetector {

  public Optional<DetectedSyntax> detectSyntax(Class<?> clazz, CandidateSyntax candidate);
}
