package org.unicode.cldr.api.example;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.unicode.cldr.api.AttributeKey;
import org.unicode.cldr.api.CldrData;
import org.unicode.cldr.api.CldrData.PathOrder;
import org.unicode.cldr.api.CldrDataSupplier;
import org.unicode.cldr.api.CldrDataSupplier.CldrResolution;
import org.unicode.cldr.api.CldrPath;
import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.CLDRLocale;
import org.unicode.cldr.util.Pair;

import com.ibm.icu.text.Transform;
import com.ibm.icu.util.ULocale;

/**
 * Sample user of the CLDR API. This class implements a
 * {@link CLDRLocale.NameFormatter} which is used to, surprisingly, format
 * CLDRLocales.
 * 
 * Sample usage:  
 *  File cldrDir = CLDRConfig.getInstance().getCldrBaseDirectory();
 *  CLDRLocale dispLoc = CLDRLocale.getInstance("fr");
 *  new CLDRNameFormatter(cldrDir, dispLoc, true).getDisplayName(CLDRLocale.getInstance("en_US"));
 * 
 * @author srl
 *
 */
public class CLDRNameFormatter implements CLDRLocale.NameFormatter {
    private static final boolean DEBUG = false;
    
    private static final Set<String> KNOWN_ELEMENTS = new HashSet<>();

    static {
        // Only care about these elements.
        Collections.addAll(KNOWN_ELEMENTS, "language", "script", "territory", "variant");
    }

    private CldrData data;

    private final boolean isLong;

    private ConcurrentMap<String, ConcurrentMap<Pair<String, String>, String>> map = new ConcurrentHashMap<>();

    private CldrDataSupplier supplier;

    /**
     * Construct a CLDRNameFormatter from the specified parameters
     * @param rootDir
     * @param displayLocale
     * @param isLong
     */
    public CLDRNameFormatter(File rootDir, CLDRLocale displayLocale, boolean isLong) {
        this.isLong = isLong;
        if(DEBUG) System.err.println("## STARTING..");
        this.supplier = CldrDataSupplier.forCldrFilesIn(rootDir.toPath());
        if(DEBUG) System.err.println("## GETTING..");
        this.data = supplier.getDataForLocale(displayLocale.getBaseName(), CldrResolution.RESOLVED);
        if(DEBUG) System.err.println("## READING..");
        
        // ingest all data into a big hashmap
        data.accept(PathOrder.ARBITRARY, (v) -> {
            CldrPath path = v.getPath();
            final String element = path.getName();
            // Shortcut if we aren't in the area of interest.
            if (!path.toString().startsWith(("//ldml/localeDisplayNames")) || !KNOWN_ELEMENTS.contains(element))
                return;
            final String type = path.get(AttributeKey.keyOf(element, "type"));
            final String alt = path.get(AttributeKey.keyOf(element, "alt"));
            final String value = v.getValue();

            if(DEBUG) System.err.println(element + "/" + type + "/" + alt + "=" + value);

            map.computeIfAbsent(element, e -> new ConcurrentHashMap<>()).put(computeKey(type, alt), value);
        });
        if(DEBUG) System.err.println("## LOADED.");
    }

    private String altParameter() {
        return isLong ? "long" : null;
    }

    private Pair<String, String> computeKey(final String type, final String alt) {
        return new Pair<>(type != null ? type : "", alt != null ? alt : "");
    }

    /**
     * Data accessor
     * @param element element name
     * @param type type attribute
     * @param alt alt attribute or null
     * @return
     */
    private String get(String element, String type, String alt) {
        ConcurrentMap<Pair<String, String>, String> subMap = map.get(element);
        if (subMap == null) {
            return null;
        }
        if (alt != null && !alt.isEmpty()) {
            Pair<String, String> specialKey = computeKey(type, alt);
            String v = subMap.get(specialKey);
            if (v != null)
                return v;
        }
        Pair<String, String> defaultKey = computeKey(type, "");
        String v = subMap.get(defaultKey); // default
        if (v != null)
            return v;

        return type; // fallback
    }


    @Override
    public String getDisplayName(CLDRLocale cldrLocale) {
        StringBuffer sb = new StringBuffer();
        String l = cldrLocale.getLanguage();
        String s = cldrLocale.getScript();
        String r = cldrLocale.getCountry();
        String v = cldrLocale.getVariant();

        if (l != null && !l.isEmpty()) {
            sb.append(getDisplayLanguage(cldrLocale));
        } else {
            sb.append("?");
        }
        if ((s != null && !s.isEmpty()) || (r != null && !r.isEmpty()) || (v != null && !v.isEmpty())) {
            sb.append(" (");
            if (s != null && !s.isEmpty()) {
                sb.append(getDisplayScript(cldrLocale)).append(",");
            }
            if (r != null && !r.isEmpty()) {
                sb.append(getDisplayCountry(cldrLocale)).append(",");
            }
            if (v != null && !v.isEmpty()) {
                sb.append(getDisplayVariant(cldrLocale)).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
        }
        return sb.toString();
    }

    @Override
    public String getDisplayName(CLDRLocale cldrLocale, boolean onlyConstructCompound,
            Transform<String, String> altPicker) {
        return getDisplayName(cldrLocale);
    }

    @Override
    public String getDisplayCountry(CLDRLocale cldrLocale) {
        return get("territory", cldrLocale.getCountry(), altParameter());
    }

    @Override
    public String getDisplayLanguage(CLDRLocale cldrLocale) {
        return get("language", cldrLocale.getLanguage(), altParameter());
    }

    @Override
    public String getDisplayScript(CLDRLocale cldrLocale) {
        return get("script", cldrLocale.getLanguage(), altParameter());
    }

    @Override
    public String getDisplayVariant(CLDRLocale cldrLocale) {
        return get("variant", cldrLocale.getLanguage(), altParameter());
    }
    
    /**
     * Test code: format the arguments as bcp47 codes
     * @param args
     */
    public static void main(String args[]) {
        System.err.println("Starting up...");
        File baseDirectory = CLDRConfig.getInstance().getCldrBaseDirectory();
        CLDRLocale locale = CLDRLocale.getInstance(ULocale.getDefault());
        System.err.println("Defaults: " + locale + " @ " + baseDirectory);
        CLDRLocale.NameFormatter nf = new CLDRNameFormatter(baseDirectory,
                                        locale,
                                        true);
        if(args.length == 0) {
            String defaultList[] = { ULocale.getDefault().toLanguageTag() };
            args = defaultList;
        }
        for (final String s : args) {
            CLDRLocale l = CLDRLocale.getInstance(ULocale.forLanguageTag(s));
            System.out.println(s + ": " + l.getDisplayName(nf));
        }
    }
}