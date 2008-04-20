package com.xfltr.hapax.parser;

import com.xfltr.hapax.Template;
import junit.framework.TestCase;

import java.util.List;
import java.util.logging.Logger;

/**
 * Unit test for EztParser.
 *
 * @author dcoker
 */
public class EztParserTest extends TestCase {
  private static final Logger logger =
      Logger.getLogger(EztParserTest.class.getSimpleName());

  private static final String[][] GOOD_DATA = {
      {"[title]", "1"},
      {"[define title]Issue[end]", "3"},
      {"[define title]Issue[end][title]", "4"},
      {"[is title \"Title\"]Issue[end]", "3"},
      {"[is title \"No title\"]Issue[end]", "3"},
      {"[define title][end][is title \"No title\"]Issue[end]", "5"},
      {"[define title]Issue[end][is title \"Issue\"]Issue[end]", "6"},
      {"[if-any title]Issue[end]", "3"},
      {"[if-any title]Issue[else]No issue[end]", "5"},
      {"[define title][end][if-any title]Issue[end]", "5"},
      {"[define title][end][if-any title]Issue[else]No issue[end]", "7"},
      {"[define  whitespace ][end][if-any whitespace]Issue[else]No issue[end]", "7"},
  };

  private static String[] PARSE_FAILURES = {
      "[if-any]",
      "[define]",
      "[include]",
      "[insertfile]",
      "[is]",
      "[is X]",
  };

  public void testTemplatesParsedIntoExpectedLengths()
      throws TemplateParserException {
    for (String[] strings : GOOD_DATA) {
      String template = strings[0];
      String expected_length = strings[1];

      final List<TemplateNode> list = EztParser.create().parse(template);
      logger.info("template = " + template);
      for (TemplateNode templateNode : list) {
        logger.info(templateNode.getClass().getSimpleName());
      }
      assertEquals(template, Integer.valueOf(expected_length).intValue(),
          list.size());
    }
  }

  public void testParseFailures() {
    for (String template : PARSE_FAILURES) {
      try {
        Template.parse(EztParser.create(), template);
        fail("Template '" + template +
            "' should have thrown TemplateParserException.");
      } catch (TemplateParserException e) {
        // pass
      }
    }
  }

  public void testParserGeneratesExpectedNodes()
      throws TemplateParserException {
    assertEquals(EztEndNode.class,
        EztParser.create().parse("[end]").get(0).getClass());
    assertEquals(EztConditionalNode.class,
        EztParser.create().parse("[if-any monkey]").get(0).getClass());
    assertEquals(EztConditionalNode.class,
        EztParser.create().parse("[is x \"yz\"]").get(0).getClass());
    assertEquals(EztDefineNode.class,
        EztParser.create().parse("[define monkey]").get(0).getClass());
    EztParser eztParser = EztParser.create();
    List<TemplateNode> list = eztParser.parse("[include \"hello\"]");
    assertEquals(EztIncludeNode.class,
        list.get(0).getClass());
    assertEquals(EztIncludeNode.class,
        EztParser.create().parse("[include var]").get(0).getClass());
    assertEquals(EztIncludeNode.class,
        EztParser.create().parse("[insertfile var]").get(0).getClass());
  }
}
