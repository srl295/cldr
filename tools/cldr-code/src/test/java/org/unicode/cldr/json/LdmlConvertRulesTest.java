package org.unicode.cldr.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.unicode.cldr.json.LdmlConvertRules.SplittableAttributeSpec;
import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.DtdData;
import org.unicode.cldr.util.DtdData.Attribute;
import org.unicode.cldr.util.DtdData.Element;
import org.unicode.cldr.util.DtdType;
import org.unicode.cldr.util.MatchValue;
import org.unicode.cldr.util.Pair;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

class LdmlConvertRulesTest {
    final File cldrDir = CLDRConfig.getInstance().getCldrBaseDirectory();
    final DtdData dtds[] = {
        DtdData.getInstance(DtdType.supplementalData, cldrDir),
        DtdData.getInstance(DtdType.ldml, cldrDir)
    };

    @Test
    void testSplittableAttributes() {
        // collect JSON list
        Set<Pair<String,String>> jsonSplittableAttrs = new TreeSet<>();
        for(final SplittableAttributeSpec e : LdmlConvertRules.getSplittableAttrs()) {
            jsonSplittableAttrs.add(Pair.of(e.element, e.attribute));
        }

        // collect DTD list
        Set<Pair<String,String>> dtdSplittableAttrs = new TreeSet<>();
        for(final DtdData dtd: dtds) {
            System.out.println("\n** " + dtd.dtdType);
            for(final Element element : dtd.getElements()) {
                if(element.getAttributes() == null) continue; // ?
                for(final Entry<Attribute, Integer> q : element.getAttributes().entrySet()) {
                    Attribute attr = q.getKey();
                    if(attr.matchValue != null && attr.matchValue instanceof MatchValue.SetMatchValue) {
                        // NAME_PART_DISTINGUISHING_ATTR_SET
                        dtdSplittableAttrs.add(Pair.of(element.name, attr.name));
                    }
                }
            }
        }

        SetView<Pair<String, String>> onlyInDtd = Sets.difference(dtdSplittableAttrs, jsonSplittableAttrs);
        SetView<Pair<String, String>> onlyInJson = Sets.difference(jsonSplittableAttrs, dtdSplittableAttrs);
        if(!onlyInDtd.isEmpty()) {
            System.err.println("Only in DTD, missing from SPLITTABLE_ATTRS: " + onlyInDtd);
        }
        if(!onlyInJson.isEmpty()) {
            System.err.println("Only in JSON, missing from DTD (!): " + onlyInJson);
        }

        assertEquals(dtdSplittableAttrs, jsonSplittableAttrs, "expected JSON to match DTD");

    }

}
