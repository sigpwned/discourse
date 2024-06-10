package com.sigpwned.discourse.core.format.help;

import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class SynopsisEditorChain extends Chain<SynopsisEditor> implements SynopsisEditor {
  @Override
  public List<SynopsisEntry> editEntries(ResolvedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    for (SynopsisEditor editor : this)
      entries = editor.editEntries(command, entries, context);
    return entries;
  }
}
