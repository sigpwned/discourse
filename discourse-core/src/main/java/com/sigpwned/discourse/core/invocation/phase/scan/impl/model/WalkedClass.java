package com.sigpwned.discourse.core.invocation.phase.scan.impl.model;

import java.util.Optional;
import com.sigpwned.discourse.core.annotation.Configurable;

public record WalkedClass<T>(Optional<SuperCommand<? super T>> supercommand, Class<T> clazz,
    Configurable configurable) {
}
