package com.sigpwned.discourse.core.format.help.parameters;

import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import com.sigpwned.discourse.core.document.Doc;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.DocumentSectionRenderer;
import com.sigpwned.discourse.core.document.render.text.TextViewport;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Text;

public class TextParameterGroupDocumentSectionRenderer implements DocumentSectionRenderer {

  @Override
  public boolean renderDocumentSection(Doc document, DocumentSection section, PrintStream out,
      InvocationContext context) throws IOException {
    if (!(section instanceof ParameterGroupDocumentSection parameterGroupSection))
      return false;

    TextViewport viewport = context.get(TextViewport.class).orElse(TextViewport.DEFAULT);

    for (ParameterGroupEntry entry : parameterGroupSection.getEntries()) {
      List<String> syntaxLines =
          Text.wrap(String.join(", ", entry.getSyntax()), viewport.getWidth() - 4);
      for (String syntaxLine : syntaxLines) {
        out.print("    ");
        out.print(syntaxLine);
        out.println();
      }

      List<List<String>> descriptionsLines = entry.getDescription().stream()
          .map(d -> Text.wrap(d, viewport.getWidth() - 8)).collect(toList());
      for (List<String> descriptionLines : descriptionsLines) {
        for (String descriptionLine : descriptionLines) {
          out.print("        ");
          out.println(descriptionLine);
        }
        out.println();
      }
    }

    return true;
  }
}
