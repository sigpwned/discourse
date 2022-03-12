package com.sigpwned.discourse.validation.command;

import java.util.List;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.validation.ValidatingInvocation;

public class ValidatingSingleCommand<T> extends SingleCommand<T> {
  public ValidatingSingleCommand(ConfigurationClass configurationClass) {
    super(configurationClass);
  }

  @Override
  protected Invocation<T> newInvocation(ConfigurationClass configurationClass, List<String> args) {
    return new ValidatingInvocation<T>(this, configurationClass, args);
  }
}
