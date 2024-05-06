package com.sigpwned.discourse.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import org.junit.Test;

/**
 * This does not test that the Java reflection API works. I'm pretty confident that it does! Rather,
 * this tests that it works the way I think it does.
 */
public class ReflectionTest {

  public static class Parent {

    public String foobar() {
      return "parent";
    }
  }

  public static class Child extends Parent {

    @Override
    public String foobar() {
      return "child";
    }
  }

  @Test
  public void givenMethodFromParentClass_whenInvokeOnChildInstanceWithOverride_thenChildOverrideIsInvoked()
      throws Exception {
    Method parentFoobar = Parent.class.getMethod("foobar");
    Child childInstance = new Child();
    String result = (String) parentFoobar.invoke(childInstance);
    assertThat(result, is("child"));
  }
}
