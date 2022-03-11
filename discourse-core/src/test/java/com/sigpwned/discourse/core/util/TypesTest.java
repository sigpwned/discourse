package com.sigpwned.discourse.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import org.junit.Test;
import com.google.common.reflect.TypeToken;

public class TypesTest {
  @Test
  public void primitiveTest() {
    assertThat(Types.isPrimitive(byte.class), is(true));
    assertThat(Types.isPrimitive(short.class), is(true));
    assertThat(Types.isPrimitive(int.class), is(true));
    assertThat(Types.isPrimitive(long.class), is(true));
    assertThat(Types.isPrimitive(float.class), is(true));
    assertThat(Types.isPrimitive(double.class), is(true));
    assertThat(Types.isPrimitive(char.class), is(true));
    assertThat(Types.isPrimitive(boolean.class), is(true));

    assertThat(Types.isPrimitive(String.class), is(false));
  }
  
  @Test
  public void boxedTest() {
    assertThat(Types.boxed(byte.class), sameInstance(Byte.class));
    assertThat(Types.boxed(short.class), sameInstance(Short.class));
    assertThat(Types.boxed(int.class), sameInstance(Integer.class));
    assertThat(Types.boxed(long.class), sameInstance(Long.class));
    assertThat(Types.boxed(float.class), sameInstance(Float.class));
    assertThat(Types.boxed(double.class), sameInstance(Double.class));
    assertThat(Types.boxed(char.class), sameInstance(Character.class));
    assertThat(Types.boxed(boolean.class), sameInstance(Boolean.class));
  }
  
  public static final TypeToken<List<String>> LIST_OF_STRING = new TypeToken<List<String>>() {};
  
  public static class TokenTest<T> {
    public TypeToken<List<T>> token=new TypeToken<List<T>>() {};
  }

  @Test
  public void concreteTest() {
    assertThat(Types.isConcrete(String.class), is(true));
    assertThat(Types.isConcrete(LIST_OF_STRING.getType()), is(true));
    assertThat(Types.isConcrete(new TokenTest<String>().token.getType()), is(false));
  }
}
