package com.sigpwned.discourse.core.format.help.render.text;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.BlockRenderer;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupBlock;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyAssignment;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.UserMessageLocalizer;
import com.sigpwned.discourse.core.l11n.util.UserMessages;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Text;

public class CommandPropertyGroupBlockTextRenderer implements BlockRenderer {

  @Override
  public boolean renderBlock(Document document, DocumentSection section, Block block,
      PrintStream out, InvocationContext context) throws IOException {
    if (!(block instanceof CommandPropertyGroupBlock groupBlock))
      return false;

    UserMessageLocalizer localizer = context.get(UserMessageLocalizer.class).orElseThrow();

    Dialect dialect = context.get(Dialect.class).orElseThrow();

    ArgFormatter formatter = dialect.newArgFormatter();

    for (CommandPropertyDescription description : groupBlock.getDescriptions()) {
      for (CommandPropertyAssignment syntax : description.getAssignments()) {
        // TODO format these...
        // -f
        // --foo
        // --foo=<true|false>
        // -f <int>
        // --foo <int>
        // <foobar>
        // TODO We're not getting the right type name here
        // TODO Should we couple the Args layer to PlannedCommand?
        List<List<String>> parts = formatter
            .formatArg(description.getProperty(), syntax.getCoordinate()).orElse(emptyList());
        out.println(parts.stream().map(xs -> String.join(" ", xs)).collect(joining(", ")));
      }

      for (UserMessage message : description.getDescriptions()) {
        UserMessage localized = localizer.localizeUserMessage(message, context);

        String rendered = UserMessages.render(localized);

        List<String> wrapped = Text.wrap(rendered, 80 - 8);

        for (String line : wrapped) {
          out.println(Text.indent(line, 8));
        }

        out.println();
      }
    }

    return true;
  }

}
