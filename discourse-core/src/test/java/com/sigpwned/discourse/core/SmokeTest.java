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
import com.sigpwned.discourse.core.annotation.DiscourseAttribute;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.annotation.DiscourseDescription;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.module.CoreModule;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineBuilder;
import com.sigpwned.discourse.core.util.Discourse;

/**
 * Perform a smoke test with a simple configuration and field assignments.
 */
public class SmokeTest {
  @DiscourseDescription("smoke test")
  @Configurable(name = "smoke")
  public static class SmokeTestConfigurable {
    @DiscourseDescription("foo")
    @OptionParameter(shortName = "f", longName = "foo")
    public String foo;

    @DiscourseDescription("bar")
    @PositionalParameter(position = 0)
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
    SmokeTestConfigurable instance = new InvocationPipelineBuilder().register(new CoreModule())
        .build().invoke(SmokeTestConfigurable.class, List.of("-f", "alpha", "42"));

    SmokeTestConfigurable expectedInstance = new SmokeTestConfigurable();
    expectedInstance.foo = "alpha";
    expectedInstance.bar = 42;


    assertThat(instance, is(expectedInstance));
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

  @DiscourseDescription("Bad Rules")
  @Configurable(name = "badrules")
  public static class InaccessableFieldConfigurable {
    @DiscourseDescription("foo")
    @OptionParameter(shortName = "f", longName = "foo")
    private String foo;
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenAConfigurableClassWithInaccessibleField_whenUseWizard_thenThrowException() {
    // TODO fix when better exception is done
    Discourse.configuration(InaccessableFieldConfigurable.class, List.of("-f", "alpha"));
  }

  @DiscourseDescription("Custom Constructor with Syntax Parameter")
  @Configurable(name = "customconstructorsyntaxparam")
  public static class CustomConstructorWithSyntaxParameterConfigurable {
    @DiscourseCreator
    public CustomConstructorWithSyntaxParameterConfigurable(
        @DiscourseRequired @DiscourseDescription("foo") @OptionParameter(shortName = "f",
            longName = "foo") @DiscourseAttribute("foo") String foo) {
      this.foo = foo;
    }


    private final String foo;

    public String getFoo() {
      return foo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(foo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CustomConstructorWithSyntaxParameterConfigurable other =
          (CustomConstructorWithSyntaxParameterConfigurable) obj;
      return Objects.equals(foo, other.foo);
    }
  }

  @Test
  public void givenAConfigurableClassWithCustomConstructorAndSyntaxParameter_whenUseWizard_thenBuildExpectedInstance() {
    CustomConstructorWithSyntaxParameterConfigurable instance = Discourse.configuration(
        CustomConstructorWithSyntaxParameterConfigurable.class, List.of("-f", "alpha"));
    assertThat(instance, is(new CustomConstructorWithSyntaxParameterConfigurable("alpha")));
  }

  @DiscourseDescription("Custom Constructor with Syntax Field")
  @Configurable(name = "customconstructorsyntaxfield")
  public static class CustomConstructorWithSyntaxFieldConfigurable {
    @DiscourseCreator
    public CustomConstructorWithSyntaxFieldConfigurable(@DiscourseAttribute("foo") String foo) {
      this.foo = foo;
    }

    @DiscourseRequired
    @DiscourseDescription("foo")
    @OptionParameter(shortName = "f", longName = "foo")
    private final String foo;

    public String getFoo() {
      return foo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(foo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CustomConstructorWithSyntaxFieldConfigurable other =
          (CustomConstructorWithSyntaxFieldConfigurable) obj;
      return Objects.equals(foo, other.foo);
    }
  }

  @Test
  public void givenAConfigurableClassWithCustomConstructorAndSyntaxField_whenUseWizard_thenBuildExpectedInstance() {
    CustomConstructorWithSyntaxFieldConfigurable instance = Discourse
        .configuration(CustomConstructorWithSyntaxFieldConfigurable.class, List.of("-f", "alpha"));
    assertThat(instance, is(new CustomConstructorWithSyntaxFieldConfigurable("alpha")));
  }

  @DiscourseDescription("Factory Method with Syntax Parameter")
  @Configurable(name = "factorymethodsyntaxfield")
  public static class FactoryMethodWithSyntaxFieldConfigurable {
    @DiscourseCreator
    @DiscourseAttribute("")
    public static FactoryMethodWithSyntaxFieldConfigurable of(
        @DiscourseAttribute("foo") String foo) {
      return new FactoryMethodWithSyntaxFieldConfigurable(foo);
    }

    private FactoryMethodWithSyntaxFieldConfigurable(String foo) {
      this.foo = foo;
    }

    @DiscourseRequired
    @DiscourseDescription("foo")
    @OptionParameter(shortName = "f", longName = "foo")
    private final String foo;

    public String getFoo() {
      return foo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(foo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      FactoryMethodWithSyntaxFieldConfigurable other =
          (FactoryMethodWithSyntaxFieldConfigurable) obj;
      return Objects.equals(foo, other.foo);
    }
  }

  @Test
  public void givenConfigurableClassWithFactoryMethodAndSyntaxField_whenUseWizard_thenBuildExpectedInstance() {
    FactoryMethodWithSyntaxFieldConfigurable instance = Discourse
        .configuration(FactoryMethodWithSyntaxFieldConfigurable.class, List.of("-f", "alpha"));
    assertThat(instance, is(FactoryMethodWithSyntaxFieldConfigurable.of("alpha")));
  }
}
