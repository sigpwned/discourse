package com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.DetectedSyntax;

public interface SyntaxDetector {

  public Optional<DetectedSyntax> detectSyntax(Class<?> clazz, CandidateSyntax candidate);
}
