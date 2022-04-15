package org.unicode.cldr.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class SpecialEntityResolver implements EntityResolver {

    private URI getUrlFor(String publicId) {
        final File base = CLDRConfig.getInstance().getCldrBaseDirectory();
        final File k = new File(base, "keyboards");
        final File d = new File(k, "dtd");
        final File dtd = new File(d, "ldmlKeyboard3.dtd");

        return dtd.toURI();
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        System.err.println(publicId + " and " + systemId);
        // Map public id to a local file
        if (publicId.equals("+//IDN unicode.org//DTD LDMLKEYBOARD/EN")) {
            System.out.println("I’m on it!");
            return new InputSource(getUrlFor(publicId).toString());
        }
        return null;
    }
}
