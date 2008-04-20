package com.xfltr.hapax.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for EZT-style templates.  This differs from the collab.net
 * implementation in a variety of ways.
 *
 * Features supported:
 *
 * Includes: [include "filename"] [include variable]
 *
 * Inserts, which are identical to includes: [insertfile "filename"] [insertfile
 * variable]
 *
 * Variable replacement: [varable]
 *
 * Existence: [if-any variable]x[else]y[end]
 *
 * Comparisons: [is x "y"][else][end]
 *
 * Nesting for [if-any], [is], and [define] is supported.
 *
 * No escaping is performed, so [format raw] is a no-op.
 *
 * @author dcoker
 */
public class EztParser implements TemplateParser {
  private static final Logger logger_ =
      Logger.getLogger(EztParser.class.getSimpleName());

  private static final String TOK_LITERAL = "([^\\[]+)";

  // for variable names
  private static final String TOK_IDENT = "[a-z][a-z0-9_.-]*";

  // for if-any, include, define, etc
  private static final String TOK_DIRECTIVE = "[a-z_-]+";

  // for the args list of the STMT_DIRECTIVE
  private static final String TOK_DIRECTIVE_ARGS =
      "((" + TOK_IDENT + ")|(\\\"[^\"]*\\\")|(" + TOK_IDENT +
          " \\\"[^\"]*\\\"))";

  // for variable [dereference]
  private static final String STMT_VARIABLE_DEREF = "(\\[" + TOK_IDENT + "\\])";

  // for [directive arg], [directive arg arg], [directive "arg" "arg"]
  private static final String STMT_DIRECTIVE =
      "(\\[(" + TOK_DIRECTIVE + ")\\s+" + TOK_DIRECTIVE_ARGS + "\\s*\\])";

  // for [[] => "["
  private static final String STMT_BRACKET = "(\\[\\[\\])";

  // for [#comments]
  private static final String STMT_COMMENT = "(\\[#[^\\]]*\\])";

  private static final Pattern PATTERN = Pattern
      .compile(TOK_LITERAL + "|" + STMT_BRACKET + "|" + STMT_COMMENT + "|" +
          STMT_VARIABLE_DEREF + "|" + STMT_DIRECTIVE,
          Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

  /**
   * Private constructor
   */
  private EztParser() {

  }

  public static EztParser create() {
    return new EztParser();
  }


  public List<TemplateNode> parse(String input) throws TemplateParserException {
    final List<TemplateNode> node_list = new LinkedList<TemplateNode>();
    if (input == null || input.length() == 0) {
      return node_list;
    }

    final Matcher matcher = PATTERN.matcher(input);

    while (matcher.find()) {
      for (int i = 1; i <= matcher.groupCount(); i++) {
        if (matcher.group(i) != null) {
          switch (i) {
            case 1: // literal string
              node_list.add(TextNode.create(matcher.group(i)));
              break;
            case 2: // left bracket ([[])
              node_list.add(TextNode.create("["));
              break;
            case 3: // a [# comment]
              break;
            case 4: // a [variable]
              handleVariable(node_list, matcher);
              break;
            case 5: // some kind of directive
              handleDirective(node_list, matcher);
              break;
            default:
              break;
          }
        }
      }
    }

    return node_list;
  }

  private void handleVariable(List<TemplateNode> node_list, Matcher matcher)
      throws TemplateParserException {
    String text = matcher.group(4);

    // Just in case the user messes up, give them a helpful error message.
    if (text.matches("\\[(is|if-any|define|include|insertfile|format)\\]")) {
      throw new TemplateParserException(
          "You cannot dereference variables named after reserved words: " +
              text);
    }

    // Catch [end] and [else] cases.
    if (text.equals("[end]")) {
      node_list.add(EztEndNode.create());
    } else if (text.equals("[else]")) {
      node_list.add(EztElseNode.create());
    } else {
      node_list.add(VariableNode.parse(
          matcher.group(4).replaceAll("(^\\[)|(\\]$)", "")));
    }
  }

  private void handleDirective(List<TemplateNode> node_list, Matcher matcher)
      throws TemplateParserException {
    final String directive = matcher.group(6);
    final String one_parameter = matcher.group(8);
    final String quoted_include_parameter = matcher.group(9);
    final String two_parameters = matcher.group(10);

    if (directive.equals("include") || directive.equals("insertfile")) {
      if (one_parameter != null) {
        node_list.add(EztIncludeNode.parse(one_parameter));
      } else {
        node_list.add(EztIncludeNode.parse(quoted_include_parameter));
      }
    } else if (directive.equals("define")) {
      node_list.add(EztDefineNode.parse(one_parameter));
    } else if (directive.equals("is")) {
      if (two_parameters == null) {
        throw new TemplateParserException("[is] requires two parameters.");
      }
      String[] tokenized = two_parameters.split("\\s+");
      String varname = tokenized[0];
      String value = tokenized[1].replace("\"", "");
      node_list.add(EztConditionalNode.is(varname, value));
    } else if (directive.equals("if-any")) {
      if (one_parameter == null) {
        throw new TemplateParserException("[if-any] requires one parameter.");
      }
      node_list.add(EztConditionalNode.ifAny(one_parameter));
    } else if (directive.equals("format")) {
      // do nothing
      logger_.info("encountered a [format] directive; ignoring.");
    }
  }
}