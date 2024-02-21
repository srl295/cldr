package org.unicode.cldr.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.xml.sax.InputSource;

public class DTDInsertingReaderFactory {
    /** Size of the input buffer, needs to be able to contain up to the xmlns=" .. portion */
    public static int BUFFER_MAX_SIZE = 1024;
    public static int BUFFER_SIZE = 512;
    /**
     * Wrap a Reader in something that will automatically insert a DTD reference in place of an
     * xmlns directive.
     * @throws IOException
     */
    public static void wrap(InputSource src) throws IOException {
        Reader r = src.getCharacterStream();
        InputStream is = src.getByteStream();
        if (r != null) {
            src.setCharacterStream(wrap(r));
        } else if(is != null) {
            src.setByteStream(wrap(is));
        } else {
            throw new NullPointerException("Internal error: Character and Byte stream are both null");
        }
    }
    public static InputStream wrap(InputStream src) throws IOException {
        PushbackInputStream pr = new PushbackInputStream(src, BUFFER_MAX_SIZE);
        byte inbuf[] = pr.readNBytes(BUFFER_SIZE);
        if(!hasDocType(inbuf)) {
            inbuf = fixup(inbuf).getBytes(StandardCharsets.ISO_8859_1);
        }
        pr.unread(inbuf);
        return pr;
    }
    private static String fixup(byte[] inbuf) {
        final String s = new String(inbuf, StandardCharsets.ISO_8859_1);
        return fixup(s);
    }
    private static String fixup(final String s) {
        // exit if nothing matches
        for (final DtdType d : DtdType.values()) {
            if (s.contains("xmlns=\""+d.getNsUrl())) {
                return fixup(s, d);
            }
        }
        throw new IllegalArgumentException("No <!DOCTYPE> and no recognized xmlns= attribute.");
    }
    private static String fixup(String s, DtdType d) {
        int n = s.indexOf("?>");
        if (n == -1) {
            throw new IllegalArgumentException("Invalid XML prefix: ?> not found.");
        }

        final String doctype = "\n" + d.getDoctype() + "\n";
        // drop all xmlns because Xerces chokes on it!
        final String s2 = s.substring(0, n + 2) + doctype + s.substring(n + 2).replaceAll("xmlns=\"[^\"]*\"", "");

        if (false) System.out.println(s2); // DEBUG: print out updated header
        return s2;
    }

    public static Reader wrap(Reader src) throws IOException {
        PushbackReader pr = new PushbackReader(src, BUFFER_MAX_SIZE);
        final char inbuf[] = new char[BUFFER_SIZE];
        final int readlen = pr.read(inbuf);
        if(hasDocType(inbuf, readlen)) {
            pr.unread(inbuf, 0, readlen);
        } else {
            throw new UnsupportedOperationException("TODO: wrap(Reader)");
        }
        return pr;
    }
    private static boolean hasDocType(byte[] inbuf) {
        if (inbuf == null || inbuf.length == 0) return false;
        final String s = new String(inbuf, StandardCharsets.ISO_8859_1); /** for this matching invalid utf-8 is not relevant */
        return hasDocType(s.toCharArray(), s.length());
    }

    private static boolean hasDocType(char[] inbuf, int readlen) {
        if (inbuf == null || readlen <= 0) {
            return false;
        }
        final String s = new String(inbuf, 0, readlen);
        return (s.contains("<!DOCTYPE"));
    }
}
