package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;

public class SyntaxDefaultValueExtractorChain extends Chain<SyntaxDefaultValueExtractor>
    implements SyntaxDefaultValueExtractor {
  @Override
  public Optional<String> extractDefaultValue(NamedSyntax syntax) {
    for (SyntaxDefaultValueExtractor extractor : this) {
      Optional<String> maybeDefaultValue = extractor.extractDefaultValue(syntax);
      if (maybeDefaultValue.isPresent())
        return maybeDefaultValue;
    }
    return Optional.empty();
  }
}
