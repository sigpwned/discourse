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
