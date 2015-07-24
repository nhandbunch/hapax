# What is Hapax? #

Hapax is a simple but powerful text templating library for Java.

Hapax is suitable for constructing text output from Java code. The syntax is similar to Google's [ctemplate](http://code.google.com/p/google-ctemplate/) library, and emphasizes the separation of logic from presentation.

Hapax was designed to be easy to use and have minimal dependencies. Hapax does not depend on any existing web framework, and is suitable for use in servlets, scripting languages (Scala, Groovy, etc), and server-side applications.

# Example #

```
package com.xfltr.hapax.examples;
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
```

# Features #

  * Hapax enforces a strict separation of "view" from application logic.
  * Hapax has zero dependencies on third-party code (except for JUnit).
  * Hapax is well unit-tested.
  * Hapax is based loosely on the syntax and behavior of Google's ctemplate library.
  * Hapax generate any kind of textual output: config files, HTML, XML, CSS, etc.
  * Hapax templates do not require a compilation step. Templates are parsed at runtime.
  * Hapax has native support for some types of content escaping (JavaScript, XML, HTML, URLs).
  * Hapax does not expect your templates to come from disk: templates can be be generated at runtime, stored in a database, or any other location.