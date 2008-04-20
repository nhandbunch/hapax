package com.xfltr.hapax;

/**
 * A dummy loader that throws an exception any time it is used.  This is used
 * by Templates that do not have a Loader associated with them.
 *
 * @author dcoker
 */
class NullTemplateLoader implements TemplateLoader {
  public Template getTemplate(String filename)
      throws TemplateException {
    throw new TemplateException(
        "You must configure the Template with setLoader() " +
            "prior to including hapax.");
  }

  public Template getTemplate(String filename, String templateDirectory)
      throws TemplateException {
    return getTemplate(filename);
  }
}
