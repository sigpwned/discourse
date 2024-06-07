package com.sigpwned.discourse.core.format.help.localize.message;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ResourceBundle;
import com.sigpwned.discourse.core.format.help.MessageLocalizer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;

public class ApplicationBundleMessageLocalizer implements MessageLocalizer {
  public static final ApplicationBundleMessageLocalizer INSTANCE =
      new ApplicationBundleMessageLocalizer();

  @Override
  public String localizeMessage(String message, List<Annotation> annotaitons,
      InvocationContext context) {
    ResourceBundle applicationBundle =
        context.get(InvocationPipelineStep.APPLICATION_BUNDLE_KEY).orElse(null);
    if (applicationBundle == null)
      return message;

    if (!applicationBundle.containsKey(message))
      return message;

    return applicationBundle.getString(message);
  }
}
