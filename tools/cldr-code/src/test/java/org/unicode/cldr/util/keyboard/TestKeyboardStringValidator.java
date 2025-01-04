package org.unicode.cldr.util.keyboard;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.keyboard.KeyboardValidator.KeyboardStatus;

public class TestKeyboardStringValidator {
    private static final String GOODFROM = "ab(cd\\u{1234}ef){2,3}";
    private static final String BADFROM = "look.*i'm.*unbounded";
    private static final String GOODTO = "You picked: IOException";
    private static final String BADTO = "$[8:abc]";

    @Test
    void testGoodFrom() {
        final KeyboardAbnfValidator fromValidator = KeyboardAbnfValidator.getFromValidator();
        final Collection<KeyboardStatus> goodList = fromValidator.apply(GOODFROM);
        assertTrue(goodList.isEmpty());
        assertTrue(fromValidator.valid(GOODFROM));
        assertDoesNotThrow(()->fromValidator.throwIfInvalid(GOODFROM));
    }
    @Test
    void testBadFrom() {
        final KeyboardAbnfValidator fromValidator = KeyboardAbnfValidator.getFromValidator();
        final Collection<KeyboardStatus> badList = fromValidator.apply(BADFROM);
        assertFalse(badList.isEmpty());
        assertFalse(fromValidator.valid(BADFROM));
        assertThrows(KeyboardStatus.class, ()->fromValidator.throwIfInvalid(BADFROM));
    }
    @Test
    void testGoodTo() {
        final String s = GOODTO;
        final KeyboardAbnfValidator toValidator = KeyboardAbnfValidator.getToValidator();
        final Collection<KeyboardStatus> list = toValidator.apply(s);
        assertTrue(list.isEmpty());
        assertTrue(toValidator.valid(s));
        assertDoesNotThrow(()->toValidator.throwIfInvalid(s));
    }
    @Test
    void testBadTo() {
        final String s = BADTO;
        final KeyboardAbnfValidator fromValidator = KeyboardAbnfValidator.getFromValidator();
        final Collection<KeyboardStatus> list = fromValidator.apply(s);
        assertFalse(list.isEmpty());
        assertFalse(fromValidator.valid(s));
        assertThrows(KeyboardStatus.class, ()->fromValidator.throwIfInvalid(s));
    }


    void testDataDriven(KeyboardStringValidator v, List<String> cases, boolean shouldValidate) {
        for (int i = 0; i < cases.size(); i++) {
            final int n = i + 1;
            final String s = cases.get(i);
            if (s.startsWith("#")) continue; // skip comments
            if (s.contains("𐒵")) {
                System.err.println("TODO: parser not Unicode friendly? Skipping " + s);
                continue;
            }
            assertEquals(shouldValidate, v.valid(s), () -> {
                try {
                    ((KeyboardAbnfValidator)v).trace(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "#" + n + ": " + s;
            });
        }
    }

    void testDataDriven(KeyboardStringValidator v, String stubName, boolean shouldValidate) throws IOException {
        final Path cldrDir = CLDRConfig.getInstance().getCldrBaseDirectory().toPath();
        final Path testFile = cldrDir.resolve("./tools/scripts/keyboard-abnf-tests/"+stubName+".txt");
        final List<String> cases = Files.readAllLines(testFile);
        testDataDriven(v, cases, shouldValidate);
    }

    @Test
    void testDataFromPass() throws IOException {
        testDataDriven(KeyboardAbnfValidator.getFromValidator(), "transform-from-required.d/from-match.pass", true);
    }
    @Test
    void testDataFromFail() throws IOException {
        testDataDriven(KeyboardAbnfValidator.getFromValidator(), "transform-from-required.d/from-match.fail", false);
    }
    @Test
    void testDataToPass() throws IOException {
        testDataDriven(KeyboardAbnfValidator.getFromValidator(), "transform-to-required.d/to-replacement.pass", true);
    }
    @Test
    void testDataToFail() throws IOException {
        testDataDriven(KeyboardAbnfValidator.getFromValidator(), "transform-to-required.d/to-replacement.fail", false);
    }
}
