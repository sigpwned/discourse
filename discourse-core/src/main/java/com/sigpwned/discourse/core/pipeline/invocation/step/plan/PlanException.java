package com.sigpwned.discourse.core.pipeline.invocation.step.plan;

import com.sigpwned.discourse.core.exception.DiscourseException;

@SuppressWarnings("serial")
public abstract class PlanException extends DiscourseException {

  protected PlanException(String message) {
    super(message);
  }

  protected PlanException(String message, Throwable cause) {
    super(message, cause);
  }
}
