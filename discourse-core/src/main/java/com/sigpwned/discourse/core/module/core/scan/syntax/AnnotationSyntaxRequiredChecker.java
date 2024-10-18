package com.sigpwned.discourse.core.module.core.scan.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxRequiredChecker;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Streams;

public class AnnotationSyntaxRequiredChecker implements SyntaxRequiredChecker {
  public static final AnnotationSyntaxRequiredChecker INSTANCE =
      new AnnotationSyntaxRequiredChecker();

  @Override
  public Optional<Boolean> checkSyntaxRequired(NamedSyntax syntax) {
    DiscourseRequired annotation = syntax.annotations().stream()
        .mapMulti(Streams.filterAndCast(DiscourseRequired.class)).findFirst().orElse(null);

    if (annotation == null)
      return Optional.empty();

    return Optional.of(true);
  }
}
