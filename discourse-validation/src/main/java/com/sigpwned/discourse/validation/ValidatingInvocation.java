package com.sigpwned.discourse.validation;

import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.validation.exception.argument.ValidationArgumentException;
import com.sigpwned.discourse.validation.util.MoreValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class ValidatingInvocation<T> extends Invocation<T> {
  private final Validator validator;

  public ValidatingInvocation(Command<T> command, ConfigurationClass configurationClass,
      List<String> args) {
    this(command, configurationClass, args,
        MoreValidation.DEFAULT_VALIDATOR_FACTORY.getValidator());
  }

  public ValidatingInvocation(Command<T> command, ConfigurationClass configurationClass,
      List<String> args, Validator validator) {
    super(command, configurationClass, args);
    this.validator = validator;
  }

  @Override
  public T configuration() {
    T result = super.configuration();
    @SuppressWarnings({"unchecked", "rawtypes"})
    Set<ConstraintViolation<?>> violations = (Set) getValidator().validate(result);
    if (!violations.isEmpty())
      throw new ValidationArgumentException(violations);
    return result;
  }

  /**
   * @return the validator
   */
  private Validator getValidator() {
    return validator;
  }
}
