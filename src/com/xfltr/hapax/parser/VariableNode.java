package com.xfltr.hapax.parser;

import com.xfltr.hapax.Modifiers;
import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateLoaderContext;

import java.io.PrintWriter;
import java.util.List;

/**
 * Represents a node whose output is defined by a value from the
 * TemplateDictionary.
 *
 * This supports both {{PLAIN}} variables as well as one with {{MODIFERS:j}}.
 * The modifiers themselves are implemented in {@link Modifiers}.
 *
 * @author dcoker
 */
public class VariableNode extends TemplateNode {
  private final String variable;

  private final List<Modifiers.FLAGS> modifiers;

  private VariableNode(String variable, List<Modifiers.FLAGS> modifiers) {
    this.variable = variable;
    this.modifiers = modifiers;
  }

  @Override
  public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                       PrintWriter collector) {
    String t = dict.get(variable);
    if (!dict.contains(variable)) {
      // Variable is not in the dictionary; default to empty string.
      t = "";
    } else {
      t = Modifiers.applyModifiers(t, modifiers);
    }
    collector.write(t);
  }

  public static VariableNode parse(String spec) {
    String split[] = spec.split(":");
    List<Modifiers.FLAGS> modifiers = Modifiers.parseModifiers(split);

    return new VariableNode(split[0], modifiers);
  }

}
