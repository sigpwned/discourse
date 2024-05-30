package com.sigpwned.discourse.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.DiscourseMixin;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.module.CoreModule;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineBuilder;

public class MixinTest {
  public static class MixinBravo {
    @OptionParameter(shortName = "b", longName = "bravo", description = "bravo")
    public String bravoValue;

    @Override
    public int hashCode() {
      return Objects.hash(bravoValue);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MixinBravo other = (MixinBravo) obj;
      return Objects.equals(bravoValue, other.bravoValue);
    }
  }

  public static class MixinAlpha {
    @OptionParameter(shortName = "a", longName = "alpha", description = "alpha")
    public String alphaValue;

    @DiscourseMixin
    public MixinBravo bravo;

    @Override
    public int hashCode() {
      return Objects.hash(alphaValue, bravo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MixinAlpha other = (MixinAlpha) obj;
      return Objects.equals(alphaValue, other.alphaValue) && Objects.equals(bravo, other.bravo);
    }
  }

  @Configurable(name = "mixin", description = "mixin test")
  public static class MixinTestConfigurable {
    @OptionParameter(shortName = "f", longName = "foo", description = "foo")
    public String foo;

    @PositionalParameter(position = 0, description = "bar")
    public int bar;

    @DiscourseMixin
    public MixinAlpha alpha;

    @Override
    public int hashCode() {
      return Objects.hash(bar, foo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MixinTestConfigurable other = (MixinTestConfigurable) obj;
      return bar == other.bar && Objects.equals(foo, other.foo);
    }
  }

  @Test
  public void givenAConfigurableClassWithMixin_whenInvokeWithAlphaBravoMixinParams_thenAlphaBravoMixinsPresent() {
    MixinTestConfigurable instance =
        new InvocationPipelineBuilder().register(new CoreModule()).build().invoke(
            MixinTestConfigurable.class, List.of("-a", "alpha", "-b", "bravo", "-f", "foo", "42"));

    MixinBravo bravo = new MixinBravo();
    bravo.bravoValue = "bravo";

    MixinAlpha alpha = new MixinAlpha();
    alpha.alphaValue = "alpha";
    alpha.bravo = bravo;

    MixinTestConfigurable expectedInstance = new MixinTestConfigurable();
    expectedInstance.foo = "foo";
    expectedInstance.bar = 42;
    expectedInstance.alpha = alpha;


    assertThat(instance, is(expectedInstance));
  }

  @Test
  public void givenAConfigurableClassWithMixin_whenInvokeWithBravoMixinParams_thenAlphaBravoMixinsPresent() {
    MixinTestConfigurable instance = new InvocationPipelineBuilder().register(new CoreModule())
        .build().invoke(MixinTestConfigurable.class, List.of("-b", "bravo", "-f", "foo", "42"));

    MixinBravo bravo = new MixinBravo();
    bravo.bravoValue = "bravo";

    MixinAlpha alpha = new MixinAlpha();
    alpha.bravo = bravo;

    MixinTestConfigurable expectedInstance = new MixinTestConfigurable();
    expectedInstance.foo = "foo";
    expectedInstance.bar = 42;
    expectedInstance.alpha = alpha;

    assertThat(instance, is(expectedInstance));
  }

  @Test
  public void givenAConfigurableClassWithMixin_whenInvokeWithAlphaMixinParams_thenAlphaMixinPresent() {
    MixinTestConfigurable instance = new InvocationPipelineBuilder().register(new CoreModule())
        .build().invoke(MixinTestConfigurable.class, List.of("-a", "alpha", "-f", "foo", "42"));

    MixinAlpha alpha = new MixinAlpha();
    alpha.alphaValue = "alpha";

    MixinTestConfigurable expectedInstance = new MixinTestConfigurable();
    expectedInstance.foo = "foo";
    expectedInstance.bar = 42;
    expectedInstance.alpha = alpha;

    assertThat(instance, is(expectedInstance));
  }

  @Test
  public void givenAConfigurableClassWithMixin_whenInvokeWithoutMixinParams_thenMixinsAbsent() {
    MixinTestConfigurable instance = new InvocationPipelineBuilder().register(new CoreModule())
        .build().invoke(MixinTestConfigurable.class, List.of("-f", "foo", "42"));

    MixinTestConfigurable expectedInstance = new MixinTestConfigurable();
    expectedInstance.foo = "foo";
    expectedInstance.bar = 42;

    assertThat(instance, is(expectedInstance));
  }
}
