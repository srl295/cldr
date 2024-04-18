package org.unicode.cldr.tool;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.unicode.cldr.test.CheckCLDR.CheckStatus;
import org.unicode.cldr.test.TestCache.TestResultBundle;
import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.CLDRPaths;
import org.unicode.cldr.util.Factory;

public class ChartSupplemental extends Chart {
    private static final CLDRConfig CLDR_CONFIG = CLDRConfig.getInstance();
    static final CLDRFile ENGLISH = CLDR_CONFIG.getEnglish();
    static final String DIR = CLDRPaths.CHART_DIRECTORY + "verify/supplemental/";

    private final String locale;

    public ChartSupplemental(String locale) {
        super();
        this.locale = locale;
    }

    @Override
    public String getDirectory() {
        return DIR;
    }

    @Override
    public String getTitle() {
        return ENGLISH.getName(locale) + ": Supplemental Data";
    }

    @Override
    public String getExplanation() {
        return "<p>This chart shows supplemental data that is of concern to the locale overall.</p>";
    }

    @Override
    public String getFileName() {
        return locale;
    }

    @Override
    public void writeContents(Writer pw, Factory factory, TestResultBundle test)
            throws IOException {
        CLDRFile cldrFile = factory.make(locale, true);

        Set<CheckStatus> pp = new TreeSet<CheckStatus>();

        // add any 'early' errors
        pp.addAll(test.getPossibleProblems());

        // add all non-path status
        for (final String x : cldrFile) {
            List<CheckStatus> result = new ArrayList<CheckStatus>();
            test.check(x, result, cldrFile.getStringValue(x));
            for (final CheckStatus s : result) {
                if (s.isNonPathBased()) pp.add(s);
            }
        }

        // report

        if (pp.isEmpty()) {
            pw.write("<h3>No Errors</h3>\n");
            pw.write("There are no overall locale issues to report");
        } else {
            pw.write("<h3>Overall Errors</h3>\n");
            pw.write("<ol>\n");
            for (final CheckStatus s : pp) {
                pw.write(
                        String.format(
                                "<li> <b>%s</b> <i>%s</i> - %s</li>\n",
                                s.getType(), s.getSubtype(), s.getMessage()));
            }
            pw.write("</ol>\n\n");
        }

        pw.write("</div> <!-- ReportSupplemental -->\n");
    }
}
