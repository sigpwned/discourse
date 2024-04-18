package com.sigpwned.discourse.validation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.invocation.strategy.DefaultInvocationStrategy;
import com.sigpwned.discourse.validation.util.Validation;
import java.util.List;
import javax.validation.Validator;

public class ValidatingInvocationStrategy implements InvocationStrategy {
  private final InvocationStrategy delegate;
  private final Validator validator;

  public ValidatingInvocationStrategy() {
    this(new DefaultInvocationStrategy());
  }

  public ValidatingInvocationStrategy(InvocationStrategy delegate) {
    this(delegate, Validation.defaultValidator());
  }


  public ValidatingInvocationStrategy(InvocationStrategy delegate, Validator validator) {
    this.delegate = requireNonNull(delegate);
    this.validator = requireNonNull(validator);
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, List<String> args) {
    Invocation<? extends T> invocation = getDelegate().invoke(command, args);
    return new ValidatingInvocation<>(invocation, getValidator());
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }

  private Validator getValidator() {
    return validator;
  }
}
