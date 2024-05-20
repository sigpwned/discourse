package com.sigpwned.discourse.core.invocation.phase.scan.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;

public interface SyntaxDetector {

  public Optional<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate);
}
