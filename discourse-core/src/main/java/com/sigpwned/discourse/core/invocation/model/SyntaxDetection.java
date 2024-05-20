package com.sigpwned.discourse.core.invocation.model;

import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;

public record SyntaxDetection(boolean required, boolean help, boolean version,
    Set<Coordinate> coordinates) {

}
