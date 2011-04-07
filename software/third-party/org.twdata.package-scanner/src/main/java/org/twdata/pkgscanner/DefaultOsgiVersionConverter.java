/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.twdata.pkgscanner;

import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

/**
 * Converts a version number into an OSGi-compatible one.  Borrowed from the Apache Maven project,
 * then rewritten to work faster without regular expressions.
 */
public class DefaultOsgiVersionConverter implements OsgiVersionConverter
{
    /** Bundle-Version must match this pattern */
    private static final Pattern OSGI_VERSION_PATTERN = Pattern
        .compile( "[0-9]+\\.[0-9]+\\.[0-9]+(\\.[0-9A-Za-z_-]+)?" );

    public String getVersion(String version)
    {
        /* if it's already OSGi compliant don't touch it */
        if (OSGI_VERSION_PATTERN.matcher(version).matches())
        {
            return version;
        }

        String[] osgiComponents = new String[4];
        int c = 0, t = 0;

        // split the original version into tokens
        String[] tokens = splitOnDelimiters(version);

        // if the first three tokens are simple numbers, add them as the first three components 
        for (; t<tokens.length && c<4; t++) {
            if (!isNumericComponent(tokens[t]))
                break;
            osgiComponents[c] = tokens[t];
            c++;
        }
        // join any remaining tokens with underscores and put them in the last component
        for (; t<tokens.length; t++) {
            if (osgiComponents[3] == null) osgiComponents[3] = "";
            osgiComponents[3] += tokens[t];
            if (t < tokens.length - 1) osgiComponents[3] += "_";
        }

        return getVersion(osgiComponents[0], osgiComponents[1], osgiComponents[2], osgiComponents[3]);
    }

    /**
     * Faster than {@link String#split(String)} because it only splits on single characters.
     */
    private String[] splitOnDelimiters(String version) {
        List<String> result = new ArrayList<String>(10);
        int lastDelimiter = -1;
        for (int c=0; c < version.length(); c++) {
            if (isDelimiter(version.charAt(c))) {
                result.add(version.substring(lastDelimiter + 1, c));
                lastDelimiter = c;
            }
        }
        result.add(version.substring(lastDelimiter + 1));
        return result.toArray(new String[result.size()]);
    }

    /**
     * Returns true if the character is not a letter or digit.
     */
    private boolean isDelimiter(char c) {
        final boolean notADelimiter = (c >= '0' && c <= '9') ||
            (c >= 'A' && c <= 'Z') ||
            (c >= 'a' && c <= 'z');
        return !notADelimiter;
    }

    /**
     * Returns true if the string is a number not longer than four digits.
     */
    private boolean isNumericComponent(String s) {
        if (s.length() > 4)
            return false;
        for (int c = 0; c < s.length(); c++)
        {
            if (s.charAt(c) < '0' || s.charAt(c) > '9')
                return false;
        }
        return true;
    }

    /**
     * Joins together the components of a four-part OSGi version. Null components will be
     * replaced with "0".
     */
    private String getVersion( String major, String minor, String service, String qualifier )
    {
        StringBuffer sb = new StringBuffer();
        sb.append( isBlank(major) ? "0" : major );
        sb.append( '.' );
        sb.append( isBlank(minor) ? "0" : minor );
        sb.append( '.' );
        sb.append( isBlank(service) ? "0" : service);
        if (!isBlank(qualifier))
        {
            sb.append( '.' );
            sb.append( qualifier );
        }
        return sb.toString();
    }

    private static boolean isBlank(String str)
    {
        return str == null || str.length() == 0;
    }
}
