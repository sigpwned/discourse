package com.sigpwned.discourse.core.util;

public final class Consoles {
  private Consoles() {}

  // METHODS //////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns an ANSI control sequence with the given codes.
   * 
   * @param firstCode The first code. Must not be {@code null}.
   * @param moreCodes Any additional codes. May be empty. Must not contain {@code null}, or contain
   *        {@code null} elements.
   * @return The ANSI control sequence.
   * @throws NullPointerException If {@code firstCode} is {@code null}, or if {@code moreCodes} is
   *         {@code null}, or if {@code moreCodes} contains {@code null}.
   * 
   * @see <a href=
   *      "https://en.wikipedia.org/wiki/ANSI_escape_code">https://en.wikipedia.org/wiki/ANSI_escape_code
   */
  public static String ansiControlSequence(String firstCode, String... moreCodes) {
    if (firstCode == null)
      throw new NullPointerException();
    if (moreCodes == null)
      throw new NullPointerException();

    StringBuilder result = new StringBuilder().append(ESC).append("[").append(firstCode);
    for (String code : moreCodes) {
      if (code == null)
        throw new NullPointerException();
      result.append(";").append(code);
    }

    return result.append("m").toString();
  }

  // CONTROL CHARACTERS ///////////////////////////////////////////////////////////////////////////

  public static final String ESC = "\u001B";

  // GRAPHICS /////////////////////////////////////////////////////////////////////////////////////

  public static final String BOLD = "1";

  public static final String FAINT = "2";

  public static final String ITALIC = "3";

  public static final String UNDERLINE = "4";

  public static final String BLINK = "5";

  public static final String REVERSE = "7";

  public static final String INVISIBLE = "8";

  public static final String STRIKETHROUGH = "9";

  // RESETS ///////////////////////////////////////////////////////////////////////////////////////

  public static final String RESET_ALL = "0";

  public static final String RESET_BOLD = "21";

  public static final String RESET_FAINT = "22";

  public static final String RESET_ITALIC = "23";

  public static final String RESET_UNDERLINE = "24";

  public static final String RESET_BLINK = "25";

  public static final String RESET_REVERSE = "27";

  public static final String RESET_INVISIBLE = "28";

  public static final String RESET_STRIKETHROUGH = "29";

  // FOREGROUND COLORS ////////////////////////////////////////////////////////////////////////////
  public static final String BLACK_FOREGROUND = "30";

  public static final String RED_FOREGROUND = "31";

  public static final String GREEN_FOREGROUND = "32";

  public static final String YELLOW_FOREGROUND = "33";

  public static final String BLUE_FOREGROUND = "34";

  public static final String PURPLE_FOREGROUND = "35";

  public static final String CYAN_FOREGROUND = "36";

  public static final String WHITE_FOREGROUND = "37";

  // BACKGROUND COLORS ////////////////////////////////////////////////////////////////////////////

  public static final String BLACK_BACKGROUND = "40";

  public static final String RED_BACKGROUND = "41";

  public static final String GREEN_BACKGROUND = "42";

  public static final String YELLOW_BACKGROUND = "43";

  public static final String BLUE_BACKGROUND = "44";

  public static final String PURPLE_BACKGROUND = "45";

  public static final String CYAN_BACKGROUND = "46";

  public static final String WHITE_BACKGROUND = "47";
}
