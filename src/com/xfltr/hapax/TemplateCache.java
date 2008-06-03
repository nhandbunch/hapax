package com.xfltr.hapax;

import com.xfltr.hapax.parser.CTemplateParser;
import com.xfltr.hapax.parser.TemplateParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * TemplateCache constructs {@link Template} objects based on files stored on
 * disk.  TemplateCache implements an in-memory unbounded cache of parsed
 * Templates.  Each stored Template is automatically invalidated if the template
 * file on disk changes.
 *
 * See examples/TemplateCacheExample.java for an example of how to use
 * TemplateCache.
 *
 * It is recommended that you place a reference to your TemplateCache instance
 * in some globally accessible location so that you use a single cache across
 * multiple requests.
 *
 * @author dcoker
 */
public class TemplateCache implements TemplateLoader {

  private final Map<String, Template> templates_ =
      new HashMap<String, Template>();
  private final Map<String, Long> lastUpdated_ =
      new HashMap<String, Long>();
  private final String basePath_;
  private final TemplateParser parser_;

  /**
   * Creates a TemplateLoader using the CTemplateParser.
   */
  public static TemplateLoader create(String base_path) {
    return new TemplateCache(CTemplateParser.create(), base_path);
  }

  /**
   * Creates a TemplateLoader using the given TemplateParser.
   */
  public static TemplateLoader createForParser(String base_path,
                                               TemplateParser parser) {
    return new TemplateCache(parser, base_path);
  }

  /**
   * Parses and fetches a template from disk.
   *
   * @param filename The path to the template, relative to the templateDirectory
   *                 passed to the ctor of TemplateCache.
   */
  public Template getTemplate(String filename) throws TemplateException {
    filename = PathUtil.join(basePath_, filename);

    // We check the last modified timestamp on the template once per render.
    // If the template has changed, we reload and reparse it.  Otherwise, we
    // return the copy from memory.
    File file = new File(filename);
    long last_modified = file.lastModified();

    if (inCache(filename, last_modified)) {
      return templates_.get(filename);
    }

    FileReader reader;
    try {
      reader = new FileReader(filename);
    } catch (FileNotFoundException e) {
      throw new TemplateException(e);
    }

    String contents;
    try {
      contents = readToString(reader);
    } catch (IOException e) {
      throw new TemplateException(e);
    }

    Template results;
    results = Template.parse(parser_, contents);
    results.setLoaderContext(new TemplateLoaderContext(this, file.getParent()));
    updateCache(filename, results, last_modified);
    return results;
  }

  /**
   * Parses and fetches a template from a subdirectory of the configured
   * basePath. This is useful when fetching templates with paths relative to
   * other templates (such as in includes).
   *
   * TODO: This smells.
   */
  public Template getTemplate(String filename, String templateDirectory)
      throws TemplateException {
    assert templateDirectory.startsWith(basePath_);
    String directory_relative_to_template_directory =
        PathUtil.makeRelative(basePath_, "");
    // Construct the filename that we use in the cache
    filename =
        PathUtil.join(directory_relative_to_template_directory, filename);
    return getTemplate(filename);
  }

  /**
   * Private constructor.
   */
  private TemplateCache(TemplateParser parser, String templateDirectory) {
    this.basePath_ = templateDirectory;
    this.parser_ = parser;
  }

  private String readToString(Reader in) throws IOException {
    StringBuilder buf = new StringBuilder();
    try {
      for (int c = in.read(); -1 != c; c = in.read()) {
        buf.append((char) c);
      }
      return buf.toString();
    } finally {
      in.close();
    }
  }

  private boolean inCache(String filename, long last_modified) {
    return lastUpdated_.containsKey(filename) &&
           lastUpdated_.get(filename) >= last_modified;
  }

  private void updateCache(String filename, Template results,
                           long last_modified) {
    templates_.put(filename, results);
    lastUpdated_.put(filename, last_modified);
  }
}
