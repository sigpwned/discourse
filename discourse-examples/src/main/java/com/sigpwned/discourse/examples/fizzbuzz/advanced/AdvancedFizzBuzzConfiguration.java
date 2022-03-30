/*-
 * =================================LICENSE_START==================================
 * discourse-examples
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
package com.sigpwned.discourse.examples.fizzbuzz.advanced;

import java.util.Objects;
import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;

/**
 * This configuration moves validation into the configuration object, adds metadata to improve help
 * messages, extends StandardConfigurationBase to provide default support for help and version
 * messages, and uses accessors to encapsulate fields.
 */
@Configurable(name = "fizzbuzz",
    description = "An implementation of the classic programming interview question")
public class AdvancedFizzBuzzConfiguration extends StandardConfigurationBase {
  @PositionalParameter(position = 0, description = "The number to count to", required = true)
  private int count;

  /**
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * @param count the count to set
   */
  public void setCount(int count) {
    this.count = count;
  }

  @OptionParameter(shortName = "f", longName = "fizz",
      description = "The string to print for multiples of 3, fizz by default")
  private String fizz = "fizz";

  /**
   * @return the fizz
   */
  public String getFizz() {
    return fizz;
  }

  /**
   * @param fizz the fizz to set
   */
  public void setFizz(String fizz) {
    this.fizz = fizz;
  }

  @OptionParameter(shortName = "b", longName = "buzz",
      description = "The string to print for multiples of 5, buzz by default")
  private String buzz = "buzz";

  /**
   * @return the buzz
   */
  public String getBuzz() {
    return buzz;
  }

  /**
   * @param buzz the buzz to set
   */
  public void setBuzz(String buzz) {
    this.buzz = buzz;
  }

  public AdvancedFizzBuzzConfiguration validate() {
    if (count < 1)
      throw new IllegalArgumentException("count must be at least 1");
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(buzz, count, fizz);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdvancedFizzBuzzConfiguration other = (AdvancedFizzBuzzConfiguration) obj;
    return Objects.equals(buzz, other.buzz) && count == other.count
        && Objects.equals(fizz, other.fizz);
  }
}
