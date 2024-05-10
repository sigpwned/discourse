package com.sigpwned.discourse.core.invocation.phase.resolve.model;

import com.sigpwned.discourse.core.invocation.phase.scan.Command;

public record CommandDereference<T>(Command<T> command, String discriminator) {

}
