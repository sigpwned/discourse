package com.sigpwned.discourse.core.format.help.synopsis.editor;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.entry.CommandNameSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.DiscriminatorSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.MoreLists;

public class CommandNameAndDiscriminatorsSynopsisEditor implements SynopsisEditor {
  @Override
  @SuppressWarnings("unchecked")
  public List<SynopsisEntry> editEntries(PlannedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    List<SynopsisEntry> prefix = new ArrayList<>();
    prefix.add(CommandNameSynopsisEntry.INSTANCE);
    for (int i = 0; i < command.getParents().size(); i++)
      prefix.add(new DiscriminatorSynopsisEntry(i));
    return MoreLists.concat(prefix, entries);
  }
}
