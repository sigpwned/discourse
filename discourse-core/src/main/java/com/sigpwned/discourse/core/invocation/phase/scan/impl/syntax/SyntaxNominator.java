package com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax;

import java.util.List;

public interface SyntaxNominator {

  public List<CandidateSyntax> nominateSyntax(Class<?> clazz);
}
