package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;

public interface SyntaxExampleValueExtractor {
  public Optional<String> extractExampleValue(NamedSyntax syntax);
}
