package com.sigpwned.discourse.core.invocation.model;

import com.sigpwned.discourse.core.command.Command;

public record CommandDereference<T>(Command<T> command, String discriminator) {

}