package org.unicode.cldr.test;

import java.util.List;

import org.unicode.cldr.test.CheckCLDR.CheckStatus;
import org.unicode.cldr.test.CheckCLDR.Options;

/**
 * This interface exposes only the core handleCheck API.
 */
public interface CheckCLDRHandler {

    /**
     * This is what the subclasses override.
     * If they ever use pathParts or fullPathParts, they need to call initialize() with the respective
     * path. Otherwise they must NOT change pathParts or fullPathParts.
     * <p>
     * If something is found, a CheckStatus is added to result. This can be done multiple times in one call, if multiple
     * errors or warnings are found. The CheckStatus may return warnings, errors, examples, or demos. We may expand that
     * in the future.
     * <p>
     * The code to add the CheckStatus will look something like::
     *
     * <pre>
     * result.add(new CheckStatus()
     *     .setType(CheckStatus.errorType)
     *     .setMessage(&quot;Value should be {0}&quot;, new Object[] { pattern }));
     * </pre>
     *
     * @param options
     *            TODO
     */
    CheckCLDR handleCheck(String path, String fullPath, String value,
        Options options, List<CheckStatus> result);

}