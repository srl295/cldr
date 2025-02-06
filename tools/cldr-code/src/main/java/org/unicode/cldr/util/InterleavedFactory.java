package org.unicode.cldr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.unicode.cldr.util.CLDRFile.DraftStatus;
import org.unicode.cldr.util.SimpleFactory.NoSourceDirectoryException;

/**
 * A Factory with multiple source directories - and resolution occurs including all directories.
 * In other words, data items can be interleaved between directories (such as with subdivision data).
 * This class does not perform the high level of caching that SimpleFactory currently does.
 */
public class InterleavedFactory extends Factory {

    final private File[] sourceDirectories;
    private DraftStatus minimalDraftStatus;
    private Set<String> localeList;
    private final Factory[] simpleFactories;

    /**
     * Create an InterleavedFactory instance.
     * Locales will be included in the specified order.
     */
    public InterleavedFactory(File sourceDirectories[], DraftStatus minimalDraftStatus) {
        this.sourceDirectories = sourceDirectories;
        this.minimalDraftStatus = minimalDraftStatus;
        Matcher m = PatternCache.get(".*").matcher("");
        this.localeList = CLDRFile.getMatchingXMLFiles(sourceDirectories, m);
        simpleFactories = new Factory[sourceDirectories.length];
        for (int i = 0; i < sourceDirectories.length; i++) {
            File farray[] = new File[1];
            farray[0] = sourceDirectories[i];
            simpleFactories[i] = SimpleFactory.make(farray, ".*", minimalDraftStatus);
        }
    }

    @Override
    public File[] getSourceDirectories() {
        return sourceDirectories;
    }

    @Override
    protected CLDRFile handleMake(String localeID, boolean resolved, DraftStatus madeWithMinimalDraftStatus) {
        if (resolved == false) {
            throw new IllegalArgumentException("resolved must be true");
        }
        final XMLSource resolvingSource = makeXmlSource(localeID, resolved, madeWithMinimalDraftStatus);
        final CLDRFile sf = new CLDRFile(resolvingSource);
        return sf;
    }

    private XMLSource makeXmlSource(String localeID, boolean resolved, DraftStatus madeWithMinimalDraftStatus) {
        // buld up the chain
        CLDRLocale l = CLDRLocale.getInstance(localeID);
        List<XMLSource> list = new LinkedList<>();
        while (l != null) {
            for (final Factory f : simpleFactories) {
                try {
                    list.add(f.makeSource(l.getBaseName()));
                    System.out.println("Added: " + f.getSourceDirectory() + " / " + l.getDisplayName() + " / " + l);
                } catch(NoSourceDirectoryException nsd) {
                    // ignore this one
                }
            }
            l = l.getParent(); // null if we hit the end
        }
        return Factory.makeResolvingSource(list);
    }

    @Override
    public DraftStatus getMinimalDraftStatus() {
        return minimalDraftStatus;
    }

    @Override
    protected Set<String> handleGetAvailable() {
        return localeList;
    }

    @Override
    public List<File> getSourceDirectoriesForLocale(String localeName) {
        if (!localeList.contains(localeName)) {
            return List.of();
        }

        // delegate to SimpleFactory
        final List<File> l = new LinkedList<>();
        for (final Factory f : simpleFactories) {
            l.addAll(f.getSourceDirectoriesForLocale(localeName));
        }
        return l;
    }

}
