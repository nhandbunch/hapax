package com.xfltr.hapax.parser;

import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateException;
import com.xfltr.hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * All tokens in the template language are represented by instances of a
 * TemplateNode.
 */
public abstract class TemplateNode {
  public abstract void evaluate(TemplateDictionary dict,
                                TemplateLoaderContext context,
                                PrintWriter collector) throws TemplateException;
}