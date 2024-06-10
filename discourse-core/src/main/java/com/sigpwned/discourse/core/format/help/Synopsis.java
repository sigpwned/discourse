package com.sigpwned.discourse.core.format.help;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;

/**
 * <p>
 * A synopsis is a brief description of the structure of a command. It should be a single line of
 * text that is an (over)simplified example of a command invocation. It should be simple enough to
 * be understood by a novice user, but detailed enough to give an experienced user a good idea of
 * what the command does.
 * </p>
 * 
 * <p>
 * For example, from the man page for <code>git pull</code>:
 * </p>
 * 
 * <pre>
 * git pull [&lt;options&gt;] [&lt;repository&gt; [&lt;refspec&gt;...]]
 * </pre>
 */
public class Synopsis {
  private final List<SynopsisEntry> entries;

  public Synopsis(List<SynopsisEntry> entries) {
    this.entries = unmodifiableList(entries);
    if (entries.isEmpty())
      throw new IllegalArgumentException("entries must not be empty");
  }

  private List<SynopsisEntry> getEntries() {
    return this.entries;
  }

  public String getText() {
    // TODO Do we want any formatting? Text wrap?
    StringBuilder result = new StringBuilder();
    result.append(getEntries().get(0).getText());
    for (int i = 1; i < getEntries().size(); i++) {
      result.append(' ');
      result.append(getEntries().get(i).getText());
    }
    return result.toString();
  }
}
