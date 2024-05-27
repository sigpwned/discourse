package com.sigpwned.discourse.core.pipeline.invocation.step.parse.exception;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.pipeline.invocation.step.parse.ParseException;

@SuppressWarnings("serial")
public class MissingOptionValueParseException extends ParseException {
  // TODO should this be a string, like --foo, instead of just foo?
  private final SwitchName switchName;

  public MissingOptionValueParseException(SwitchName switchName) {
    super("Option " + switchName + " requires value, but none was provided");
    this.switchName = requireNonNull(switchName);
  }

  public SwitchName getSwitchName() {
    return switchName;
  }
}
