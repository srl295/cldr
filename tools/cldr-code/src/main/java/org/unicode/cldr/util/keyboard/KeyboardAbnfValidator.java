package org.unicode.cldr.util.keyboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.unicode.cldr.util.keyboard.KeyboardValidator.KeyboardStatus;
import org.unicode.cldr.util.keyboard.KeyboardValidator.KeyboardValidity;

import apg.Grammar;
import apg.Parser;
import apg.Trace;

/** validate against a generated rule. */
public class KeyboardAbnfValidator extends KeyboardStringValidator {
    private Grammar grammar;

    KeyboardAbnfValidator(final Grammar grammar) {
            this.grammar = grammar;
    }

    @Override
    public Collection<KeyboardStatus> apply(String t) {
        final List<KeyboardStatus> r = new ArrayList<KeyboardStatus>();
        final Parser p = getParser(t);
        try {
            final Parser.Result res = p.parse();
            if (!res.success()) {
                r.add(new KeyboardStatus(KeyboardValidity.error, "Failed to Parse"));
            }
        } catch(Throwable e) {
            r.add(new KeyboardStatus(KeyboardValidity.error, e.toString()));
        }

        return r;
    }

    private Parser getParser(String t) {
        final Parser p = new Parser(grammar);
        p.setInputString(t);
        return p;
    }

    /** for debugging - prints  trace to stdout */
    void trace(final String t) throws Exception {
        final Parser p = getParser(t);
        p.enableTrace(true);
        p.parse();
        System.out.println();
    }

    /** parse transform from= strings */
    public static KeyboardAbnfValidator getFromValidator() {
        return new KeyboardAbnfValidator(ParseFrom.getInstance());
    }
    /** parse transform to= strings */
    public static KeyboardAbnfValidator getToValidator() {
        return new KeyboardAbnfValidator(ParseTo.getInstance());
    }
}
