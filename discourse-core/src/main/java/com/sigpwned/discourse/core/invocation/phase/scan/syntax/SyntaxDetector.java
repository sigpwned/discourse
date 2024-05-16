package com.sigpwned.discourse.core.invocation.phase.scan.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.DetectedSyntax;

public interface SyntaxDetector {

  public Optional<DetectedSyntax> detectSyntax(Class<?> clazz, CandidateSyntax candidate);
}
