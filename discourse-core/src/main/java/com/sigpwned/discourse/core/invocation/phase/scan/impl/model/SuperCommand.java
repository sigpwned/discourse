package com.sigpwned.discourse.core.invocation.phase.scan.impl.model;

public record SuperCommand<T>(Class<T> clazz, String discriminator) {

}

