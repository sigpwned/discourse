package com.sigpwned.discourse.core.invocation.phase.scan.syntax;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;

public interface SyntaxDetector {

  public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
      InvocationContext context);
}
