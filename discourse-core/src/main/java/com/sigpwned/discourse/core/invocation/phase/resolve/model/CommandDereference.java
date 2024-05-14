package com.sigpwned.discourse.core.invocation.phase.resolve.model;

import com.sigpwned.discourse.core.invocation.model.command.Command;

public record CommandDereference<T>(Command<T> command, String discriminator) {

}
