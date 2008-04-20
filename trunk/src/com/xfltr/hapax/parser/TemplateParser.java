package com.xfltr.hapax.parser;

import java.util.List;

/**
 * Interface that must be implemented by any template parsers.
 *
 * @author dcoker
 */
public interface TemplateParser {
  List<TemplateNode> parse(String template) throws TemplateParserException;
}
