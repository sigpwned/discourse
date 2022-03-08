package com.sigpwned.discourse.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import com.google.common.reflect.TypeToken;

public class ListTypeTest {
  public static final TypeToken<Collection<String>> COLLECTION_OF_STRING = new TypeToken<Collection<String>>() {};

  @Test(expected = IllegalArgumentException.class)
  public void parameterizedTest() {
    ListType.parse(COLLECTION_OF_STRING.getType());
  }

  @Test(expected = IllegalArgumentException.class)
  public void objectTest() {
    ListType.parse(Object.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rawTest() {
    ListType.parse(List.class);
  }

  public static final TypeToken<List<String>> LIST_OF_STRING = new TypeToken<List<String>>() {};

  @Test
  public void concreteTest() {
    ListType observed = ListType.parse(LIST_OF_STRING.getType());

    assertThat(observed, is(ListType.of(String.class)));
  }
  
  public static class ListTest<T> {
    public TypeToken<List<T>> token=new TypeToken<List<T>>() {};
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void unresolvedTest() {
    ListType.parse(new ListTest<String>().token.getType());
  }
}
