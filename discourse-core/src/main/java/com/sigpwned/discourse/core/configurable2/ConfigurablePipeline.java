package com.sigpwned.discourse.core.configurable2;

import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHole;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleCandidate;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleFactory;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleNominator;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntax;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxCandidate;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxFactory;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxNominator;
import com.sigpwned.discourse.core.configurable2.syntax.NamedConfigurableSyntax;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigurablePipeline {

  private final ConfigurableSyntaxNominator syntaxNominator;
  private final ConfigurableSyntaxFactory syntaxFactory;
  private final ConfigurableAttributeNamingScheme attributeNamingScheme;
  private final ConfigurableHoleNominator holeNominator;
  private final ConfigurableHoleFactory holeFactory;

  public ConfigurablePipeline(ConfigurableHoleNominator nominator,
      ConfigurableAttributeNamingScheme namingScheme) {
    this.nominator = nominator;
    this.namingScheme = namingScheme;
  }

  public void process(Class<?> clazz, List<String> args) {
    List<ConfigurableSyntaxCandidate> syntaxCandidates = syntaxNominator.nominateSyntax(clazz);

    List<ConfigurableSyntax> syntaxes = syntaxCandidates.stream()
        .flatMap(candidate -> syntaxFactory.createSyntax(candidate).stream()).toList();

    List<NamedConfigurableSyntax> nameds = syntaxes.stream().map(
        syntax -> attributeNamingScheme.nameAttribute(syntax).map(
            name -> new NamedConfigurableSyntax(syntax.nominated(), syntax.genericType(),
                syntax.annotations(), syntax.coordinates(), name)).orElseThrow(() -> {
          // TODO better exception
          return new RuntimeException("Failed to name attribute");
        })).toList();

    // These probably should not be "pegs". They are more like "peg types" or something. These
    // are really just used to resolve the names of the holes.
    List<Peg> pegs = new ArrayList<>();
    for (NamedConfigurableSyntax named : nameds) {
      pegs.add(new Peg(named.name(), named.nominated(), Optional.empty()));
    }

    List<ConfigurableHoleCandidate> holeCandidates = holeNominator.nominateHoles(clazz);

    boolean addedHole;
    List<ConfigurableHole> holes = new ArrayList<>();
    do {
      addedHole = false;
      Iterator<ConfigurableHoleCandidate> iterator = holeCandidates.iterator();
      while (iterator.hasNext()) {
        ConfigurableHoleCandidate candidate = iterator.next();
        Optional<ConfigurableHole> maybeHole = holeFactory.createHole(candidate, pegs);
        if (maybeHole.isPresent()) {
          ConfigurableHole hole = maybeHole.orElseThrow();
          holes.add(hole);
          if (hole.consequent().isPresent()) {
            pegs.add(hole.consequent().get());
          }
          iterator.remove();
          addedHole = true;
        }
      }
    } while (addedHole);

    // new PhasePipeline().go(args);
    Map<String, Object> parsed = null;

    Map<String, Peg> thepegs = parsed.entrySet().stream()
        .map(e -> new Peg(e.getKey(), e.getValue(), Optional.empty()))
        .collect(Collectors.toMap(Peg::name, Function.identity(), (a, b) -> {
          // TODO better exception
          throw new RuntimeException("Duplicate peg: " + a);
        }, HashMap::new));

    for (ConfigurableHole hole : holes) {
      if (thepegs.keySet().containsAll(hole.antecedents())) {
        Map<String, Peg> holePegs = new HashMap<>(hole.antecedents().size());
        for (String antecedent : hole.antecedents()) {
          holePegs.put(antecedent, thepegs.get(antecedent));
        }

        // Now... how do we democratize the hole fillers? Just return boolean?


      } if (hole.consequent().isPresent()) {
        Peg peg = hole.consequent().get();
        pegs.put(peg.name(), peg.nominated());
      }
    }

    List<ConfigurableHoleCandidate> holeCandidates = holeNominator.nominateHoles(clazz);
    List<Peg> pegs = new ArrayList<>();
    for (ConfigurableHoleCandidate candidate : candidates) {
      Optional<Object> value = Optional.empty();
      Peg peg = new Peg(candidate.getName(), candidate.getNominated(), value);
      pegs.add(peg);
    }
    Optional<String> name = this.attributeNamingScheme.nameAttribute(syntax);
    return this.syntaxFactory.createSyntax(name, pegs);
  }
}
