package com.sigpwned.discourse.core.configurable3.syntax;

import java.util.Optional;

public interface SyntaxDetector {

  public Optional<DetectedSyntax> detectSyntax(Class<?> clazz, CandidateSyntax candidate);
}
