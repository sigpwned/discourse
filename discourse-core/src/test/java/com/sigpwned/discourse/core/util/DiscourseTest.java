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

import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import java.util.List;
import java.util.Objects;
import org.junit.Test;

public class DiscourseTest {

  @Configurable
  public static class Example extends StandardConfigurationBase {

    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 0)
    public String position0;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(flag, option, position0);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Example other = (Example) obj;
      return flag == other.flag && Objects.equals(option, other.option) && Objects.equals(position0,
          other.position0);
    }
  }

  @Test
  public void givenSimpleExample_whenInvoke_thenSucceedWithExpectedValue() {
    final String alpha = "alpha";
    final String bravo = "bravo";

    Example observed = Discourse.configuration(Example.class, List.of("-f", "-o", alpha, bravo));

    Example expected = new Example();
    expected.flag = true;
    expected.option = alpha;
    expected.position0 = bravo;

    assertThat(observed, is(expected));
  }
}
