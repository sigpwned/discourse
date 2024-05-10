package com.sigpwned.discourse.core.invocation.phase.resolve.model;

import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import java.util.List;

public record ResolvedCommand<T>(Command<T> command,
    List<CommandDereference<? super T>> discriminators, List<String> remainingArgs) {

}
