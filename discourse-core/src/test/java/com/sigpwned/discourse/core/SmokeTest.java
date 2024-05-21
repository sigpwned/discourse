package com.sigpwned.discourse.core;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.invocation.Invocation;
import com.sigpwned.discourse.core.invocation.InvocationPipelineBuilder;
import com.sigpwned.discourse.core.module.DefaultModule;

public class SmokeTest {
  @Configurable(name = "smoke", description = "smoke test")
  public class SmokeTestConfigurable {
    @OptionParameter(shortName = "f", longName = "foo", description = "foo")
    public String foo;

    @PositionalParameter(position = 0, description = "bar")
    public int bar;
  }

  @Test
  public void test() {
    Invocation<? extends SmokeTestConfigurable> invocation =
        new InvocationPipelineBuilder().register(new DefaultModule()).build()
            .execute(SmokeTestConfigurable.class, List.of("-f", "alpha", "42"));

    SmokeTestConfigurable expectedInstance = new SmokeTestConfigurable();
    expectedInstance.foo = "alpha";
    expectedInstance.bar = 42;


    assertThat(invocation.getInstance(), is(expectedInstance));
  }

  @Test
  public void fieldEqualsEqualsTest() throws Exception {
    Field foo1 = SmokeTestConfigurable.class.getDeclaredField("foo");
    Field foo2 = SmokeTestConfigurable.class.getDeclaredField("foo");
    assertTrue(foo1.equals(foo2));
  }
}
