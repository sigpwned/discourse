package com.sigpwned.discourse.examples.fizzbuzz.selfcontained;

import java.util.Objects;
import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.util.Discourse;

/**
 * This configuration class puts the configuration model objects and business logic in one class.
 * This is a perfectly valid choice that Discourse allows, but does not require.
 */
@Configurable(name = "fizzbuzz",
    description = "An implementation of the classic programming interview question")
public class SelfContainedFizzBuzz extends StandardConfigurationBase {
  @PositionalParameter(position = 0, description = "The number to count to", required = true)
  public int count;

  @OptionParameter(shortName = "f", longName = "fizz",
      description = "The string to print for multiples of 3, fizz by default")
  public String fizz = "fizz";

  @OptionParameter(shortName = "b", longName = "buzz",
      description = "The string to print for multiples of 5, buzz by default")
  public String buzz = "buzz";


  public SelfContainedFizzBuzz validate() {
    if (count < 1)
      throw new IllegalArgumentException("count must be at least 1");
    return this;
  }

  public static void main(String[] args) {
    Discourse.configuration(SelfContainedFizzBuzz.class, args).validate().run();
  }

  public void run() {
    for (int i = 1; i <= count; i++) {
      boolean mod3 = (i % 3) == 0;
      boolean mod5 = (i % 5) == 0;
      if (mod3 && mod5)
        System.out.println(fizz + " " + buzz);
      else if (mod3)
        System.out.println(fizz);
      else if (mod5)
        System.out.println(buzz);
      else
        System.out.println(i);
    }
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
    SelfContainedFizzBuzz other = (SelfContainedFizzBuzz) obj;
    return Objects.equals(buzz, other.buzz) && count == other.count
        && Objects.equals(fizz, other.fizz);
  }
}
