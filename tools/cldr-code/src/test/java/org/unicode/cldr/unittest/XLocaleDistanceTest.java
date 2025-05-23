package org.unicode.cldr.unittest;

import com.ibm.icu.util.Output;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.unicode.cldr.draft.XLikelySubtags.LSR;
import org.unicode.cldr.draft.XLocaleDistance;
import org.unicode.cldr.draft.XLocaleDistance.DistanceNode;
import org.unicode.cldr.draft.XLocaleDistance.DistanceOption;
import org.unicode.cldr.draft.XLocaleDistance.DistanceTable;
import org.unicode.cldr.icu.dev.test.TestFmwk;
import org.unicode.cldr.icu.util.LocaleMatcher;

/**
 * Test the XLocaleDistance.
 *
 * @author markdavis
 */
public class XLocaleDistanceTest extends TestFmwk {
    private static final boolean REFORMAT =
            false; // set to true to get a reformatted data file listed

    public static void main(String[] args) {
        new XLocaleDistanceTest().run(args);
    }

    public static final int FAIL = XLocaleDistance.ABOVE_THRESHOLD;

    private XLocaleDistance localeMatcher = XLocaleDistance.getDefault();
    DataDrivenTestHelper tfh =
            new MyTestFileHandler()
                    .setFramework(this)
                    .load(XLocaleDistanceTest.class, "data/localeDistanceTest.txt");

    static class Arguments {
        final ULocale desired;
        final ULocale supported;
        final int desiredToSupported;
        final int supportedToDesired;

        public Arguments(List<String> args) {
            this.desired =
                    new ULocale.Builder()
                            .setLanguageTag(args.get(0))
                            .build(); // use more complicated expression to check syntax
            this.supported = new ULocale.Builder().setLanguageTag(args.get(1)).build();
            this.desiredToSupported = Integer.parseInt(args.get(2));
            this.supportedToDesired =
                    args.size() > 3 ? Integer.parseInt(args.get(3)) : this.desiredToSupported;
        }
    }

    public void testTiming() {
        List<Arguments> testArgs = new ArrayList<>();
        for (List<String> line : tfh.getLines()) {
            if (tfh.isTestLine(line)) {
                testArgs.add(new Arguments(line));
            }
        }
        Arguments[] tests = testArgs.toArray(new Arguments[testArgs.size()]);

        final LocaleMatcher oldLocaleMatcher = new LocaleMatcher("");

        long likelyTime = 0;
        long newLikelyTime = 0;
        long newTimeMinusLikely = 0;
        long intTime = 0;
        long oldTimeMinusLikely = 0;
        final int maxIterations = 10000;

        for (int iterations = maxIterations; iterations > 0; --iterations) {
            int count = 0;
            for (Arguments test : tests) {
                final ULocale desired = test.desired;
                final ULocale supported = test.supported;
                final int desiredToSupported = test.desiredToSupported;
                final int supportedToDesired = test.supportedToDesired;

                long temp = System.nanoTime();
                final ULocale desiredMax = ULocale.addLikelySubtags(desired);
                final ULocale supportedMax = ULocale.addLikelySubtags(supported);
                likelyTime += System.nanoTime() - temp;

                temp = System.nanoTime();
                double distOld1 =
                        oldLocaleMatcher.match(desired, desiredMax, supported, supportedMax);
                double distOld2 =
                        oldLocaleMatcher.match(supported, supportedMax, desired, desiredMax);
                oldTimeMinusLikely += System.nanoTime() - temp;

                temp = System.nanoTime();
                final LSR desiredLSR = LSR.fromMaximalized(desired);
                final LSR supportedLSR = LSR.fromMaximalized(supported);
                newLikelyTime += System.nanoTime() - temp;

                temp = System.nanoTime();
                int dist1 =
                        localeMatcher.distanceRaw(
                                desiredLSR, supportedLSR, 1000, DistanceOption.NORMAL);
                int dist2 =
                        localeMatcher.distanceRaw(
                                supportedLSR, desiredLSR, 1000, DistanceOption.NORMAL);
                newTimeMinusLikely += System.nanoTime() - temp;
            }
        }
        final long oldTime = oldTimeMinusLikely + likelyTime;
        final long newTime = newLikelyTime + newTimeMinusLikely;
        logln("\n");
        logln("\tlikelyTime:\t" + likelyTime / maxIterations);
        logln("\toldTime-likelyTime:\t" + oldTimeMinusLikely / maxIterations);
        logln("totalOld:\t" + oldTime / maxIterations);
        logln("\tnewLikelyTime:\t" + newLikelyTime / maxIterations);
        logln("totalNew:\t" + newTime / maxIterations);
        assertTrue("newTime < 20% of oldTime", newTime * 5 < oldTime);
        // logln("\tnewIntTime-newLikelyTime-extractTime:\t" + intTime/maxIterations);
        // logln("totalInt:\t" + (intTime)/maxIterations);
    }

