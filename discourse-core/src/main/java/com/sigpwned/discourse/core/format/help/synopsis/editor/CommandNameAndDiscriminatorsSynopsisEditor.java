package com.sigpwned.discourse.core.format.help.synopsis.editor;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.ParentCommand;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.entry.LiteralSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.CommandNameSynopsisEntryId;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.DiscriminatorSynopsisEntryId;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.MoreLists;

public class CommandNameAndDiscriminatorsSynopsisEditor implements SynopsisEditor {
  @Override
  @SuppressWarnings("unchecked")
  public List<SynopsisEntry> editEntries(ResolvedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    // TODO better exception
    List<SynopsisEntry> prefix = new ArrayList<>();
    prefix.add(new LiteralSynopsisEntry(CommandNameSynopsisEntryId.INSTANCE,
        command.getName().orElseThrow()));
    for (int i = 0; i < command.getParents().size(); i++) {
      ParentCommand parent = command.getParents().get(i);
      prefix.add(
          new LiteralSynopsisEntry(new DiscriminatorSynopsisEntryId(i), parent.getDiscriminator()));
    }

    return MoreLists.concat(prefix, entries);
  }
}
