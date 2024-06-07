package com.sigpwned.discourse.core.format.help.text.console;

import com.sigpwned.discourse.core.format.help.text.StrikethruTextFormatterBase;
import com.sigpwned.discourse.core.util.Consoles;

public class ConsoleStrikethruTextFormatter extends StrikethruTextFormatterBase {
  public static final String BEGIN_FORMAT = Consoles.ansiControlSequence(Consoles.STRIKETHROUGH);

  public static final String END_FORMAT =
      Consoles.ansiControlSequence(Consoles.RESET_STRIKETHROUGH);

  public static final ConsoleStrikethruTextFormatter INSTANCE =
      new ConsoleStrikethruTextFormatter();

  public ConsoleStrikethruTextFormatter() {
    super(BEGIN_FORMAT, END_FORMAT);
  }
}
