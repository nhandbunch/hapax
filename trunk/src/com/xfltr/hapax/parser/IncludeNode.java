package com.xfltr.hapax.parser;

import com.xfltr.hapax.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Represents an {{> include token.
 *
 * @author dcoker
 */
public class IncludeNode extends TemplateNode {

  private final String includeName;
  private final List<Modifiers.FLAGS> modifiers;

  private IncludeNode(String includeName, List<Modifiers.FLAGS> modifiers) {
    this.includeName = includeName;
    this.modifiers = modifiers;
  }

  public static IncludeNode parse(String spec) {
    String split[] = spec.split(":");
    return new IncludeNode(split[0], Modifiers.parseModifiers(split));
  }

  @Override
  public void evaluate(TemplateDictionary dict,
                       TemplateLoaderContext context, PrintWriter collector)
      throws TemplateException {
    String include_name = includeName;

    // The filename is stored as a standard variable.
    String filename = dict.get(include_name);
    if (filename == null) {
      throw new TemplateException(
          "The template identifier for included section "
              + include_name + " is not set!");
    }

    Template incl_tmpl = context.getLoader().getTemplate(filename,
        context.getTemplateDirectory());
    incl_tmpl.setLoaderContext(context);

    // If the included section has modifiers applied, we buffer it into a
    // string so that we can apply the modifiers.
    PrintWriter previous_printwriter = null;
    StringWriter sw = null;
    if (modifiers.size() > 0) {
      previous_printwriter = collector;
      sw = new StringWriter();
      collector = new PrintWriter(sw);
    }

    List<TemplateDictionary> child_dicts = dict.getChildDicts(include_name);
    if (child_dicts.size() == 0) {
      incl_tmpl.render(dict, collector);
    } else {
      for (TemplateDictionary subdict : child_dicts) {
        incl_tmpl.render(subdict, collector);
      }
    }

    if (previous_printwriter != null) {
      String results = sw.toString();
      collector = previous_printwriter;
      collector.write(Modifiers.applyModifiers(results, modifiers));
    }
  }
}
