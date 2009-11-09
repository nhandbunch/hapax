package com.xfltr.hapax;

import com.xfltr.hapax.parser.TemplateParserException;
import junit.framework.TestCase;

/**
 * Unit test for the {@link Template} class when used with the {@link
 * com.xfltr.hapax.parser.CTemplateParser}.
 *
 * @author dcoker
 */
public class CTemplateTest extends TestCase {
  private TemplateDictionary td;
  private static final String SIMPLE_X_SECTION_TEMPLATE = "{{#X}}Hello{{/X}}";

  @Override
  protected void setUp() throws Exception {
    td = TemplateDictionary.create();
  }

  public void testPlainTextTemplate() throws TemplateException {
    Template tmpl = Template.parse("Hello");
    String output = tmpl.renderToString(td);
    assertEquals("Hello", output);
  }

  public void testSectionsAreRepeatedForEachDictionary()
      throws TemplateException {
    String t = SIMPLE_X_SECTION_TEMPLATE;
    Template tmpl = Template.parse(t);
    td.addChildDict("X");
    td.addChildDict("X");
    td.showSection("X");
    String output = tmpl.renderToString(td);
    assertEquals("HelloHello", output);
  }

  public void testHiddenSectionWithNoChildDict() throws TemplateException {
    String t = SIMPLE_X_SECTION_TEMPLATE;
    Template tmpl = Template.parse(t);
    String output = tmpl.renderToString(td);
    assertEquals("", output);
  }

  public void testHiddenSectionWithChildDict() throws TemplateException {
    String t = SIMPLE_X_SECTION_TEMPLATE;
    Template tmpl = Template.parse(t);
    td.hideSection("X");
    td.addChildDict("X");
    String output = tmpl.renderToString(td);
    assertEquals("", output);
  }

  public void testRepeatedSectionsReadMultipleDicts() throws TemplateException {
    String t = "-{{#X}}{{V}}{{/X}}-";
    Template tmpl = Template.parse(t);
    td.addChildDict("X").put("V", 1);
    td.addChildDict("X").put("V", 2);
    td.addChildDict("X").put("V", 3);
    assertEquals("--", tmpl.renderToString(td));
    td.showSection("X");
    assertEquals("-123-", tmpl.renderToString(td));
  }

  public void testModifiersApplyToVariables() throws TemplateException {
    td.put("K", "&");
    String t = "{{K:h}}";
    Template tmpl = Template.parse(t);
    String output = tmpl.renderToString(td);
    assertEquals("&amp;", output);
  }

  public void testCompatibility1() throws TemplateException {
    String t = "hi {{VAR}} lo";
    Template tmpl = Template.parse(t);
    assertEquals("hi  lo", tmpl.renderToString(td));
    td.put("VAR", "yo");
    assertEquals("hi yo lo", tmpl.renderToString(td));
    td.put("VAR", "yoyo");
    assertEquals("hi yoyo lo", tmpl.renderToString(td));
    td.put("VA", "noyo");
    assertEquals("hi yoyo lo", tmpl.renderToString(td));
  }

  public void testCompatibility2() throws TemplateException {
    String t = "boo!\nhi {{#SEC}}lo{{/SEC}} bar";
    Template tmpl = Template.parse(t);
    assertEquals("boo!\nhi  bar", tmpl.renderToString(td));
    td.showSection("SEC");
    assertEquals("boo!\nhi lo bar", tmpl.renderToString(td));

    {
      TemplateDictionary new_dict = TemplateDictionary.create();
      new_dict.addChildDict("SEC");
      assertEquals("boo!\nhi  bar", tmpl.renderToString(new_dict));
      new_dict.addChildDict("SEC");
      // Even though we have two child dictionaries, the output should stay
      // the same because that section is not visible.
      assertEquals("boo!\nhi  bar", tmpl.renderToString(new_dict));
      new_dict.showSection("SEC");
      // Once the section is unhidden, all the repeated dictionaries are
      // displayed.
      assertEquals("boo!\nhi lolo bar", tmpl.renderToString(new_dict));
    }
  }

