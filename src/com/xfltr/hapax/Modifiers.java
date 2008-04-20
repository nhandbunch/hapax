package com.xfltr.hapax;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Static class that implements the methods used by the modifiers.
 *
 * @author dcoker
 */
public final class Modifiers {

  private Modifiers() {
  }

  /**
   * Given a string, returns a string suitable for use as a JavaScript string
   * literal.
   *
   * @param unescaped The value you wish to escape.
   *
   * @return The escaped string
   */
  public static String jsEscape(String unescaped) {
    final String also_safe = "_ ";
    StringBuilder escaped = new StringBuilder();
    for (int i = 0; i < unescaped.length(); i++) {
      char ch = unescaped.charAt(i);
      if (Character.isLetterOrDigit(ch) || also_safe.indexOf(ch) != -1) {
        escaped.append(ch);
      } else if (ch < 256) {
        escaped.append("\\x").append(Integer.toHexString(ch));
      } else {
        escaped.append("\\u").append(String.format("%04x", (int) ch));
      }
    }
    return escaped.toString();
  }

  /**
   * Given a string, returns a string suitable for inclusion as a URL
   * parameter.
   *
   * @param unescaped The value to escape.
   *
   * @return The escaped value.
   */
  private static String urlEncode(String unescaped) {
    try {
      return URLEncoder.encode(unescaped, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Given a string, returns a string suitable for use in an XML file.
   *
   * @param unescaped The string to escape.
   *
   * @return The escaped string.
   */
  private static String xmlEscape(String unescaped) {
    StringBuilder escaped = new StringBuilder();
    for (int i = 0; i < unescaped.length(); i++) {
      char ch = unescaped.charAt(i);
      if (Character.isLetterOrDigit(ch) || ch == ' ') {
        escaped.append(ch);
      } else {
        escaped.append("&#").append((int) ch).append(";");
      }
    }
    return escaped.toString();
  }

  /**
   * Given a string, returns a string suitable for use in an HTML document.
   *
   * @param unescaped The string to escape.
   *
   * @return The escaped string.
   */
  private static String htmlEscape(String unescaped) {
    StringBuilder escaped = new StringBuilder();
    for (int i = 0; i < unescaped.length(); i++) {
      char ch = unescaped.charAt(i);
      if (Character.isLetterOrDigit(ch) || ch == ' ') {
        escaped.append(ch);
      } else if (ch == '&') {
        escaped.append("&amp;");
      } else if (ch == '"') {
        escaped.append("&quot;");
      } else if (ch == '\r' || ch == '\n' || ch == '\t') {
        escaped.append(ch);
      } else {
        escaped.append("&#").append((int) ch).append(";");
      }
    }
    return escaped.toString();
  }

  public static String newlinesToBreaks(String t) {
    return t.replaceAll("\n", "<br/>");
  }

  public static String applyModifiers(String input, List<FLAGS> modifiers) {
    for (FLAGS modifier : modifiers) {
      switch (modifier) {
        case H:
          input = htmlEscape(input);
          break;
        case X:
          input = xmlEscape(input);
          break;
        case J:
          input = jsEscape(input);
          break;
        case U:
          input = urlEncode(input);
          break;
        case B:
          input = newlinesToBreaks(input);
      }
    }
    return input;
  }

  public static List<FLAGS> parseModifiers(String[] split) {
    List<FLAGS> list =
        new ArrayList<FLAGS>(10);
    for (int i = 1; i < split.length; i++) {
      list.add(FLAGS.valueOf(split[i].toUpperCase()));
    }
    return list;
  }

  public enum FLAGS {
    H, // HTML
    X, // XML
    J, // JavaScript string literal
    U, // URL escaped (%xx)
    B, // turns \n into <br/>
  }
}
