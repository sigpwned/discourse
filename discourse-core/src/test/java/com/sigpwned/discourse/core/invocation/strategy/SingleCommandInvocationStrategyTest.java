/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.invocation.strategy;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.TestModule;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.exception.bean.AssignmentFailureBeanException;
import com.sigpwned.discourse.core.invocation.InvocationBuilderParseStep;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import com.sigpwned.discourse.core.optional.OptionalEnvironmentVariable;
import com.sigpwned.discourse.core.optional.OptionalSystemProperty;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SingleCommandInvocationStrategyTest {

  public InvocationContext context;

  @Before
  public void setupSingleCommandInvocationStrategyTest() {
    context = new DefaultInvocationContext();
    context.register(new TestModule());
  }

  @After
  public void cleanupSingleCommandInvocationStrategyTest() {
    context = null;
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class EnvironmentAssignmentFailureExample {

    @EnvironmentParameter(variableName = "HELLO")
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failure");
    }
  }

  @Test(expected = AssignmentFailureBeanException.class)
  public void givenClassWithSimulatedVariableAssignmentFailure_whenInvoke_thenFailWithAssignmentFailureException() {
    final String hello = "hello";

    context.set(InvocationBuilderParseStep.VARIABLE_STORE_KEY, name -> {
      if ("HELLO".equals(name)) {
        return OptionalEnvironmentVariable.of("HELLO", hello);
      }
      return OptionalEnvironmentVariable.getenv(name);
    });

    Invocation.builder().scan(EnvironmentAssignmentFailureExample.class, context)
        .resolve(List.of(), context).parse(context).deserialize(context)
        .prepare(context).build(context);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class PropertyAssignmentFailureExample {

    @PropertyParameter(propertyName = "hello")
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failure");
    }
  }

  @Test(expected = AssignmentFailureBeanException.class)
  public void givenClassWithSimulatedPropertyAssignmentFailure_whenInvoke_thenFailWithAssignmentFailureException() {
    final String hello = "hello";

    context.set(InvocationBuilderParseStep.PROPERTY_STORE_KEY, name -> {
      if ("hello".equals(name)) {
        return OptionalSystemProperty.of("hello", hello);
      }
      return OptionalSystemProperty.getProperty(name);
    });

    Invocation.builder().scan(PropertyAssignmentFailureExample.class, context)
        .resolve(List.of(), context).parse(context).deserialize(context)
        .prepare(context).build(context);
  }
}
