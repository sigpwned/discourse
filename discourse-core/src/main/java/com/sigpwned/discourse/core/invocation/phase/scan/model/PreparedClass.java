package com.sigpwned.discourse.core.invocation.phase.scan.model;

import java.util.Optional;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.CommandBody;

public record PreparedClass<T>(Optional<SuperCommand<? super T>> supercommand, Class<T> clazz,
    Configurable configurable, Optional<CommandBody<T>> body) {
}
