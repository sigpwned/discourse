package com.sigpwned.discourse.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.google.common.reflect.TypeToken;

public class SetTypeTest {
  public static final TypeToken<Collection<String>> COLLECTION_OF_STRING = new TypeToken<Collection<String>>() {};

  @Test(expected = IllegalArgumentException.class)
  public void parameterizedTest() {
    SetType.parse(COLLECTION_OF_STRING.getType());
  }

  @Test(expected = IllegalArgumentException.class)
  public void objectTest() {
    SetType.parse(Object.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rawTest() {
    SetType.parse(List.class);
  }

  public static final TypeToken<Set<String>> SET_OF_STRING = new TypeToken<Set<String>>() {};

  @Test
  public void concreteTest() {
    SetType observed = SetType.parse(SET_OF_STRING.getType());

    assertThat(observed, is(SetType.of(String.class)));
  }
  
  public static class SetTest<T> {
    public TypeToken<Set<T>> token=new TypeToken<Set<T>>() {};
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void unresolvedTest() {
    SetType.parse(new SetTest<String>().token.getType());
  }
}
