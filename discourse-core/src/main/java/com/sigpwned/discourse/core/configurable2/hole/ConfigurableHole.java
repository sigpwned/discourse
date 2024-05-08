package com.sigpwned.discourse.core.configurable2.hole;

import com.sigpwned.discourse.core.configurable2.Peg;
import java.util.Optional;
import java.util.Set;

public record ConfigurableHole(Object nominated, Set<String> antecedents, Optional<Peg> consequent) {

}
