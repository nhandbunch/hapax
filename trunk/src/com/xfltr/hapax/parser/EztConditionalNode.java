package com.xfltr.hapax.parser;

import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateException;
import com.xfltr.hapax.TemplateLoaderContext;

import java.io.PrintWriter;
import java.util.List;

/**
 * A conditional EZT node: either [is or [if-any.
 *
 * @author dcoker
 */
public class EztConditionalNode extends TemplateNode {
  private interface Behavior {
    public boolean trueBranch(TemplateDictionary td);
  }

  private final Behavior behavior;

  private EztConditionalNode(Behavior b) {
    behavior = b;
  }

  /**
   * Construct a node that evaluates the [if-any] logic.
   */
  public static EztConditionalNode ifAny(final String varname) {
    return new EztConditionalNode(new Behavior() {
      public boolean trueBranch(TemplateDictionary td) {
        return td.contains(varname) && td.get(varname).length() > 0;
      }
    });
  }

  /**
   * Construct a node that evaluates the [is x "y"] logic.
   */
  public static EztConditionalNode is(final String varname,
                                      final String expected) {
    return new EztConditionalNode(new Behavior() {
      public boolean trueBranch(TemplateDictionary td) {
        return td.contains(varname) && td.get(varname).equals(expected);
      }
    });
  }

  public Range advise(List<TemplateNode> nodes,
                      int is_node_idx,
                      TemplateDictionary dict) throws TemplateException {
    int other_sections = 0;
    int end_node = -1;
    int else_node = -1;

    for (int i = is_node_idx; i < nodes.size(); i++) {
      final TemplateNode node = nodes.get(i);
      if (node instanceof EztConditionalNode) {
        other_sections++;
      } else if (node instanceof EztElseNode && other_sections == 1) {
        else_node = i;
      } else if (node instanceof EztEndNode) {
        other_sections--;
        if (other_sections == 0) {
          end_node = i;
          break; // we can stop now because we've found the matching [end]
        }
      }
    }

    if (end_node == -1) {
      throw new TemplateException("Unable to find matching [end] node.");
    }

    if (behavior.trueBranch(dict)) {
      if (else_node == -1) {
        // no [else]
        return new Range(is_node_idx + 1, end_node, end_node);
      } else {
        // there is an [else]
        return new Range(is_node_idx + 1, else_node, end_node);
      }
    } else {
      if (else_node == -1) {
        // no [else]
        return new Range(end_node, end_node, end_node);
      } else {
        return new Range(else_node, end_node, end_node);
      }
    }
  }

  @Override
  public void evaluate(TemplateDictionary dict,
                       TemplateLoaderContext context, PrintWriter collector)
      throws TemplateException {
    // do nothing
  }
}
