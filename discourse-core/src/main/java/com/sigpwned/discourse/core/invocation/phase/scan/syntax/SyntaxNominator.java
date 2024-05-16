package com.sigpwned.discourse.core.invocation.phase.scan.syntax;

import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;

public interface SyntaxNominator {

  public List<CandidateSyntax> nominateSyntax(Class<?> clazz);
}
