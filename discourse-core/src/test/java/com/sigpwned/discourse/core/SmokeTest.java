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
package com.sigpwned.discourse.core;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.invocation.Invocation;
import com.sigpwned.discourse.core.invocation.InvocationPipelineBuilder;
import com.sigpwned.discourse.core.module.DefaultModule;
import com.sigpwned.discourse.core.util.Discourse;

/**
 * Perform a smoke test with a simple configuration and field assignments.
 */
public class SmokeTest {
  @Configurable(name = "smoke", description = "smoke test")
  public static class SmokeTestConfigurable {
    @OptionParameter(shortName = "f", longName = "foo", description = "foo")
    public String foo;

    @PositionalParameter(position = 0, description = "bar")
    public int bar;

    @Override
    public int hashCode() {
      return Objects.hash(bar, foo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SmokeTestConfigurable other = (SmokeTestConfigurable) obj;
      return bar == other.bar && Objects.equals(foo, other.foo);
    }
  }

  @Test
  public void givenASimpleConfigurableClass_whenUseInvocationBuilder_thenBuildExpectedInstance() {
    Invocation<? extends SmokeTestConfigurable> invocation =
        new InvocationPipelineBuilder().register(new DefaultModule()).build()
            .execute(SmokeTestConfigurable.class, List.of("-f", "alpha", "42"));

    SmokeTestConfigurable expectedInstance = new SmokeTestConfigurable();
    expectedInstance.foo = "alpha";
    expectedInstance.bar = 42;


    assertThat(invocation.getInstance(), is(expectedInstance));
  }

  @Test
  public void givenASimpleConfigurableClass_whenUseWizard_thenBuildExpectedInstance() {
    SmokeTestConfigurable invocationInstance =
        Discourse.configuration(SmokeTestConfigurable.class, List.of("-f", "alpha", "42"));

    SmokeTestConfigurable expectedInstance = new SmokeTestConfigurable();
    expectedInstance.foo = "alpha";
    expectedInstance.bar = 42;


    assertThat(invocationInstance, is(expectedInstance));
  }
}
