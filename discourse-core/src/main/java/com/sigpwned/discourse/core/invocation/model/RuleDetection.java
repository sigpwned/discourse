package com.sigpwned.discourse.core.invocation.model;

import java.util.Set;

public record RuleDetection(Set<String> antecedents, boolean hasConsequent) {

}
