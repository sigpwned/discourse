package com.sigpwned.discourse.core.configurable2.syntax;

import java.util.Optional;

public interface ConfigurableSyntaxFactory {

  public Optional<ConfigurableSyntax> createSyntax(ConfigurableSyntaxCandidate candidate);
}
