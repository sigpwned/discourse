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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.sigpwned.discourse.core.model.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.ShortSwitchNameCoordinate;
import java.util.List;
import org.junit.Test;

public class ArgsTest {

  @Test
  public void givenArgsWithBundleContainingShortSwitch_whenSearchForSwitch_thenDoFind() {
    assertThat(Args.containsFlag(List.of("-xyz"), ShortSwitchNameCoordinate.fromString("y"), null),
        is(true));
  }

  @Test
  public void givenArgsWithBundleNotContainingShortSwitch_whenSearchForSwitch_thenDontFind() {
    assertThat(Args.containsFlag(List.of("-xyz"), ShortSwitchNameCoordinate.fromString("a"), null),
        is(false));
  }

  @Test
  public void givenArgsWithShortSwitch_whenSearchForThatSwitch_thenDoFind() {
    assertThat(Args.containsFlag(List.of("-x"), ShortSwitchNameCoordinate.fromString("x"), null),
        is(true));
  }

  @Test
  public void givenArgsWithShortSwitch_whenSearchForDifferentSwitch_thenDontFind() {
    assertThat(Args.containsFlag(List.of("-x"), ShortSwitchNameCoordinate.fromString("a"), null),
        is(false));
  }

  @Test
  public void givenArgsWithLongSwitch_whenSearchForThatSwitch_thenDoFind() {
    assertThat(
        Args.containsFlag(List.of("--hello"), null, LongSwitchNameCoordinate.fromString("hello")),
        is(true));
  }

  @Test
  public void givenArgsWithLongSwitch_whenSearchForDifferentSwitch_thenDoFind() {
    assertThat(
        Args.containsFlag(List.of("--hello"), null, LongSwitchNameCoordinate.fromString("world")),
        is(false));
  }

  @Test
  public void givenArgsWithSeparator_whenSearchNoSwitches_thenDontFind() {
    assertThat(Args.containsFlag(List.of("--"), null, null), is(false));
  }
}
