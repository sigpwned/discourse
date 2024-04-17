/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;

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
