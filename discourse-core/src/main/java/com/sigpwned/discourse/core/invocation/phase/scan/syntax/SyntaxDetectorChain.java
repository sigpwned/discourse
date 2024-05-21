package com.sigpwned.discourse.core.invocation.phase.scan.syntax;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;

public class SyntaxDetectorChain extends Chain<SyntaxDetector> implements SyntaxDetector {

  @Override
  public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
      InvocationContext context) {
    for (SyntaxDetector detector : this) {
      Maybe<SyntaxDetection> maybeSyntax = detector.detectSyntax(clazz, candidate, context);
      if (maybeSyntax.isDecided()) {
        return maybeSyntax;
      }
    }
    return Maybe.maybe();
  }
}
