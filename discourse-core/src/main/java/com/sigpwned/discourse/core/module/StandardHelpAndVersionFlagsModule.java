package com.sigpwned.discourse.core.module;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.error.ExitError;
import com.sigpwned.discourse.core.module.parameter.flag.FlagCoordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.util.Internationalization;
import com.sigpwned.discourse.core.util.MoreSets;

public class StandardHelpAndVersionFlagsModule extends com.sigpwned.discourse.core.Module {
  private static final String HELP_PROPERTY_NAME = "$help";
  private static final String DEFAULT_HELP_SWITCH = "--help";
  private static final String DEFAULT_HELP_DESCRIPTION = "Show this help message and exit";

  private static final String VERSION_PROPERTY_NAME = "$version";
  private static final String DEFAULT_VERSION_SWITCH = "--version";
  private static final String DEFAULT_VERSION_DESCRIPTION = "Print version information and exit";

  private final Set<SwitchName> helpSwitches;
  private final String helpDescription;
  private final Set<SwitchName> versionSwitches;
  private final String versionDescription;

  public StandardHelpAndVersionFlagsModule() {
    this(
        SwitchName.fromString(
            Internationalization.getMessage(StandardHelpAndVersionFlagsModule.class, "helpFlag")
                .orElse(DEFAULT_HELP_SWITCH)),
        Internationalization.getMessage(StandardHelpAndVersionFlagsModule.class, "helpDescription")
            .orElse(DEFAULT_HELP_DESCRIPTION),
        SwitchName.fromString(
            Internationalization.getMessage(StandardHelpAndVersionFlagsModule.class, "versionFlag")
                .orElse(DEFAULT_VERSION_SWITCH)),
        Internationalization
            .getMessage(StandardHelpAndVersionFlagsModule.class, "versionDescription")
            .orElse(DEFAULT_VERSION_DESCRIPTION));
  }

  public StandardHelpAndVersionFlagsModule(SwitchName helpSwitch, String helpDescription,
      SwitchName versionSwitch, String versionDescription) {
    this(Set.of(helpSwitch), helpDescription, Set.of(versionSwitch), versionDescription);
  }

  public StandardHelpAndVersionFlagsModule(Set<SwitchName> helpSwitches, String helpDescription,
      Set<SwitchName> versionSwitches, String versionDescription) {
    this.helpSwitches = unmodifiableSet(helpSwitches);
    this.helpDescription = requireNonNull(helpDescription);
    this.versionSwitches = unmodifiableSet(versionSwitches);
    this.versionDescription = requireNonNull(versionDescription);
    if (!MoreSets.intersection(getHelpSwitches(), getVersionSwitches()).isEmpty()) {
      // TODO better exception?
      throw new IllegalArgumentException("help and version switches must be disjoint");
    }
  }

  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addFirst(new InvocationPipelineListener() {
      @Override
      public <T> void beforePlanStep(ResolvedCommand<? extends T> resolvedCommand,
          InvocationContext context) {
        LeafCommand<? extends T> leaf = (LeafCommand<? extends T>) resolvedCommand.getCommand();

        // TODO check help coordinates overlap
        // TODO check help parameter name overlap
        // TODO check version coordinates overlap
        // TODO check version parameter name overlap

        // We don't need to localize the help and version property names because the user never
        // sees them.

        if (!getHelpSwitches().isEmpty()) {
          leaf.getProperties()
              .add(new LeafCommandProperty(HELP_PROPERTY_NAME, getHelpDescription(),
                  getHelpSwitches().stream().map(FlagCoordinate::new).collect(toSet()),
                  Boolean.class, emptyList()));
        }

        if (!getVersionSwitches().isEmpty()) {
          leaf.getProperties()
              .add(new LeafCommandProperty(VERSION_PROPERTY_NAME, getVersionDescription(),
                  getVersionSwitches().stream().map(FlagCoordinate::new).collect(toSet()),
                  Boolean.class, emptyList()));
        }
      }

      @Override
      public void afterGroupStep(List<Entry<String, String>> attributedArgs,
          Map<String, List<String>> groupedArgs, InvocationContext context) {
        boolean hasHelp;
        if (groupedArgs.containsKey(HELP_PROPERTY_NAME)
            && !groupedArgs.get(HELP_PROPERTY_NAME).isEmpty()
            && Boolean.parseBoolean(groupedArgs.get(HELP_PROPERTY_NAME).get(0))) {
          hasHelp = true;
        } else {
          hasHelp = false;
        }

        boolean hasVersion;
        if (groupedArgs.containsKey(VERSION_PROPERTY_NAME)
            && !groupedArgs.get(VERSION_PROPERTY_NAME).isEmpty()
            && Boolean.parseBoolean(groupedArgs.get(VERSION_PROPERTY_NAME).get(0))) {
          hasVersion = true;
        } else {
          hasVersion = false;
        }

        // TODO What stream should we print to?
        if (hasVersion) {
          // TODO Use actual version formatter
          System.out.println("version");
        }
        if (hasHelp) {
          // TODO Use actual help formatter
          System.out.println("help");
        }

        if (hasHelp || hasVersion) {
          // TODO throw this using an exit factory?
          throw new ExitError(0);
        }
      }
    });
  }

  @Override
  public List<Module> getDependencies() {
    return List.of(new FlagParameterModule());
  }

  /**
   * @return the helpSwitches
   */
  private Set<SwitchName> getHelpSwitches() {
    return helpSwitches;
  }

  /**
   * @return the helpDescription
   */
  private String getHelpDescription() {
    return helpDescription;
  }

  /**
   * @return the versionSwitches
   */
  private Set<SwitchName> getVersionSwitches() {
    return versionSwitches;
  }

  /**
   * @return the versionDescription
   */
  private String getVersionDescription() {
    return versionDescription;
  }
}