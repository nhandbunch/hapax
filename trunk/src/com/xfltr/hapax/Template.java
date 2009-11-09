package com.xfltr.hapax;

import com.xfltr.hapax.parser.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

/**
 * Template executes the program defined by the tmpl_ list.  The list itself is
 * constructed by implementations of {@link TemplateParser}.
 *
 * Instead of constructing a Template directly, use an implementation of {@link
 * TemplateLoader} such as {@link TemplateCache}.
 *
 * @author dcoker
 */
public final class Template {
  private static final Logger logger_ =
      Logger.getLogger(Template.class.getSimpleName());

  private final List<TemplateNode> tmpl_;
  private TemplateLoader loader_ = new NullTemplateLoader();
  private TemplateLoaderContext context_ =
      new TemplateLoaderContext(loader_, null);

  /**
   * Constructs a Template from the given CTemplate string.
   */
  public static Template parse(String template) throws TemplateParserException {
    return parse(CTemplateParser.create(), template);
  }

  /**
   * Constructs a Template from the given string, using the specified parser.
   */
  public static Template parse(TemplateParser parser, String template)
      throws TemplateParserException {
    List<TemplateNode> results = parser.parse(template);
    return new Template(results);
  }

  public void setLoaderContext(TemplateLoaderContext context) {
    this.context_ = context;
    this.loader_ = context.getLoader();
  }

  public void setLoader(TemplateLoader loader) {
    this.context_ = new TemplateLoaderContext(loader, null);
    this.loader_ = context_.getLoader();
  }

  public String renderToString(final TemplateDictionary td)
      throws TemplateException {
    return renderToString(tmpl_, td);
  }

  private Template(List<TemplateNode> tmpl) {
    this.tmpl_ = tmpl;
  }

  private void render(List<TemplateNode> list,
                      final TemplateDictionary td,
                      final PrintWriter pw)
      throws TemplateException {
    // TODO: delegate these to the nodes, per evaluate.
    for (int i = 0; i < list.size(); i++) {
      TemplateNode node = list.get(i);
      if (node instanceof SectionNode) {
        i = handleSectionNode(list, td, i, pw);
      } else if (node instanceof EztDefineNode) {
        i = handleEztDefineNode(list, td, i);
      } else if (node instanceof EztConditionalNode) {
        i = handleEztConditionalNode(list, td, i, pw);
      } else {
        node.evaluate(td, context_, pw);
      }
    }
  }

  private int handleEztConditionalNode(List<TemplateNode> list,
                                       TemplateDictionary td,
                                       int is_node_idx, PrintWriter pw)
      throws TemplateException {
    EztConditionalNode ecn = (EztConditionalNode) list.get(is_node_idx);
    Range range = ecn.advise(list, is_node_idx, td);
    List<TemplateNode> view = list.subList(range.getStart(), range.getStop());
    render(view, td, pw);
    return range.getSkipTo();
  }

  private int handleEztDefineNode(List<TemplateNode> list,
                                  TemplateDictionary td, int define_node_idx)
      throws TemplateException {
    // TODO: the behavior of this method and handleEztConditionalNode are very
    // similar; they can probably be merged elegantly.

    EztDefineNode edn = (EztDefineNode) list.get(define_node_idx);
    String var_name = edn.getVariableName();

    Range range = edn.advise(list, define_node_idx);
    List<TemplateNode> view = list.subList(range.getStart(), range.getStop());
    StringWriter sw = new StringWriter();
    render(view, td, new PrintWriter(sw));

    String new_value = sw.toString();
    td.put(var_name, new_value);

    return range.getSkipTo();
  }

  public void render(TemplateDictionary td, PrintWriter printWriter)
      throws TemplateException {
    render(tmpl_, td, printWriter);
  }

  private int handleSectionNode(final List<TemplateNode> list,
                                final TemplateDictionary td,
                                final int open_tag_idx,
                                final PrintWriter collector)
      throws TemplateException {
    SectionNode sn = (SectionNode) list.get(open_tag_idx);

    String new_section_name = sn.getSectionName();
    // scan forward for the {{/CLOSE}} tag
    int p = open_tag_idx + 1;
    // keep track of the number of other open section tags we've encountered
    // so that we can throw an error if there is a mismatch
    int other_sections = 0;
    for (; p < list.size(); ++p) {
      TemplateNode tp = list.get(p);
      if (tp instanceof SectionNode) {
        SectionNode maybe_close_tag = (SectionNode) tp;
        if (maybe_close_tag.isOpenSectionTag()) {
          other_sections++;
        }
        if (maybe_close_tag.isCloseSectionTag()) {
          if (maybe_close_tag.getSectionName().equals(sn.getSectionName())) {
            // We found the matching close tag.
            break;
          } else {
            if (other_sections == 0) {
              String msg = MessageFormat.format(
                  "mismatched close tag: expecting a close tag for {0}, " +
                      "but got close tag for {1}",
                  sn.getSectionName(),
                  maybe_close_tag.getSectionName());
              logger_.warning(msg);
              throw new TemplateException(msg);
            } else {
              other_sections--;
            }
          }
        }
      }
    }

    // If there was no close tag, then the template is broken.
    if (p == list.size()) {
      throw new TemplateException(
          "missing close tag for " + sn.getSectionName());
    }

    // p now points to the close node.

    // If this section is hidden, we don't render the intermediate nodes.
    if (td.isHiddenSection(sn.getSectionName())) {
      logger_.warning("Skipping section " + sn.getSectionName() +
          " because it is hidden");
      return p;
    }

    List<TemplateDictionary> subdicts = td.getChildDicts(new_section_name);

    // The presence of child dictionaries indicates that this section is repeated.
    // If there are no child dicts of the same name, then we display only once.
    if (subdicts.size() == 0) {
      render(list.subList(open_tag_idx + 1, p), td, collector);
    } else {
      for (TemplateDictionary subdict : subdicts) {
        render(list.subList(open_tag_idx + 1, p), subdict, collector);
      }
    }

    return p;
  }

  private String renderToString(final List<TemplateNode> list,
                                final TemplateDictionary td)
      throws TemplateException {
    StringWriter k = new StringWriter();
    PrintWriter pw = new PrintWriter(k);
    render(list, td, pw);
    pw.flush();
    return k.toString();
  }
}
