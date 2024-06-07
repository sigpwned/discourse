package com.sigpwned.discourse.core.format.help;

import java.lang.annotation.Annotation;
import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class MessageLocalizerChain extends Chain<MessageLocalizer> implements MessageLocalizer {
  @Override
  public String localizeMessage(String message, List<Annotation> annotations, InvocationContext context) {
    String result = message;
    for (MessageLocalizer localizer : this)
      result = localizer.localizeMessage(result, annotations, context);
    return result;
  }
}
