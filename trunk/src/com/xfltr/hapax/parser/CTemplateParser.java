package com.xfltr.hapax.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser turns strings containing the contents of a template into a list
 * of TemplateNodes.
 *
 * @author dcoker
 */
public class CTemplateParser implements TemplateParser {
  private static final Logger logger =
      Logger.getLogger(CTemplateParser.class.getSimpleName());

  private static final int RE_FLAGS =
      Pattern.CASE_INSENSITIVE | Pattern.MULTILINE;

  private static final String OPEN_SQUIGGLE = Pattern.quote("{{");

  private static final String CLOSE_SQUIGGLE = Pattern.quote("}}");

  private static final String VARIABLE_RE = "([a-zA-Z_]+(:[a-zA-Z]+)*)*";

  private final Pattern RE_OPEN_SECTION =
      Pattern
          .compile(OPEN_SQUIGGLE + "#([a-zA-Z_]+)" + CLOSE_SQUIGGLE, RE_FLAGS);

  private final Pattern RE_CLOSE_SECTION =
      Pattern
          .compile(OPEN_SQUIGGLE + "/([a-zA-Z_]+)" + CLOSE_SQUIGGLE, RE_FLAGS);

  private final Pattern RE_VARIABLE =
      Pattern.compile(OPEN_SQUIGGLE + VARIABLE_RE + CLOSE_SQUIGGLE, RE_FLAGS);

  private final Pattern RE_INCLUDE =
      Pattern
          .compile(OPEN_SQUIGGLE + ">" + VARIABLE_RE + CLOSE_SQUIGGLE,
              RE_FLAGS);

  private StringBuilder input_;

  private final List<TemplateNode> nodeList_ =
      new LinkedList<TemplateNode>();

  private enum NODE_TYPE {
    OPEN_SECTION,
    CLOSE_SECTION,
    VARIABLE,
    TEXT_NODE,
    INCLUDE_SECTION
  }

  public static CTemplateParser create() {
    return new CTemplateParser();
  }

  private CTemplateParser() {
  }

  List<TemplateNode> getNodeList() {
    return nodeList_;
  }

  // Implements a simple lookahead.
  private NODE_TYPE next() {
    if (input_.toString().startsWith("{{#")) {
      return NODE_TYPE.OPEN_SECTION;
    } else if (input_.toString().startsWith("{{/")) {
      return NODE_TYPE.CLOSE_SECTION;
    } else if (input_.toString().startsWith("{{>")) {
      return NODE_TYPE.INCLUDE_SECTION;
    } else if (input_.toString().startsWith("{{")) {
      return NODE_TYPE.VARIABLE;
    } else {
      return NODE_TYPE.TEXT_NODE;
    }
  }

  private String consume(Pattern p) throws TemplateParserException {
    Matcher m = p.matcher(input_);
    if (m.lookingAt()) {
      String string_to_return = m.group(1);
      // This is apparently an inefficient operation, but parsing should only
      // happen when the template is loaded or changes, so it doesn't happen
      // very often.
      input_ = input_.delete(0, m.end());
      logger.finest("consumed '" + string_to_return + "'");
      return string_to_return;
    }
    throw new TemplateParserException(
        "Unexpected or malformed input: " + input_);
  }

  private void handleTextNode() {
    int next_braces = input_.indexOf("{{");
    String text;
    if (next_braces == -1) { // no more parser syntax
      text = input_.toString();
      input_.setLength(0);
      input_.trimToSize();
    } else {
      text = input_.substring(0, next_braces);
      input_.delete(0, next_braces);
    }
    if (text.length() > 0) {
      logger.finest("found text node '" + text + "'");
      nodeList_.add(TextNode.create(text));
    }
  }

  public List<TemplateNode> parse(String template)
      throws TemplateParserException {
    nodeList_.clear();
    input_ = new StringBuilder(template);

    while (input_.length() > 0) {
      logger.finest("looking ahead at '" + input_ + "'");
      switch (next()) {
        case OPEN_SECTION:
          handleOpenSection();
          break;
        case CLOSE_SECTION:
          handleCloseSection();
          break;
        case VARIABLE:
          handleVariable();
          break;
        case TEXT_NODE:
          handleTextNode();
          break;
        case INCLUDE_SECTION:
          handleInclude();
          break;
        default:
          throw new RuntimeException("Internal error parsing template.");
      }
    }
    return getNodeList();
  }

  private void handleInclude() throws TemplateParserException {
    String consumed = consume(RE_INCLUDE);
    nodeList_.add(IncludeNode.parse(consumed));
  }

  private void handleVariable() throws TemplateParserException {
    logger.finest("consuming VARIABLE with regex " + RE_VARIABLE.pattern());
    String consumed = consume(RE_VARIABLE);
    nodeList_.add(VariableNode.parse(consumed));
  }

  private void handleCloseSection() throws TemplateParserException {
    logger.finest(
        "consuming CLOSE SECTION with regex " + RE_CLOSE_SECTION.pattern());
    String consumed = consume(RE_CLOSE_SECTION);
    logger.finest("CLOSING " + consumed);
    nodeList_.add(SectionNode.close(consumed));
  }

  private void handleOpenSection() throws TemplateParserException {
    logger.finest(
        "consuming OPEN SECTION with regex " + RE_OPEN_SECTION.pattern());
    String consumed = consume(RE_OPEN_SECTION);
    logger.finest("OPENING " + consumed);
    nodeList_.add(SectionNode.open(consumed));
  }
}