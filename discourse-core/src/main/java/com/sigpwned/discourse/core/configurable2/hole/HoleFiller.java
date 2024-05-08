package com.sigpwned.discourse.core.configurable2.hole;

import com.sigpwned.discourse.core.configurable2.Peg;
import java.util.Map;

public interface HoleFiller {

  public void fill(ConfigurableHole hole, Map<String, Peg> pegs);
}
