package com.xfltr.hapax.parser;

import com.xfltr.hapax.TemplateException;

/**
 * An exception thrown by a TemplateParser.
 *
 * @author dcoker
 */
public class TemplateParserException extends TemplateException {
  public TemplateParserException(String details) {
    super(details);
  }
}
