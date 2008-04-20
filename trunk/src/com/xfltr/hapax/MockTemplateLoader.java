package com.xfltr.hapax;

import com.xfltr.hapax.parser.CTemplateParser;
import com.xfltr.hapax.parser.TemplateParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a mock TemplateLoader that can be populated from unit tests.
 *
 * @author dcoker
 */
class MockTemplateLoader implements TemplateLoader {
  private final Map<String, String> mock_templates =
    new HashMap<String, String>();
  private final TemplateParser parser_;

  public MockTemplateLoader() {
    this.parser_ = CTemplateParser.create();
  }

  public MockTemplateLoader(TemplateParser parser) {
    this.parser_ = parser;
  }

  public void put(String name, String template) {
    mock_templates.put(name, template);
  }

  public Template getTemplate(String filename) throws TemplateException {
    Template template = Template.parse(parser_, mock_templates.get(filename));
    template.setLoader(this);
    return template;
  }

  public Template getTemplate(String filename, String templateDirectory)
      throws TemplateException {
    return getTemplate(filename);
  }
}
