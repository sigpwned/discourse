package com.sigpwned.discourse.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import org.junit.Test;
import com.google.common.reflect.TypeToken;

public class SortedSetTypeTest {
  public static final TypeToken<Collection<String>> COLLECTION_OF_STRING = new TypeToken<Collection<String>>() {};

  @Test(expected = IllegalArgumentException.class)
  public void parameterizedTest() {
    SortedSetType.parse(COLLECTION_OF_STRING.getType());
  }

  @Test(expected = IllegalArgumentException.class)
  public void objectTest() {
    SortedSetType.parse(Object.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rawTest() {
    SortedSetType.parse(List.class);
  }

  public static final TypeToken<SortedSet<String>> SORTED_SET_OF_STRING = new TypeToken<SortedSet<String>>() {};

  @Test
  public void concreteTest() {
    SortedSetType observed = SortedSetType.parse(SORTED_SET_OF_STRING.getType());

    assertThat(observed, is(SortedSetType.of(String.class)));
  }
  
  public static class SortedSetTest<T> {
    public TypeToken<SortedSet<T>> token=new TypeToken<SortedSet<T>>() {};
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void unresolvedTest() {
    SortedSetType.parse(new SortedSetTest<String>().token.getType());
  }
}
