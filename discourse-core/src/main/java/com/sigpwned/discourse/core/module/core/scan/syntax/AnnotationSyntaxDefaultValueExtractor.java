package com.sigpwned.discourse.core.module.core.scan.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.annotation.DiscourseDefaultValue;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDefaultValueExtractor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Streams;

public class AnnotationSyntaxDefaultValueExtractor implements SyntaxDefaultValueExtractor {
  public static final AnnotationSyntaxDefaultValueExtractor INSTANCE =
      new AnnotationSyntaxDefaultValueExtractor();

  @Override
  public Optional<String> extractDefaultValue(NamedSyntax syntax) {
    DiscourseDefaultValue annotation = syntax.annotations().stream()
        .mapMulti(Streams.filterAndCast(DiscourseDefaultValue.class)).findFirst().orElse(null);

    if (annotation == null)
      return Optional.empty();

    return Optional.of(annotation.value());
  }
}
