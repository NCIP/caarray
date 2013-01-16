//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package org.twdata.pkgscanner.pattern;

import java.util.regex.Pattern;

/**
 * A simple wildcard pattern that uses the '*' character to match everything.
 */
public class SimpleWildcardPattern implements CompiledPattern {
    private Pattern pattern;
    private String original;

    public SimpleWildcardPattern(String pattern) {

        this.original = pattern;

        String ptn = pattern;
        ptn = ptn.replace(".", "\\.");
        ptn = ptn.replace("*", ".*");
        this.pattern = Pattern.compile(ptn);
    }


    public String getOriginal() {
        return original;
    }

    public boolean matches(String value) {
        return pattern.matcher(value).matches();
    }
}
