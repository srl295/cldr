package org.unicode.cldr.util.keyboard;

import java.util.Collection;
import java.util.function.Function;
import org.unicode.cldr.util.keyboard.KeyboardValidator.KeyboardStatus;

/** parent class for validating subcomponents of Keyboard files */
public abstract class KeyboardStringValidator implements Function<String, Collection<KeyboardStatus>> {
    /** helper function, return true if no errors present */
    public boolean valid(final String s) {
        for (final KeyboardStatus t : apply(s)) {
            if (t.getValidity().isError()) return false;
        }
        return true;
    }
    /** helper function, throw on first error */
    public void throwIfInvalid(final String s) throws KeyboardStatus {
        for (final KeyboardStatus t : apply(s)) {
            t.throwIfError();
        }
    }
}
