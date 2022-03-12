package com.sigpwned.discourse.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.validation.exception.argument.ValidationArgumentException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ValidationTest {
  @Configurable
  public static class Example {
    @Min(1)
    @Max(10)
    @PositionalParameter(position = 0)
    public int example;
  }

  @Test
  public void goodTest() {
    new ValidatingCommandBuilder().build(Example.class).args("5").configuration();
  }

  @Test
  public void badTest() {
    ValidationArgumentException problem;

    try {
      new ValidatingCommandBuilder().build(Example.class).args("15").configuration();
      throw new AssertionError("no exception");
    } catch (ValidationArgumentException e) {
      problem = e;
    }
    
    assertThat(problem.getViolations().size(), is(1));

    ConstraintViolation<?> violation = problem.getViolations().iterator().next();
    
    assertThat(violation.getInvalidValue(), is(15));
    assertThat(violation.getPropertyPath().toString(), is("example"));
    assertThat(violation.getMessage(), is("must be less than or equal to 10"));
  }
}
