package com.xfltr.hapax.parser;

import com.xfltr.hapax.TemplateException;
import junit.framework.TestCase;

/**
 * Unit test for CTemplateParser.
 *
 * @author dcoker
 */
public class CTemplateParserTest extends TestCase {
  private final String[] failing_cases = {
      "{{ malformed variable }",
      "{{openbutnotclosed",
      "{{open with bad characters",
      "Colon but no modifier {{VARIABLE:}}",
      "Trailing {{",
      "}} waffles {{",
      "trailing open section {{#",
      "trailing close section {{/",
      "trailing include {{>",
      "{{#joined{{#section",
      "{{#",
      "{{/",
      "{{>",
      "bad section names {{#R0F_C0PTERS!}}{{/ROF_C0PTERS!}}"
  };

  public void testMalformedTemplateThrowsException() {
    for (String failing_case : failing_cases) {
      TemplateParser tp = CTemplateParser.create();
      try {
        tp.parse(failing_case);
        fail("malformed template should throw an exception: '" + failing_case +
            "'");
      } catch (TemplateException e) {
        assertTrue(e instanceof TemplateParserException);
      }
    }
  }

  public void testParsesIncludes() throws TemplateException {
    assertExpectedNodeListLength("{{>INCL}}", 1);
  }

  public void testTemplateParsesIntoCorrectNumberOfNodes1()
      throws TemplateException {
    assertExpectedNodeListLength("{{VAR}}", 1);
    assertExpectedNodeListLength("{{VAR}} ", 2);
    assertExpectedNodeListLength(" {{VAR}}", 2);
    assertExpectedNodeListLength(" {{VAR}} ", 3);
    assertExpectedNodeListLength("{{VAR}}{{#SECTION}}", 2);
    assertExpectedNodeListLength("{{VAR}}{{#SECTION}}{{/SECTION}}", 3);
    assertExpectedNodeListLength("{{VAR}}{{#SECTION}}{{/SECTION}}end", 4);
  }

  /**
   * Helper method for comparing the length of the parsed template to the number
   * of expected nodes.
   */
  private void assertExpectedNodeListLength(String input, int expected_length)
      throws TemplateParserException {
    CTemplateParser tp = CTemplateParser.create();
    assertEquals(expected_length, tp.parse(input).size());
  }
}
