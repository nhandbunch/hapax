package com.xfltr.hapax;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Unit test for the {@link Modifiers} class.
 *
 * @author dcoker
 */
public class ModifiersTest extends TestCase {
  public void testJsEscape() {
    Assert.assertEquals("", Modifiers.jsEscape(""));
    assertEquals("a", Modifiers.jsEscape("a"));
    assertEquals("\\x5cn", Modifiers.jsEscape("\\n"));
    assertEquals("\\x3b", Modifiers.jsEscape(";"));
    assertEquals("\\x27", Modifiers.jsEscape("'"));
    assertEquals("\\x22", Modifiers.jsEscape("\""));
    assertEquals("\\x2d", Modifiers.jsEscape("-"));
    assertEquals("\\x3c", Modifiers.jsEscape("<"));
    assertEquals("\\x3e", Modifiers.jsEscape(">"));
    assertEquals("Miche\\u0300le", Modifiers.jsEscape("Miche\u0300le"));
    assertEquals("\\u0322", Modifiers.jsEscape("\u0322"));
  }

  public void testNewlinesToBreaks() {
    assertEquals("", Modifiers.newlinesToBreaks(""));
    assertEquals("hello<br/>world", Modifiers.newlinesToBreaks("hello\nworld"));
  }
}
