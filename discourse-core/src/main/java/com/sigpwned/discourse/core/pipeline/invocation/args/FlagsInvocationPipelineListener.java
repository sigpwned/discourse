package com.sigpwned.discourse.core.pipeline.invocation.args;

import static java.util.Collections.unmodifiableSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;

public class FlagsInvocationPipelineListener implements ArgsInvocationPipelineListener {
  private final Set<SwitchName> flags;

  public FlagsInvocationPipelineListener(Set<SwitchName> flags) {
    this.flags = unmodifiableSet(flags);
  }

  @Override
  public void afterTokenizeStep(List<String> args, List<Token> tokens) {
    ListIterator<Token> iterator = tokens.listIterator();
    while (iterator.hasNext()) {
      Token token = iterator.next();
      if (token instanceof SwitchNameToken switchNameToken) {
        SwitchName switchName = switchNameToken.getName();
        if (getFlags().contains(switchName)) {
          iterator.add(new ValueToken("true", false));
        }
      }
    }
  }

  private Set<SwitchName> getFlags() {
    return flags;
  }
}
