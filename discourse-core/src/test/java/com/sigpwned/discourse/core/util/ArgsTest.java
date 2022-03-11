package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;

public class ArgsTest {
  @Test
  public void containsFlagBundleTrueTest() {
    assertThat(Args.containsFlag(asList("-xyz"), ShortSwitchNameCoordinate.fromString("y"), null),
        is(true));
  }

  @Test
  public void containsFlagBundleFalseTest() {
    assertThat(Args.containsFlag(asList("-xyz"), ShortSwitchNameCoordinate.fromString("a"), null),
        is(false));
  }

  @Test
  public void containsShortNameBundleTrueTest() {
    assertThat(Args.containsFlag(asList("-x"), ShortSwitchNameCoordinate.fromString("x"), null),
        is(true));
  }

  @Test
  public void containsShortNameBundleFalseTest() {
    assertThat(Args.containsFlag(asList("-x"), ShortSwitchNameCoordinate.fromString("a"), null),
        is(false));
  }

  @Test
  public void containsLongNameBundleTrueTest() {
    assertThat(
        Args.containsFlag(asList("--hello"), null, LongSwitchNameCoordinate.fromString("hello")),
        is(true));
  }

  @Test
  public void containsLongNameBundleFalseTest() {
    assertThat(
        Args.containsFlag(asList("--hello"), null, LongSwitchNameCoordinate.fromString("world")),
        is(false));
  }

  @Test
  public void separatorTest() {
    assertThat(Args.containsFlag(asList("--"), null, null), is(false));
  }
}
