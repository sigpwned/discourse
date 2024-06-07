package com.sigpwned.discourse.core.format.help.localize.message;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.annotation.DiscourseLocalize;
import com.sigpwned.discourse.core.format.help.MessageLocalizer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class AnnotationBundleMessageLocalizer implements MessageLocalizer {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AnnotationBundleMessageLocalizer.class);

  public static final AnnotationBundleMessageLocalizer INSTANCE =
      new AnnotationBundleMessageLocalizer();

  @Override
  public String localizeMessage(String message, List<Annotation> annotations,
      InvocationContext context) {
    DiscourseLocalize annotation = annotations.stream()
        .mapMulti(Streams.filterAndCast(DiscourseLocalize.class)).findFirst().orElse(null);
    if (annotation == null)
      return message;

    ResourceBundle bundle;
    try {
      bundle = ResourceBundle.getBundle(annotation.value());
    } catch (MissingResourceException e) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Resource bundle not found: " + annotation.value(), e);
      return message;
    }

    if (!bundle.containsKey(message))
      return message;

    return bundle.getString(message);
  }
}
