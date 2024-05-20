package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.syntax.SyntaxTokenizer;

public interface Syntax {
  public SyntaxTokenizer newTokenizer();

  /**
   * <p>
   * Formats the given switch name for display, e.g., in help messages.
   * </p>
   * 
   * <p>
   * For example, a Unix-style syntax might format the switch name {@code "help"} as
   * {@code "--help"}. A Windows-style syntax might format the same switch name as {@code "/help"}.
   * </p>
   * 
   * @param name The switch name to format.
   * @return The formatted switch name.
   */
  public String formatSwitchName(SwitchName name);
}
