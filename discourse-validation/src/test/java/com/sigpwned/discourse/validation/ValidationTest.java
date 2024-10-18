/*-
 * =================================LICENSE_START==================================
 * discourse-validation
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.invocation.InvocationPipeline;
import com.sigpwned.discourse.validation.exception.argument.ValidationArgumentException;

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
    InvocationPipeline.builder().register(new ValidationModule()).build()
        .execute(Example.class, List.of("5")).getInstance();
  }

  @Test
  public void badTest() {
    ValidationArgumentException problem;

    try {
      InvocationPipeline.builder().register(new ValidationModule()).build()
          .execute(Example.class, List.of("15")).getInstance();
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
