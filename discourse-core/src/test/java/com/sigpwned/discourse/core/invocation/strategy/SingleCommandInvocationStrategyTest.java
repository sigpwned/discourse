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

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.exception.bean.AssignmentFailureBeanException;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import com.sigpwned.discourse.core.optional.OptionalEnvironmentVariable;
import com.sigpwned.discourse.core.optional.OptionalSystemProperty;
import java.util.List;
import org.junit.Test;

public class SingleCommandInvocationStrategyTest {
//
//  /////////////////////////////////////////////////////////////////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////////////
//  @Configurable
//  public static class EnvironmentAssignmentFailureExample {
//
//    @EnvironmentParameter(variableName = "HELLO")
//    private String example;
//
//    public String getExample() {
//      return example;
//    }
//
//    public void setExample(String example) {
//      throw new RuntimeException("simulated failure");
//    }
//  }
//
//  @Test(expected = AssignmentFailureBeanException.class)
//  public void givenClassWithSimulatedVariableAssignmentFailure_whenInvoke_thenFailWithAssignmentFailureException() {
//    final String hello = "hello";
//
//    SingleCommandInvocationStrategy invoker = new SingleCommandInvocationStrategy();
//    invoker.setVariables(name -> name.equals("HELLO") ? OptionalEnvironmentVariable.of(name, hello)
//        : OptionalEnvironmentVariable.getenv(name));
//
//    invoker.invoke(Command.scan(EnvironmentAssignmentFailureExample.class),
//        new DefaultInvocationContext(), List.of()).getConfiguration();
//  }
//
//  /////////////////////////////////////////////////////////////////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////////////
//  @Configurable
//  public static class PropertyAssignmentFailureExample {
//
//    @PropertyParameter(propertyName = "hello")
//    private String example;
//
//    public String getExample() {
//      return example;
//    }
//
//    public void setExample(String example) {
//      throw new RuntimeException("simulated failure");
//    }
//  }
//
//  @Test(expected = AssignmentFailureBeanException.class)
//  public void givenClassWithSimulatedPropertyAssignmentFailure_whenInvoke_thenFailWithAssignmentFailureException() {
//    final String hello = "hello";
//
//    SingleCommandInvocationStrategy invoker = new SingleCommandInvocationStrategy();
//    invoker.setProperties(name -> name.equals("hello") ? OptionalSystemProperty.of("hello", hello)
//        : OptionalSystemProperty.getProperty(name));
//
//    invoker.invoke(Command.scan(PropertyAssignmentFailureExample.class),
//        new DefaultInvocationContext(), List.of()).getConfiguration();
//  }
}
