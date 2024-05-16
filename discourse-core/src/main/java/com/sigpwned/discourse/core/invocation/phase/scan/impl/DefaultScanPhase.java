package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.CommandBody;
import com.sigpwned.discourse.core.command.CommandProperty;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.command.SubCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.PreparedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.SuperCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.WalkedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.DetectedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.DetectedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.util.Graphs;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import com.sigpwned.discourse.core.value.sink.ValueSinkFactory;

public class DefaultScanPhase implements ScanPhase {
  private final SubCommandScanner subCommandScanner;
  private final SyntaxNominator syntaxNominator;
  private final SyntaxDetector syntaxDetector;
  private final RuleNominator ruleNominator;
  private final RuleDetector ruleDetector;
  private final NamingScheme namingScheme;
  private final ValueSinkFactory valueSinkFactory;
  private final ValueDeserializerFactory<?> valueDeserializerFactory;

  public DefaultScanPhase(SubCommandScanner subCommandScanner, SyntaxNominator syntaxNominator,
      SyntaxDetector syntaxDetector, RuleNominator ruleNominator, RuleDetector ruleDetector,
      NamingScheme namingScheme, ValueSinkFactory valueSinkFactory,
      ValueDeserializerFactory<?> valueDeserializerFactory) {
    this.subCommandScanner = requireNonNull(subCommandScanner);
    this.syntaxNominator = requireNonNull(syntaxNominator);
    this.syntaxDetector = requireNonNull(syntaxDetector);
    this.ruleNominator = requireNonNull(ruleNominator);
    this.ruleDetector = requireNonNull(ruleDetector);
    this.namingScheme = requireNonNull(namingScheme);
    this.valueSinkFactory = requireNonNull(valueSinkFactory);
    this.valueDeserializerFactory = requireNonNull(valueDeserializerFactory);
  }


  @Override
  public <T> RootCommand<T> scan(Class<T> clazz) {
    List<WalkedClass<? extends T>> walkedClasses = walkStep(clazz);

    List<PreparedClass<? extends T>> bodiedClasses = prepareStep(walkedClasses);

    RootCommand<T> gatheredClasses = gatherStep(bodiedClasses);

    return gatheredClasses;
  }

  protected <T> List<WalkedClass<? extends T>> walkStep(Class<T> clazz) {
    record WalkingClass<T>(Class<? super T> superclazz, Class<T> clazz) {
    }

    List<WalkedClass<? extends T>> result = new ArrayList<>();

    Set<Class<? extends T>> touched = new HashSet<>();
    Queue<WalkingClass<? extends T>> queue = new LinkedList<>();
    queue.add(new WalkingClass<>(null, clazz));
    do {
      WalkingClass<? extends T> current = queue.poll();
      Class<? extends T> currentSuperclazz = (Class) current.superclazz();
      Class<? extends T> currentClazz = (Class) current.clazz();

      if (touched.contains(currentClazz)) {
        // TODO better exception
        throw new IllegalStateException("cycle");
      }

      if (currentSuperclazz != null && !currentSuperclazz.isAssignableFrom(currentClazz)) {
        // TODO better exception
        throw new IllegalStateException(
            "superclazz " + currentSuperclazz + " not assignable from " + currentClazz);
      }

      Configurable configurable = currentClazz.getAnnotation(Configurable.class);
      if (configurable == null) {
        // TODO better exception
        throw new IllegalStateException("no @Configurable on " + current);
      }

      Map<String, Class<? extends T>> subcommands =
          (Map) subCommandScanner.scanForSubCommands(currentClazz).orElseGet(Collections::emptyMap);

      SuperCommand<? extends T> supercommand = null;
      if (currentSuperclazz != null) {
        supercommand = new SuperCommand<>(currentSuperclazz, configurable.discriminator());
      } else {
        supercommand = null;
      }

      result.add(new WalkedClass(Optional.ofNullable(supercommand), currentClazz, configurable));

      for (Class<? extends T> subcommand : subcommands.values()) {
        queue.add(new WalkingClass(currentClazz, subcommand));
      }

      touched.add(currentClazz);
    } while (!queue.isEmpty());

    return result;
  }


