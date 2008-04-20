package com.xfltr.hapax;

import java.util.*;
import java.util.logging.Logger;

/**
 * TemplateDictionary a nested dictionary that stores name => value mappings for
 * template variables, filenames for included hapax, and a list of sections
 * that have been explicitly shown.
 *
 * @author dcoker
 */
public class TemplateDictionary {
  private static final Logger logger =
      Logger.getLogger(TemplateDictionary.class.getSimpleName());

  private final Map<String, String> dict = new HashMap<String, String>();

  /**
   * A list of children dictionaries.
   */
  private final Map<String, List<TemplateDictionary>> subs =
      new HashMap<String, List<TemplateDictionary>>();

  /**
   * A list of sections that have been explicitly shown with showSection().
   */
  private final Set<String> shownSections =
      new HashSet<String>();

  /**
   * The TemplateDictionary that this class is a child of, or null if it is the
   * top-level dictionary.
   */
  private final TemplateDictionary parent;

  /**
   * Creates a top-level TemplateDictionary.
   *
   * Create child dictionaries with .addChildDict().
   *
   * @return a new TemplateDictionary
   */
  public static TemplateDictionary create() {
    return new TemplateDictionary(null);
  }

  /**
   * Puts a String value into the dictionary.
   *
   * @param key The key for this value
   * @param val The value
   */
  public void put(String key, String val) {
    if (dict.containsKey(key)) {
      logger.warning("put(" + key +
          ") called, but there is already a value for this key.");
    }
    dict.put(key.toUpperCase(), val);
  }

  /**
   * Puts an integer value into the dictionary.  All values are normalized to
   * Strings.
   *
   * @param key The key for this value.
   * @param val The value
   */
  public void put(String key, int val) {
    put(key.toUpperCase(), String.valueOf(val));
  }

  /**
   * Returns true if the Dictionary (or any parent dictionaries) contains the
   * requested key.
   *
   * @param key The key to look for
   *
   * @return True if the dictionary (or any parent dictionary) contains the key,
   *         false otherwise.
   */
  public boolean contains(String key) {
    if (dict.containsKey(key.toUpperCase())) {
      return true;
    } else if (parent != null) {
      return parent.contains(key.toUpperCase());
    }
    return false;
  }

  /**
   * Gets the value of a given dictionary key.
   *
   * @param key The name of the dictionary item to return.
   *
   * @return The value of the requested dictionary item, or empty string.
   */
  public String get(String key) {
    if (dict.containsKey(key.toUpperCase())) {
      return dict.get(key.toUpperCase());
    } else if (parent != null) {
      return parent.get(key.toUpperCase());
    } else {
      logger.warning(
          "Unable to find a value for '" + key + "', returning empty string!");
      return "";
    }
  }

  /**
   * Gets a list of the child dictionaries with a given name.
   *
   * @param key The name of the child dictionaries to retreive.
   *
   * @return a list of TemplateDictionaries that are children to this
   *         dictionary
   */
  public List<TemplateDictionary> getChildDicts(String key) {
    if (subs.containsKey(key.toUpperCase())) {
      return subs.get(key.toUpperCase());
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Adds a dictionary with a given name.  The name should correspond to the
   * name of an included template or to the name of a section.
   *
   * If there are multiple dictionaries with the same name present AND
   * showSection() has been called, the section will be repeated once for each
   * dictionary.
   *
   * @param key The name of the child dictionary to create.
   *
   * @return a new TemplateDictionary
   */
  public TemplateDictionary addChildDict(String key) {
    TemplateDictionary td = new TemplateDictionary(this);

    if (!subs.containsKey(key.toUpperCase())) {
      List<TemplateDictionary> dicts =
          new LinkedList<TemplateDictionary>();
      dicts.add(td);
      subs.put(key.toUpperCase(), dicts);
    } else {
      subs.get(key.toUpperCase()).add(td);
    }
    return td;
  }

  /**
   * Creates a child dictionary and shows the section.  This is equivalent to
   * calling addChildDict() and showSection() separately.
   *
   * @param section_name The name of the dictionary and section to show
   *
   * @return The child dictionary
   */
  public TemplateDictionary addChildDictAndShowSection(String section_name) {
    showSection(section_name);
    return addChildDict(section_name);
  }

  /**
   * Hides a section from being visible.  Sections are hidden by default.
   *
   * @param section The section to hide.
   */
  public void hideSection(String section) {
    shownSections.remove(section);
  }

  /**
   * Shows a section.  Sections are shown by default.  When this method is
   * called, the section will be displayed once for each child dictionary of the
   * same name or once if there are no child dictionaries of the same name.
   *
   * @param section The section to show.
   */
  public void showSection(String section) {
    shownSections.add(section);
  }

  boolean isHiddenSection(String sectionName) {
    return !shownSections.contains(sectionName);
  }

  private TemplateDictionary(TemplateDictionary parent) {
    this.parent = parent;
  }
}
