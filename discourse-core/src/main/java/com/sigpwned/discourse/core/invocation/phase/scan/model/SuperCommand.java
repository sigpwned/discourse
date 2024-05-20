package com.sigpwned.discourse.core.invocation.phase.scan.model;

import com.sigpwned.discourse.core.command.Discriminator;

public record SuperCommand<T>(Class<T> clazz, Discriminator discriminator) {

}

