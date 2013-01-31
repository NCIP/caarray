//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Utility classes for our project.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class CaArrayUtils {
    private static final SortedSet<Object> EMPTY_SORTED_SET = new TreeSet<Object>();

    private CaArrayUtils() {
        // prevent instantiation;
    }

    /**
     * Method to take a get a unique result from a set and return it or null.
     *
     * @param <T> the type of the returned object
     * @param results the set of results returned from a query
     * @return the first result in the set or null
     */
    public static <T> T uniqueResult(Collection<T> results) {
        return results.isEmpty() ? null : results.iterator().next();
    }

    /**
     * Returns an empty collection or map of the appropriate type for a given collection class. By default,
     * returns an empty list, but will return an empty set, empty sorted set, or empty map if the passed
     * in type is a subclass of Set, SortedSet, or Map respectively.
     * @param collectionType the class of whose type to return an empty collection or map
     * @return the empty collection or map
     */
    public static Object emptyCollectionOrMapFor(Class<?> collectionType) {
        Object val = Collections.EMPTY_LIST;
        if (SortedSet.class.isAssignableFrom(collectionType)) {
            val = EMPTY_SORTED_SET;
        } else if (Set.class.isAssignableFrom(collectionType)) {
            val = Collections.EMPTY_SET;
        } else if (List.class.isAssignableFrom(collectionType)) {
            val = Collections.EMPTY_LIST;
        } else if (Map.class.isAssignableFrom(collectionType)) {
            val = Collections.EMPTY_MAP;
        }
        return val;
    }

    /**
     * Removes matched quotes (single or double) from a string.  Quotes are only removed from the first and last
     * characters of the string.
     * @param string string to dequote
     * @return the dequoted string or the original string, if no changes were made
     */
    public static String dequoteString(String string) {
        if (string != null && string.length() > 1
                && ((string.charAt(0) == '"' || string.charAt(0) == '\'')
                        && string.charAt(string.length() - 1) == string.charAt(0))) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }
}
