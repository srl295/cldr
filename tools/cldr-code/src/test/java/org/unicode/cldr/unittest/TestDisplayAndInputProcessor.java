package org.unicode.cldr.unittest;

import java.util.Set;

import org.unicode.cldr.test.DisplayAndInputProcessor;
import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.CLDRFile.ExemplarType;
import org.unicode.cldr.util.Factory;

import com.ibm.icu.dev.test.TestFmwk;
import com.ibm.icu.lang.CharSequences;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.text.UnicodeSetIterator;

public class TestDisplayAndInputProcessor extends TestFmwk {

    CLDRConfig info = CLDRConfig.getInstance();

    public static void main(String[] args) {
        new TestDisplayAndInputProcessor().run(args);
    }

    public void TestAll() {
        showCldrFile(info.getEnglish());
        showCldrFile(info.getCLDRFile("ar", true));
        showCldrFile(info.getCLDRFile("ja", true));
        showCldrFile(info.getCLDRFile("hi", true));
        showCldrFile(info.getCLDRFile("wae", true));
    }

    public void TestAExemplars() {
        UnicodeSet test = new UnicodeSet();
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info.getEnglish(), true);
        Exception[] internalException = new Exception[1];

        for (String s : new UnicodeSet("[!-#%-\\]_a-~¡§ª-¬±-³ µ-·¹-þ؉٠-٬۰-۹०-९০-৯੦-੯ ૦-૯୦-୯௦-௯౦-౯೦-೯൦-൯༠-༩ ၀-၉\\‎\\‏’‰−〇一七三九二五八六四]")) {
            if (s.contentEquals("-")) {
                continue; // special case because of non-breaking processing
            }
            test.clear().add(s);
            String value = test.toPattern(false);
            String path = CLDRFile.getExemplarPath(ExemplarType.numbers);

            String display = daip.processForDisplay(path, value);
            internalException[0] = null;
            String input = daip.processInput(path, display, internalException);

            try {
                UnicodeSet roundTrip = new UnicodeSet(input);
                if (!assertEquals(test.toString() + "=>" + display, test, roundTrip)) {
                    input = daip.processInput(path, display, internalException); // for debugging
                }
            } catch (Exception e) {
                errln(test.toString() + "=>" + display + ": Failed to parse " + input);
            }
        }
    }

    public void TestTasawaq() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info
            .getCLDRFile("twq", true));
        // time for data driven test
        final String input = "[Z \u017E ]";
        final String expect = "[z \u017E]"; // lower case
        String value = daip.processInput(
            "//ldml/characters/exemplarCharacters", input, null);
        if (!value.equals(expect)) {
            errln("Tasawaq incorrectly normalized with output: '" + value
                + "', expected '" + expect + "'");
        }
    }

    public void TestMalayalam() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info
            .getCLDRFile("ml", false));
        String value = daip.processInput(
            "//ldml/localeDisplayNames/languages/language[@type=\"alg\"]",
            "അല്‍ഗോണ്‍ക്യന്‍ ഭാഷ", null);
        if (!value
            .equals("\u0D05\u0D7D\u0D17\u0D4B\u0D7A\u0D15\u0D4D\u0D2F\u0D7B \u0D2D\u0D3E\u0D37")) {
            errln("Malayalam incorrectly normalized with output: " + value);
        }
    }

    public void TestRomanian() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info
            .getCLDRFile("ro", false));
        String value = daip
            .processInput(
                "//ldml/localeDisplayNames/types/type[@type=\"hant\"][@key=\"numbers\"]",
                "Numerale chineze\u015Fti tradi\u0163ionale", null);
        if (!value.equals("Numerale chineze\u0219ti tradi\u021Bionale")) {
            errln("Romanian incorrectly normalized: " + value);
        }
    }

    /**
     * Good strings.
     */
    public final String MY_UNICODE[] = {
        // Examples from CLDR-13956, should not be changed
        "အနန္တ",
        "အစား လက္ခဏာ",
        "ဟိန္ဒူ",
        "မက္ကဆီကို ပီဆို",
        "သဲကန္တာရ",
        "ရထားလမ်း | ရထား သံလမ်း | ရထား",
        "တက္ကစီ",
        "အန္တာတိက",
        "ဇူးရစ်ချ်",
        "ချပ်ချ် စလာဗစ်",
        // other examples
        "\u1019\u101B\u103E\u102D\u101E\u1031\u102C",
        "\u1001\u103B\u103c"
    };

    /**
     * Zawgyi Strings. Should be flagged.
     */
    public final String MY_ZAWGYI[] = {
        "ေမာင္းရီ (နယူးဇီလန္ကၽြန္းရွိ ပင္ရင္းတိုင္းရင္းသားလူမ်ိဳး)",
        "ABCDE ေမာင္းရီ (နယူးဇီလန္ကၽြန္းရွိ ပင္ရင္းတိုင္းရင္းသားလူမ်ိဳး) XYZ",
        "\u1031\u1019\u102c\u1004\u1039\u1038\u101b\u102e\u0020\u0028\u1014"
            + "\u101A\u1030\u1038\u1007\u102E\u101C\u1014\u1039\u1000\u107D\u103C\u1014\u1039\u1038\u101B\u103D\u102D",
            "\u1000\u1005\u102C\u1038\u101E\u1019\u102C\u1038",
            "\u1021\u101E\u1004\u1039\u1038\u1019\u103D",
            "\u1031\u1040\u1037",
            "\u1041\u1040\u1037",
            "\u1001\u103E\u103A"
    };

    public void TestMyanmarZawgyi() {
        // Check that the Zawgyi detector and Zawgyi->Unicode converter perform
        // correctly.
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info
            .getCLDRFile("my", false));

        for(final String testGoodString  : MY_UNICODE) {
            final String check_is_unicode = daip.processInput("", testGoodString, null);
            if (!check_is_unicode.equals(testGoodString)) {
                errln("Myanmar should not have converted:\n" + testGoodString + " to\n"
                    + check_is_unicode);
            }
        }
    }

    public void TestCompactNumberFormats() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(
            info.getEnglish(), false);
        String xpath = "//ldml/numbers/decimalFormats[@numberSystem=\"latn\"]/decimalFormatLength[@type=\"long\"]/decimalFormat[@type=\"standard\"]/pattern[@type=\"1000\"] ";
        String value = daip.processInput(xpath, "0.00K.", null);
        assertEquals("Period not correctly quoted", "0K'.'", value);
        value = daip.processInput(xpath, "00.0K'.'", null);
        assertEquals("Quotes should not be double-quoted", "00K'.'", value);
        value = daip.processForDisplay(xpath, "0.0 K'.'");
        assertEquals("There should be no quotes left", "0.0 K.", value);
    }

    public void TestPatternCanonicalization() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(
            info.getEnglish(), false);
        String xpath = "//ldml/numbers/decimalFormats[@numberSystem=\"latn\"]/decimalFormatLength/decimalFormat[@type=\"standard\"]/pattern[@type=\"standard\"]";
        String value = daip.processInput(xpath, "#,###,##0.###", null);
        assertEquals("Format not correctly canonicalized", "#,##0.###", value);
    }

    public void TestCurrencyFormatSpaces() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(
            info.getEnglish(), false);
        String xpath = "//ldml/numbers/currencyFormats[@numberSystem=\"latn\"]/currencyFormat[@type=\"standard\"]/pattern[@type=\"standard\"]";
        String value = daip.processInput(xpath, "¤ #,##0.00", null); // breaking
        // space
        assertEquals("Breaking space not replaced", "¤ #,##0.00", value); // non-breaking
        // space
    }

    private Boolean usesModifierApostrophe(CLDRFile testFile) {
        char MODIFIER_LETTER_APOSTROPHE = '\u02BC';
        String exemplarSet = testFile
            .getWinningValue("//ldml/characters/exemplarCharacters");
        UnicodeSet mainExemplarSet = new UnicodeSet(exemplarSet);
        UnicodeSetIterator usi = new UnicodeSetIterator(mainExemplarSet);
        while (usi.next()) {
            if (usi.codepoint == MODIFIER_LETTER_APOSTROPHE
                || (usi.codepoint == UnicodeSetIterator.IS_STRING && usi
                    .getString().indexOf(MODIFIER_LETTER_APOSTROPHE) >= 0)) {
                return true;
            }
        }
        return false;
    }

    public void TestModifierApostropheLocales() {
        Factory f = info.getFullCldrFactory();
        Set<String> allLanguages = f.getAvailableLanguages();
        for (String thisLanguage : allLanguages) {
            CLDRFile thisLanguageFile = f.make(thisLanguage, true);
            try {
                if (usesModifierApostrophe(thisLanguageFile)) {
                    if (!DisplayAndInputProcessor.LANGUAGES_USING_MODIFIER_APOSTROPHE
                        .contains(thisLanguage)) {
                        errln("Language : "
                            + thisLanguage
                            + " uses MODIFIER_LETTER_APOSROPHE, but is not on the list in DAIP.LANGUAGES_USING_MODIFIER_APOSTROPHE");
                    }
                } else {
                    if (DisplayAndInputProcessor.LANGUAGES_USING_MODIFIER_APOSTROPHE
                        .contains(thisLanguage)) {
                        errln("Language : "
                            + thisLanguage
                            + "is on the list in DAIP.LANGUAGES_USING_MODIFIER_APOSTROPHE, but the main exemplars don't use this character.");
                    }
                }
            } catch(Throwable t) {
                t.printStackTrace();
                errln("Error in " + thisLanguage + " - " + t.getMessage());
            }
        }
    }

    public void TestQuoteNormalization() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(
            info.getEnglish(), false);
        String xpath = "//ldml/units/unitLength[@type=\"narrow\"]/unitPattern[@count=\"one\"]";
        String value = daip.processInput(xpath, "{0}''", null); // breaking
        // space
        assertEquals("Quotes not normalized", "{0}″", value); // non-breaking
        // space
    }

    public void TestAdlamNasalization() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info
            .getCLDRFile("ff_Adlm", false));
        final String xpath_a = "//ldml/localeDisplayNames/types/type[@type=\"hant\"][@key=\"numbers\"]";
        final String TEST_DATA[] = {
            xpath_a,         // xpath
            "{0} 𞤸𞤭𞤼𞤢𞥄𞤲'𞤣𞤫",  // src
            "{0} 𞤸𞤭𞤼𞤢𞥄𞤲"+DisplayAndInputProcessor.ADLAM_NASALIZATION+"𞤣𞤫",   // dst

            xpath_a,         // xpath
            "𞤐‘𞤄𞤵𞥅𞤯𞤭",  // src
            "𞤐"+DisplayAndInputProcessor.ADLAM_NASALIZATION+"𞤄𞤵𞥅𞤯𞤭",   // dst

            xpath_a,
            "𞤑𞤭𞤶𞤮𞥅𞤪𞤫 𞤖𞤢𞤱𞤪𞤭𞤼𞤵𞤲‘𞤣𞤫 𞤖𞤭𞥅𞤪𞤲𞤢𞥄𞤲‘𞤺𞤫 𞤘𞤪𞤭𞤲𞤤𞤢𞤲𞤣",
            "𞤑𞤭𞤶𞤮𞥅𞤪𞤫 𞤖𞤢𞤱𞤪𞤭𞤼𞤵𞤲"+DisplayAndInputProcessor.ADLAM_NASALIZATION+"𞤣𞤫 𞤖𞤭𞥅𞤪𞤲𞤢𞥄𞤲"
                +DisplayAndInputProcessor.ADLAM_NASALIZATION+"𞤺𞤫 𞤘𞤪𞤭𞤲𞤤𞤢𞤲𞤣",

            xpath_a, // no change
            "'Something' ‘Else’",
            "‘Something’ ‘Else’" // smart quotes
        };
        for (int i=0; i<TEST_DATA.length; i+= 3) {
            final String xpath = TEST_DATA[i+0];
            final String src   = TEST_DATA[i+1];
            final String dst   = TEST_DATA[i+2];

            String value = daip.processInput(xpath, src, null);
            assertEquals("ff_Adlm: " + src, dst, value);
        }
    }

    private void showCldrFile(final CLDRFile cldrFile) {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(cldrFile,
            true);
        Exception[] internalException = new Exception[1];
        for (String path : cldrFile) {
            String value = cldrFile.getStringValue(path);
            if (value.equals("[\\- , . % ‰ + 0-9]")) {
                int debug = 0;
            }
            String display = daip.processForDisplay(path, value);
            internalException[0] = null;
            String input = daip.processInput(path, display, internalException);
            String diff = diff(value, input, path);
            if (diff != null) {
                errln(cldrFile.getLocaleID() + "\tNo roundtrip in DAIP:"
                    + "\n\t  value<"
                    + value
                    + ">\n\tdisplay<"
                    + display
                    + ">\n\t  input<"
                    + input
                    + ">\n\t   diff<"
                    + diff
                    + (internalException[0] != null ? ">\n\texcep<"
                        + internalException[0] : "")
                    + ">\n\tpath<"
                    + path + ">");
                daip.processInput(path, value, internalException); // for
                // debugging
            } else if (!CharSequences.equals(value, display)
                || !CharSequences.equals(value, input)
                || internalException[0] != null) {
                logln("DAIP Changes"
                    + "\n\tvalue<"
                    + value
                    + ">\n\tdisplay<"
                    + display
                    + ">\n\tinput<"
                    + input
                    + ">\n\tdiff<"
                    + diff
                    + (internalException[0] != null ? ">\n\texcep<"
                        + internalException[0] : "")
                    + ">\n\tpath<"
                    + path + ">");
            }
        }
    }

    private String diff(String value, String input, String path) {
        if (value.equals(input)) {
            return null;
        }
        if (path.contains("/exemplarCharacters") || path.contains("/parseLenient")) {
            try {
                UnicodeSet s1 = new UnicodeSet(value);
                UnicodeSet s2 = new UnicodeSet(input);
                if (!s1.equals(s2)) {
                    UnicodeSet temp = new UnicodeSet(s1).removeAll(s2);
                    UnicodeSet temp2 = new UnicodeSet(s2).removeAll(s1);
                    temp.addAll(temp2);
                    return temp.toPattern(true);
                }
                return null;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        String value2 = value.replace('[', '(').replace(']', ')')
            .replace('［', '（').replace('］', '）');
        if (value2.equals(input)) {
            return null;
        }
        return "?";
    }

    /**
     * Test whether DisplayAndInputProcessor.processInput removes backspaces
     */
    public void TestBackspaceFilter() {
        DisplayAndInputProcessor daip = new DisplayAndInputProcessor(info.getEnglish(), false);
        String xpath = "//ldml/localeDisplayNames/languages/language[@type=\"fr\"]";
        String value = daip.processInput(xpath, "\btest\bTEST\b", null);
        assertEquals("Backspaces are filtered out", "testTEST", value);
    }
}
