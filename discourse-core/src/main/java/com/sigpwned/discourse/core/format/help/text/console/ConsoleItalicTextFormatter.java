package com.sigpwned.discourse.core.format.help.text.console;

import com.sigpwned.discourse.core.format.help.text.ItalicTextFormatterBase;
import com.sigpwned.discourse.core.util.Consoles;

public class ConsoleItalicTextFormatter extends ItalicTextFormatterBase {
  public static final String BEGIN_FORMAT = Consoles.ansiControlSequence(Consoles.ITALIC);

  public static final String END_FORMAT = Consoles.ansiControlSequence(Consoles.RESET_ITALIC);

  public static final ConsoleItalicTextFormatter INSTANCE = new ConsoleItalicTextFormatter();

  public ConsoleItalicTextFormatter() {
    super(BEGIN_FORMAT, END_FORMAT);
  }
}
