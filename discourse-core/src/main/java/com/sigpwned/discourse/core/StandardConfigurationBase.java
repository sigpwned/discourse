package com.sigpwned.discourse.core;

import java.util.Objects;
import com.sigpwned.discourse.core.annotation.FlagParameter;

public class StandardConfigurationBase {
  @FlagParameter(longName = "help", help = true, description = "Print this help message")
  private boolean help = false;

  /**
   * @return the help
   */
  public boolean isHelp() {
    return help;
  }

  /**
   * @param help the help to set
   */
  public void setHelp(boolean help) {
    this.help = help;
  }

  @FlagParameter(longName = "version", version = true,
      description = " The current version of this software")
  private boolean version = false;

  /**
   * @return the version
   */
  public boolean isVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(boolean version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    return Objects.hash(help, version);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StandardConfigurationBase other = (StandardConfigurationBase) obj;
    return help == other.help && version == other.version;
  }
}
