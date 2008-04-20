import com.xfltr.hapax.*;

/**
 * Example code for using TemplateCache.
 */
public class TemplateCacheExample {

  public static void main(String[] args) throws TemplateException {  
    TemplateLoader loader = TemplateCache.create(".");
    Template tmpl = loader.getTemplate("template_cache_example.tpl");
    TemplateDictionary dict = TemplateDictionary.create();
    dict.put("VARIABLE", "Hello, World");
    System.out.println(tmpl.renderToString(dict));
  }
}
