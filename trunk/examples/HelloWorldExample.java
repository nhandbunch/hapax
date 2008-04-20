import com.xfltr.hapax.Template;
import com.xfltr.hapax.TemplateDictionary;
import com.xfltr.hapax.TemplateException;

class HelloWorldExample {
  public static void main(String[] args) throws TemplateException {
    Template tmpl = Template.parse("Hello, {{WORLD:h}}");
    TemplateDictionary dict = TemplateDictionary.create();
    dict.put("WORLD", "Iapetus");
    System.out.println(tmpl.renderToString(dict));
  }
}
