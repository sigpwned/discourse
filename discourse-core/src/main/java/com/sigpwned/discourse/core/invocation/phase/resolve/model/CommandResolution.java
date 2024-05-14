package com.sigpwned.discourse.core.invocation.phase.resolve.model;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import com.sigpwned.discourse.core.invocation.model.command.RootCommand;
import java.util.List;

public record CommandResolution<T>(RootCommand<? super T> rootCommand, Command<T> resolvedCommand,
    List<CommandDereference<? super T>> dereferences, List<String> remainingArgs) {

}
