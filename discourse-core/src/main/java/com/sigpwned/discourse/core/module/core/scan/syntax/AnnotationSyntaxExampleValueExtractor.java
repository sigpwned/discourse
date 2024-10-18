package com.sigpwned.discourse.core.module.core.scan.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.annotation.DiscourseExampleValue;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxExampleValueExtractor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Streams;

public class AnnotationSyntaxExampleValueExtractor implements SyntaxExampleValueExtractor {
  public static final AnnotationSyntaxExampleValueExtractor INSTANCE =
      new AnnotationSyntaxExampleValueExtractor();

  @Override
  public Optional<String> extractExampleValue(NamedSyntax syntax) {
    DiscourseExampleValue annotation = syntax.annotations().stream()
        .mapMulti(Streams.filterAndCast(DiscourseExampleValue.class)).findFirst().orElse(null);

    if (annotation == null)
      return Optional.empty();

    return Optional.of(annotation.value());
  }
}
