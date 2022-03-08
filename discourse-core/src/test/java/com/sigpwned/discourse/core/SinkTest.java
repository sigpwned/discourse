package com.sigpwned.discourse.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;

public class SinkTest {
  @Configurable
  public static class SinkExample {
    @OptionParameter(longName = "assign")
    public String assign;

    @OptionParameter(longName = "list")
    public List<String> list;

    @OptionParameter(longName = "set")
    public Set<String> set;

    @OptionParameter(longName = "sortedSet")
    public SortedSet<String> sortedSet;

    @OptionParameter(longName = "array")
    public String[] array;
    
    public SinkExample() {
    }

    public SinkExample(String assign, List<String> list, Set<String> set,
        SortedSet<String> sortedSet, String[] array) {
      this.assign = assign;
      this.list = list;
      this.set = set;
      this.sortedSet = sortedSet;
      this.array = array;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(array);
      result = prime * result + Objects.hash(assign, list, set, sortedSet);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SinkExample other = (SinkExample) obj;
      return Arrays.equals(array, other.array) && Objects.equals(assign, other.assign)
          && Objects.equals(list, other.list) && Objects.equals(set, other.set)
          && Objects.equals(sortedSet, other.sortedSet);
    }
  }

  @Test
  public void sinkTest() {
    SinkExample observed=new Configurator<>(SinkExample.class).args(asList("--assign", "alpha", "--list",
        "bravo", "--list", "charlie", "--set", "delta", "--set", "echo", "--sortedSet", "foxtrot",
        "--sortedSet", "golf", "--array", "hotel", "--array", "india")).done();
    
    assertThat(observed, is(new SinkExample("alpha", ImmutableList.of("bravo", "charlie"), ImmutableSet.of("delta", "echo"), ImmutableSortedSet.of("foxtrot", "golf"), new String[] {"hotel", "india"})));
  }
}
