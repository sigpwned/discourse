package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;

public class SyntaxExampleValueExtractorChain extends Chain<SyntaxExampleValueExtractor>
    implements SyntaxExampleValueExtractor {
  @Override
  public Optional<String> extractExampleValue(NamedSyntax syntax) {
    for (SyntaxExampleValueExtractor extractor : this) {
      Optional<String> maybeExampleValue = extractor.extractExampleValue(syntax);
      if (maybeExampleValue.isPresent())
        return maybeExampleValue;
    }
    return Optional.empty();
  }
}
