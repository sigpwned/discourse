package com.sigpwned.discourse.examples.fizzbuzz.advanced;

import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.util.Discourse;

public class AdvancedFizzBuzz extends StandardConfigurationBase {
  public static void main(String[] args) {
    AdvancedFizzBuzzConfiguration configuration =
        Discourse.configure(AdvancedFizzBuzzConfiguration.class, args).validate();
    for (int i = 1; i <= configuration.getCount(); i++) {
      boolean mod3 = (i % 3) == 0;
      boolean mod5 = (i % 5) == 0;
      if (mod3 && mod5)
        System.out.println(configuration.getFizz() + " " + configuration.getBuzz());
      else if (mod3)
        System.out.println(configuration.getFizz());
      else if (mod5)
        System.out.println(configuration.getBuzz());
      else
        System.out.println(i);
    }
  }
}
