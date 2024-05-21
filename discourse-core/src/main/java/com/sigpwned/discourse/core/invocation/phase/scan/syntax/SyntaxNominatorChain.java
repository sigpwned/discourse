package com.sigpwned.discourse.core.invocation.phase.scan.syntax;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import java.util.ArrayList;
import java.util.List;

public class SyntaxNominatorChain extends Chain<SyntaxNominator> implements SyntaxNominator {

  @Override
  public List<CandidateSyntax> nominateSyntax(Class<?> clazz, InvocationContext context) {
    List<CandidateSyntax> result = new ArrayList<>();
    for (SyntaxNominator nominator : this) {
      result.addAll(nominator.nominateSyntax(clazz, context));
    }
    return result;
  }
}
