package com.xfltr.hapax;

import junit.framework.TestCase;

/**
 * Unit test for the {@link TemplateDictionary}.
 *
 * @author dcoker
 */
public class TemplateDictionaryTest extends TestCase {
  public void testHideShow() {
    TemplateDictionary td = TemplateDictionary.create();
    assertTrue(td.isHiddenSection("FOO"));
    td.hideSection("FOO");
    assertTrue(td.isHiddenSection("FOO"));
    td.showSection("FOO");
    assertFalse(td.isHiddenSection("FOO"));
  }

  public void testHideShowDoesNotPropagateToChildren() {
    TemplateDictionary td = TemplateDictionary.create();
    assertTrue(td.isHiddenSection("FOO"));
    TemplateDictionary child = td.addChildDict("CHILD");
    assertTrue(td.isHiddenSection("CHILD"));
    assertTrue(child.isHiddenSection("FOO"));
    td.showSection("FOO");
    assertFalse(td.isHiddenSection("FOO"));
    assertTrue(child.isHiddenSection("FOO"));
  }

  public void testDictStoresValues() {
    TemplateDictionary td = TemplateDictionary.create();
    td.put("K", "V");
    assertEquals("V", td.get("K"));
  }

  public void testDictStoresIntValues() {
    TemplateDictionary td = TemplateDictionary.create();
    td.put("K", 123);
    assertEquals("123", td.get("K"));
  }

  public void testDictReturnsEmptyStringForUnknownVariables() {
    TemplateDictionary td = TemplateDictionary.create();
    assertEquals("", td.get("X"));
  }

  public void testChildDictsLookUpwardsButNotDownwards() {
    TemplateDictionary td = TemplateDictionary.create();
    td.put("L0", "L0");
    TemplateDictionary tda = td.addChildDict("D1");
    assertEquals("L0", tda.get("L0"));
    tda.put("L1", "L1");
    TemplateDictionary tdaa = tda.addChildDict("D2");
    assertEquals("L0", tdaa.get("L0"));
    assertEquals("L1", tdaa.get("L1"));
    tdaa.put("L2", "L2");
    assertEquals("", td.get("L1"));
    assertFalse(td.contains("L1"));
    assertEquals("", td.get("L2"));
    assertFalse(td.contains("L2"));
    assertEquals("", tda.get("L2"));
    assertFalse(tda.contains("L2"));
    assertEquals("L2", tdaa.get("L2"));
  }

  public void testMultipleChildDicts() {
    TemplateDictionary td = TemplateDictionary.create();
    td.addChildDict("TWO");
    td.addChildDict("TWO");
    assertEquals(2, td.getChildDicts("TWO").size());
  }

  public void testLastPutWins() {
    TemplateDictionary td = TemplateDictionary.create();
    td.put("X", "");
    td.put("X", "X");
    assertEquals("X", td.get("X"));
  }

  public void testShowAndHideAreIdempotent() {
    TemplateDictionary td = TemplateDictionary.create();
    assertTrue(td.isHiddenSection("X"));
    td.showSection("X");
    td.showSection("X");
    assertFalse(td.isHiddenSection("X"));
    td.hideSection("X");
    td.hideSection("X");
    assertTrue(td.isHiddenSection("X"));
  }

  public void testAddChildDictAndShowSection() {
    TemplateDictionary td = TemplateDictionary.create();
    assertTrue(td.isHiddenSection("X"));
    td.addChildDictAndShowSection("X");
    assertFalse(td.isHiddenSection("X"));
  }

}