  protected <T> List<PreparedClass<? extends T>> prepareStep(
      List<WalkedClass<? extends T>> walkedClasses) {
    List<PreparedClass<? extends T>> result = new ArrayList<>(walkedClasses.size());

    for (WalkedClass<? extends T> walkedClass : walkedClasses) {
      boolean hasSubcommands = walkedClasses.stream().flatMap(wc -> wc.supercommand().stream())
          .anyMatch(sc -> sc.clazz().equals(walkedClass.clazz()));

      CommandBody<? extends T> body;
      if (hasSubcommands) {
        // If a class has subcommands, we don't want to create a body for it. We'll create a body
        // for its subcommands instead.
        body = null;
      } else {
        // If a class doesn't have subcommands, we'll create a body for it.
        Class<?> clazz = walkedClass.clazz();

        List<CandidateSyntax> candidateSyntax = syntaxNominator.nominateSyntax(clazz);

        List<DetectedSyntax> detectedSyntax = new ArrayList<>();
        for (CandidateSyntax csi : candidateSyntax) {
          Optional<DetectedSyntax> maybeDetectedSyntax = syntaxDetector.detectSyntax(clazz, csi);
          if (maybeDetectedSyntax.isPresent()) {
            detectedSyntax.add(maybeDetectedSyntax.get());
          } else {
            // This is fine. Not every candidate is actually syntax.
          }
        }

        List<NamedSyntax> syntax = new ArrayList<>();
        for (DetectedSyntax dsi : detectedSyntax) {
          Optional<String> maybeName = namingScheme.name(dsi.nominated());
          if (maybeName.isPresent()) {
            syntax.add(new NamedSyntax(dsi.nominated(), dsi.genericType(), dsi.annotations(),
                dsi.required(), dsi.coordinates(), maybeName.get()));
          } else {
            // This is not OK. Everything has to be named.
            throw new IllegalStateException("No name for " + dsi.nominated());
          }
        }

        List<CandidateRule> candidateRules = ruleNominator.nominateRules(clazz, syntax);

        List<DetectedRule> detectedRules = new ArrayList<>();
        for (CandidateRule cri : candidateRules) {
          Optional<DetectedRule> maybeDetectedRule = ruleDetector.detectRule(clazz, syntax, cri);
          if (maybeDetectedRule.isPresent()) {
            detectedRules.add(maybeDetectedRule.get());
          } else {
            // This is fine. Not every candidate is actually a rule.
          }
        }

        List<NamedRule> rules = new ArrayList<>();
        for (DetectedRule dri : detectedRules) {
          if (dri.hasConsequent()) {
            // We only name consequents. Antecedents come named. So we only need to name the
            // rule if it has a consequent.
            Optional<String> maybeName = namingScheme.name(dri.nominated());
            if (maybeName.isPresent()) {
              rules.add(new NamedRule(dri.nominated(), dri.genericType(), dri.annotations(),
                  dri.antecedents(), maybeName));
            } else {
              // This is not OK. Everything has to be named.
              throw new IllegalStateException("No name for " + dri.nominated());
            }
          } else {
            // No consequent, no name.
            rules.add(new NamedRule(dri.nominated(), dri.genericType(), dri.annotations(),
                dri.antecedents(), Optional.empty()));
          }
        }

        List<CommandProperty> properties = new ArrayList<>();
        for (NamedSyntax namedSyntax : syntax) {
          final Map<String, String> syntaxMap = namedSyntax.coordinates().entrySet().stream()
              .filter(e -> e.getKey() instanceof String)
              .collect(toMap(e -> (String) e.getKey(), e -> e.getValue()));
          final ValueSink sink = valueSinkFactory
              .getSink(namedSyntax.genericType(), namedSyntax.annotations()).orElseThrow(() -> {
                // TODO better exception
                return new IllegalArgumentException("no sink for " + namedSyntax.genericType());
              });
          final ValueDeserializer<?> deserializer = valueDeserializerFactory
              .getDeserializer(sink.getGenericType(), namedSyntax.annotations()).orElseThrow(() -> {
                // TODO better exception
                return new IllegalArgumentException("no deserializer for " + sink.getGenericType());
              });
          properties
              .add(new CommandProperty(namedSyntax.name(), "", syntaxMap, sink, deserializer));
        }


        body = new CommandBody<>(properties, rules);
      }

      result.add(new PreparedClass(walkedClass.supercommand(), walkedClass.clazz(),
          walkedClass.configurable(), Optional.ofNullable(body)));
    }

    return result;
  }

  public <T> RootCommand<T> gatherStep(List<PreparedClass<? extends T>> bodiedClasses) {
    // We need to visit our classes in dependency order. That is, we want to process a node only
    // after all of its dependencies have been processed. (So, leaves first, root last.) We will
    // need a dependency graph to compute that, so let's build it here.
    Map<Class<?>, Set<Class<?>>> shallowDependencies = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        SuperCommand<?> supercommand = bodiedClass.supercommand().get();
        shallowDependencies.computeIfAbsent(supercommand.clazz(), k -> new HashSet<>())
            .add(bodiedClass.clazz());
      }
    }

    // Now compute the topological ordering of our graph. In other words, we want to sort our
    // classes
    // in dependency order. We want to process a node only after all of its dependencies have been
    // processed. So leaves first, root last.
    List<Class<?>> sortedClasses = new ArrayList<>(shallowDependencies.keySet());
    sortedClasses.sort(Graphs.topologicalOrdering(shallowDependencies));


    // Let's gather our discriminators
    Map<Class<?>, String> discriminators = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        discriminators.put(bodiedClass.clazz(), bodiedClass.supercommand().get().discriminator());
      }
    }

    // Now let's create our nodes in the order we just sorted them. This will ensure that we
    // process a node only after all of its dependencies have been processed.
    List<RootCommand<?>> roots = new ArrayList<>();
    Map<Class<?>, SubCommand<?>> subcommands = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
      Class<?> clazz = bodiedClass.clazz();

      Set<Class<?>> deps = shallowDependencies.get(clazz);
      if (deps == null) {
        throw new AssertionError("no dependencies for " + clazz);
      }

      Map<String, SubCommand<?>> subs = new HashMap<>();
      for (Class<?> dep : deps) {
        SubCommand<?> sub = subcommands.get(dep);
        if (sub == null) {
          throw new AssertionError("no subcommand for " + dep);
        }

        String discriminator = discriminators.get(dep);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + dep);
        }

        subs.put(discriminator, sub);
      }


      if (bodiedClass.supercommand().isPresent()) {
        // This is a subcommand
        String discriminator = discriminators.get(clazz);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + clazz);
        }

        subcommands.put(clazz, new SubCommand(clazz, discriminator,
            bodiedClass.configurable().description(), bodiedClass.body().orElse(null), subs));
      } else {
        // This is a root command
        roots.add(new RootCommand(clazz, bodiedClass.configurable().name(),
            bodiedClass.configurable().version(), bodiedClass.configurable().description(),
            bodiedClass.body().orElse(null), subs));
      }
    }

    if (roots.size() != 1) {
      throw new AssertionError("expected exactly one root command, got " + roots);
    }

    return (RootCommand<T>) roots.get(0);
  }

}
