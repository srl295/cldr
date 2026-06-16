package org.unicode.cldr.tool;

import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.CLDRLocale;
import org.unicode.cldr.util.CLDRTool;
import org.unicode.cldr.util.LocaleInheritanceInfo;

@CLDRTool(alias = "get", description="work with paths from cli")
public class GetPath {
    public static void main(String args[]) {
        CLDRLocale l = null;
        String p = null;
        for(int i=0; i<args.length; i++) {
            final String s = args[i];
            switch(s) {
                case "--locale":
                    i++;
                    l = CLDRLocale.getInstance(args[i]);
                    break;
                case "--xpath":
                    i++;
                    p = args[i];
                case "--explain":
                    if (p == null) p = readString("XPath");
                    if (l == null) l = readString("Locale");
                    explainPath(l, p.replaceAll("%","\""));
                    break;
            }
        }
    }

    private static void explainPath(CLDRLocale l, String p) {
        System.out.println("XPath: " + p);
        System.out.println("Loading: " + l);
        final CLDRFile f = CLDRConfig.getInstance().getCLDRFile(l.getBaseName(), true);
        for(LocaleInheritanceInfo path : f.getPathsWhereFound(p)) {
            System.out.println(path.toString());
        }
        System.out.println();
    }
}
