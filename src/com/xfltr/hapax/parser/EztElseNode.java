package com.xfltr.hapax.parser;

import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateException;
import com.xfltr.hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Node for the [else] tag.
 *
 * @author dcoker
 */
public class EztElseNode extends TemplateNode {

  private EztElseNode() {
  }

  @Override
  public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                       PrintWriter collector) throws TemplateException {
    // do nothing
  }

  public static EztElseNode create() {
    return new EztElseNode();
  }
}