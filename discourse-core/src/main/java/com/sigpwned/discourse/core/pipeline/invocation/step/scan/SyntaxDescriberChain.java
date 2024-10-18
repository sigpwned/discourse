package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;

public class SyntaxDescriberChain extends Chain<SyntaxDescriber> implements SyntaxDescriber {
  @Override
  public Optional<String> describeSyntax(NamedSyntax syntax) {
    for (SyntaxDescriber describer : this) {
      Optional<String> maybeDescription = describer.describeSyntax(syntax);
      if (maybeDescription.isPresent())
        return maybeDescription;
    }
    return Optional.empty();
  }
}
