package com.sigpwned.discourse.core.format.version;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.VersionFormatter;

public class DefaultVersionFormatter implements VersionFormatter {
  @Override
  public String formatVersion(Command<?> command) {
    List<String> parts=new ArrayList<>();
    if(command.getName() != null)
      parts.add(command.getName());
    if(command.getVersion() != null)
      parts.add(command.getVersion());
    return String.join(" ", parts);
  }
}
