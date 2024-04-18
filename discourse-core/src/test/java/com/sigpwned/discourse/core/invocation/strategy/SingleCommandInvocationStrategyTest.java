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

import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class SingleCommandInvocationStrategyTest {

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

  @Test(expected = AssignmentFailureArgumentException.class)
  public void environmentAssignmentFailureExample() {
    final String hello = "hello";

    SingleCommandInvocationStrategy invoker = new SingleCommandInvocationStrategy();
    invoker.setVariables(
        name -> Optional.ofNullable(name.equals("HELLO") ? hello : System.getenv(name)));

    invoker.invoke(new CommandBuilder().build(EnvironmentAssignmentFailureExample.class), List.of())
        .getConfiguration();
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

  @Test(expected = AssignmentFailureArgumentException.class)
  public void propertyAssignmentFailureExample() {
    final String hello = "hello";

    SingleCommandInvocationStrategy invoker = new SingleCommandInvocationStrategy();
    invoker.setProperties(
        name -> Optional.ofNullable(name.equals("hello") ? hello : System.getProperty(name)));

    invoker.invoke(new CommandBuilder().build(PropertyAssignmentFailureExample.class), List.of())
        .getConfiguration();
  }
}
