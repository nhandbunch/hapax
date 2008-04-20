package com.xfltr.hapax.parser;

import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateException;
import com.xfltr.hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Node for the [end] tag.
 *
 * @author dcoker
 */
public class EztEndNode extends TemplateNode {

  private EztEndNode() {
  }

  @Override public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                       PrintWriter collector) throws TemplateException {
    // do nothing
  }

  public static EztEndNode create() {
    return new EztEndNode();
  }
}