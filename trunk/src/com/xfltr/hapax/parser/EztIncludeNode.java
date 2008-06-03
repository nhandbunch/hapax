package com.xfltr.hapax.parser;

import com.xfltr.hapax.PathUtil;
import com.xfltr.hapax.Template;
import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateException;
import com.xfltr.hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Node for the [include] tag.
 *
 * @author dcoker
 */
public class EztIncludeNode extends TemplateNode {

  private final String variableName_;

  private EztIncludeNode(String s) {
    this.variableName_ = s;
  }

  public static TemplateNode parse(String s) {
    return new EztIncludeNode(s);
  }

  @Override
  public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                       PrintWriter collector) throws TemplateException {

    String include_filename;
    if (variableName_.startsWith("\"")) {
      // path to a file
      include_filename = variableName_.replaceAll("\"", "");
    } else {
      // indirect reference to a variable
      include_filename = dict.get(variableName_);
    }

    // TODO: double no-pony hack.
    //
    // If we see a path that starts with "/", we load the template from the base
    // directory given to the template loader.
    //
    // If we see a path that starts with "/html", treat the but-first components
    // as paths originating at the root of the
    // template directory rather than relative to the current template's path.
    //
    // In both of these cases, we ignore the path of the current template.
    //
    final Template template;
    String search_filename;
    if (PathUtil.isAbsolute(include_filename)) {
      // TODO: "/html" is an exceptional case.
      include_filename =
          PathUtil.join("/", PathUtil.makeRelative("/html", include_filename));
      template = context.getLoader().getTemplate(include_filename);
      search_filename = include_filename;
    } else {
      // TODO: design-pattern-needed hack.  Context should know how to get this
      // template.
      template = context.getLoader().getTemplate(include_filename,
                                                 context.getTemplateDirectory());
      if (context.getTemplateDirectory() != null) {
        search_filename =
            PathUtil.join(context.getTemplateDirectory(), include_filename);
      } else {
        search_filename =
            PathUtil.join("", include_filename);
      }
    }

    // Create an entry in the data dictionary for every template we've processed
    // already.  This allows us to detect infinite loops.
    String warning_flag = "__already__included__" + search_filename;
    if (dict.contains(warning_flag)) {
      throw new CyclicIncludeException(
          "Cyclic include loop detected: " + search_filename
          + " has been included multiple times.");
    } else {
      dict.put(warning_flag, "");
    }

    template.render(dict, collector);
  }

}