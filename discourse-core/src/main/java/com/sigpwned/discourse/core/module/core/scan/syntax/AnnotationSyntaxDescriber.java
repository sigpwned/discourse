package com.sigpwned.discourse.core.module.core.scan.syntax;

import java.util.Optional;
import com.sigpwned.discourse.core.annotation.DiscourseDescription;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDescriber;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Streams;

public class AnnotationSyntaxDescriber implements SyntaxDescriber {
  public static final AnnotationSyntaxDescriber INSTANCE = new AnnotationSyntaxDescriber();

  @Override
  public Optional<String> describeSyntax(NamedSyntax syntax) {
    DiscourseDescription annotation = syntax.annotations().stream()
        .mapMulti(Streams.filterAndCast(DiscourseDescription.class)).findFirst().orElse(null);

    if (annotation == null)
      return Optional.empty();

    return Optional.of(annotation.value());
  }
}