    @SuppressWarnings("deprecation")
    public void testInternalTable() {
        checkTables(localeMatcher.internalGetDistanceTable(), "", 1);
    }

    @SuppressWarnings("deprecation")
    private void checkTables(DistanceTable internalGetDistanceTable, String title, int depth) {
        // Check that ANY, ANY is always present, and that the table has a depth of exactly 3
        // everyplace.
        Map<String, Set<String>> matches = internalGetDistanceTable.getInternalMatches();

        // must have ANY,ANY
        boolean haveANYANY = false;
        for (Entry<String, Set<String>> entry : matches.entrySet()) {
            String first = entry.getKey();
            boolean haveANYfirst = first.equals(XLocaleDistance.ANY);
            for (String second : entry.getValue()) {
                haveANYANY |= haveANYfirst && second.equals(XLocaleDistance.ANY);
                DistanceNode distanceNode = internalGetDistanceTable.getInternalNode(first, second);
                DistanceTable subDistanceTable = distanceNode.getDistanceTable();
                if (subDistanceTable == null || subDistanceTable.isEmpty()) {
                    if (depth != 3) {
                        logln("depth should be 3");
                    }
                    if (distanceNode.getClass() != DistanceNode.class) {
                        logln("should be plain DistanceNode");
                    }
                } else {
                    if (depth >= 3) {
                        logln("depth should be ≤ 3");
                    }
                    if (distanceNode.getClass() == DistanceNode.class) {
                        logln("should NOT be plain DistanceNode");
                    }
                    checkTables(subDistanceTable, first + "," + second + ",", depth + 1);
                }
            }
        }
        if (!haveANYANY) {
            logln("ANY-ANY not in" + matches);
        }
    }

    public void testDataDriven() throws IOException {
        tfh.test();
        if (REFORMAT) {
            System.out.println(tfh.appendLines(new StringBuffer()));
        }
    }

    class MyTestFileHandler extends DataDrivenTestHelper {
        final XLocaleDistance distance = XLocaleDistance.getDefault();
        Output<ULocale> bestDesired = new Output<ULocale>();
        private DistanceOption distanceOption = DistanceOption.NORMAL;
        private Integer threshold = distance.getDefaultScriptDistance();

        @Override
        public void handle(boolean breakpoint, String commentBase, List<String> arguments) {
            if (breakpoint) {
                breakpoint = false; // put debugger breakpoint here to break at @debug in test file
            }
            Arguments args = new Arguments(arguments);
            int supportedToDesiredActual =
                    distance.distance(args.supported, args.desired, threshold, distanceOption);
            int desiredToSupportedActual =
                    distance.distance(args.desired, args.supported, threshold, distanceOption);
            String desiredTag = args.desired.toLanguageTag();
            String supportedTag = args.supported.toLanguageTag();
            final String comment = commentBase.isEmpty() ? "" : "\t# " + commentBase;
            if (assertEquals(
                    desiredTag + " to " + supportedTag + comment,
                    args.desiredToSupported,
                    desiredToSupportedActual)) {
                assertEquals(
                        supportedTag + " to " + desiredTag + comment,
                        args.supportedToDesired,
                        supportedToDesiredActual);
            }
        }

        @Override
        public void handleParams(String comment, List<String> arguments) {
            switch (arguments.get(0)) {
                case "@DistanceOption":
                    distanceOption = DistanceOption.valueOf(arguments.get(1));
                    break;
                case "@Threshold":
                    threshold = Integer.valueOf(arguments.get(1));
                    break;
                default:
                    super.handleParams(comment, arguments);
                    break;
            }
            return;
        }
    }
}