  public void testIncludesAreRenderedAndRenderOncePerChildDict()
      throws TemplateException {
    MockTemplateLoader mock = new MockTemplateLoader();
    mock.put("text.xtm", "Plain Text");
    mock.put("variable.xtm", "Hello {{WORLD}}");

    Template t = Template.parse("{{>TEXT}}");
    t.setLoader(mock);
    TemplateDictionary td = TemplateDictionary.create();
    td.put("TEXT", "text.xtm");
    assertEquals("Plain Text", t.renderToString(td));
    // The existence of a single dictionary should still cause the template
    // to render only once.
    td.addChildDict("TEXT");
    assertEquals("Plain Text", t.renderToString(td));
    // Two dictionaries should cause the template to render twice.
    td.addChildDict("TEXT");
    assertEquals("Plain TextPlain Text", t.renderToString(td));
  }

  // http://code.google.com/p/hapax/issues/detail?id=2
  public void testIncludesWithSubdirectoriesWork() throws TemplateException {
    MockTemplateLoader mock = new MockTemplateLoader();
    mock.put("test/b.xtm", "b");
    Template t = Template.parse("a{{>B}}cdefg");
    t.setLoader(mock);
    TemplateDictionary td = TemplateDictionary.create();
    td.put("B", "test/b.xtm");
    assertEquals("abcdefg", t.renderToString(td));
  }

  // http://code.google.com/p/hapax/issues/detail?id=1
  public void testEncodingDefault() throws TemplateException {
    Template tmpl = Template.parse("Hello-you, {{WORLD:h}}");
    TemplateDictionary dict = TemplateDictionary.create();
    dict.put("WORLD", "Iapetus");
    System.out.println(tmpl.renderToString(dict));
    assertEquals("Hello-you, Iapetus", tmpl.renderToString(dict));
  }

  public void testIncludesReadVariablesFromParentDict()
      throws TemplateException {
    MockTemplateLoader mock = new MockTemplateLoader();
    mock.put("nested.xtm", "w00t");
    mock.put("incl.xtm", "Hello {{WORLD}} {{>NESTED}}");
    Template t = Template.parse("{{>INCL}}");
    t.setLoader(mock);
    TemplateDictionary td = TemplateDictionary.create();
    td.put("INCL", "incl.xtm");
    td.put("WORLD", "mars");
    td.put("NESTED", "nested.xtm");
    assertEquals("Hello mars w00t", t.renderToString(td));

    // An entry in a child ditionary should take precedence over a parent
    // dictionary.
    TemplateDictionary sub = td.addChildDict("INCL");
    sub.put("WORLD", "venus");
    assertEquals("Hello venus w00t", t.renderToString(td));
  }

  public void testIncludesThrowExceptionWhenMissingFilename()
      throws TemplateException {
    assertRenderingThrowsException("{{>INCL}}");
  }

  public void testModifiersApplyToIncludes() throws TemplateException {
    MockTemplateLoader loader = new MockTemplateLoader();
    loader.put("incl.xtm", "& on & on");
    TemplateDictionary td = TemplateDictionary.create();
    td.put("INCL", "incl.xtm");
    Template t = Template.parse("{{>INCL:h}}");
    t.setLoader(loader);
    assertEquals("&amp; on &amp; on", t.renderToString(td));
  }

  public void testMultipleModifiersApplyToIncludes() throws TemplateException {
    MockTemplateLoader loader = new MockTemplateLoader();
    loader.put("incl.xtm", "& on\n& on");
    TemplateDictionary td = TemplateDictionary.create();
    td.put("INCL", "incl.xtm");
    Template t = Template.parse("{{>INCL:h:b}}");
    t.setLoader(loader);
    assertEquals("&amp; on<br/>&amp; on", t.renderToString(td));
  }

  public void testMissingCloseTagThrowsException() {
    assertRenderingThrowsException("{{#X}}");
  }

  public void testMismatchedTagThrowsException() {
    assertRenderingThrowsException("{{#X}}{{/Y}}");
    assertRenderingThrowsException("{{#X}}{{#P}}{{/P}}{{/Y}}");
  }

  private void assertRenderingThrowsException(String t) {
    try {
      Template tmpl = Template.parse(t);
      tmpl.renderToString(td);
      fail("Rendering invalid template did not throw an exception.");
    } catch (TemplateException e) {
      // We are looking for exceptions thrown during render phase, not parse
      // phase.
      assertFalse(e instanceof TemplateParserException);
    }
  }
}
