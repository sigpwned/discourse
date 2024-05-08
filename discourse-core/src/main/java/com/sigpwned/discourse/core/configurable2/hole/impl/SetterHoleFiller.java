package com.sigpwned.discourse.core.configurable2.hole.impl;

import com.sigpwned.discourse.core.configurable2.Peg;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHole;
import com.sigpwned.discourse.core.configurable2.hole.HoleFiller;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SetterHoleFiller implements HoleFiller {

  @Override
  public void fill(ConfigurableHole hole, Map<String, Peg> pegs) {
    Method setter = (Method) hole.nominated();

    Object instance = pegs.get("");
    Object value = pegs.values().stream()
        .filter(peg -> peg.nominated() == setter.getParameters()[0])
        .flatMap(peg -> peg.value().stream())
        .findFirst()
        .orElseThrow();

    try {
      setter.invoke(instance, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
