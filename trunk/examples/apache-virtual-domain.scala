#!/bin/sh
exec /usr/bin/scala -classpath ../dist/xfltr-hapax-1.0-bin.jar "$0" "$@"
!#

import com.xfltr.hapax.Template;
import com.xfltr.hapax.TemplateDictionary;

object ApacheVirtualHostScalaExample {
  def main(args: Array[String]) {
    val template = """
<VirtualHost *:80>
 DocumentRoot {{directory}}
 ServerName {{domain}}
 ServerAlias www.{{domain}}
 {{#CUSTOM_LOGGING_SECTION}}
 ErrorLog logs/autodomain/{{domain}}-error.log
 TransferLog logs/autodomain/{{domain}}-access.log
 CustomLog logs/autodomain/{{domain}}-referer.log referers
 {{/CUSTOM_LOGGING_SECTION}}
 <Directory {{directory}}>
  Options {{options}}
  Order allow,deny
  Allow from all
 </Directory>
</Virtualhost>
};
""";

    val t = Template.parse(template);
    val d = TemplateDictionary.create();
    d.showSection("CUSTOM_LOGGING_SECTION");
    d.put("domain", "xfltr.com");
    d.put("directory", "/home/vhosts/xfltr.com/www/");
    d.put("options", "none");
    println(t.renderToString(d));
  }
}
ApacheVirtualHostScalaExample.main(args)
