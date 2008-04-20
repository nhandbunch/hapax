package com.xfltr.hapax;

/**
 * TemplateLoaderContext represents information about a Template that is useful
 * to the TemplateLoader.  Specifically, this stores the directory of the template
 * that is being rendered so that [include] or {{>include}} syntax can use
 * relative paths.
 *
 * @author dcoker 
 */
public class TemplateLoaderContext {
  private final TemplateLoader loader_;
  private final String template_directory_;

  public TemplateLoaderContext(TemplateLoader loader,
                               String template_directory) {
    this.loader_ = loader;
    this.template_directory_ = template_directory;
  }

  public TemplateLoader getLoader() {
    return loader_;
  }


  public String getTemplateDirectory() {
    return template_directory_;
  }

}
