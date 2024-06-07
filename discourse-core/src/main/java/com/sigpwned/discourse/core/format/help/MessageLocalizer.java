package com.sigpwned.discourse.core.format.help;

import java.lang.annotation.Annotation;
import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface MessageLocalizer {
  public String localizeMessage(String message, List<Annotation> annotations, InvocationContext context);
}
