//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import java.util.Locale;

/**
 * Enum of constants for different ways of matching an example entity.
 * 
 * @author dkokotov
 */
public enum MatchMode {
    /**
     * Match the entire string to the pattern.
     */
    EXACT {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(String candidate, String target) {
            return candidate.equalsIgnoreCase(target);
        }
    },

    /**
     * Match the start of the string to the pattern.
     */
    START {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(String candidate, String target) {
            return target.toUpperCase(Locale.getDefault()).startsWith(candidate.toUpperCase(Locale.getDefault()));
        }
    },

    /**
     * Match the end of the string to the pattern.
     */
    END {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(String candidate, String target) {
            return target.toUpperCase(Locale.getDefault()).endsWith(candidate.toUpperCase(Locale.getDefault()));
        }
    },

    /**
     * Match the pattern anywhere in the string.
     */
    ANYWHERE {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(String candidate, String target) {
            return target.toUpperCase(Locale.getDefault()).contains(candidate.toUpperCase(Locale.getDefault()));
        }
    };

    private static final long serialVersionUID = 1L;
    
    /**
     * @return the name
     */
    public String getName() {
        return name();
    }
    
    /**
     * Checks whether the candidate string matches the target string, using the comparison mode for this match mode.
     * @param candidate the pattern. Must not be null.
     * @param target the string to match the pattern against. Must not be null.
     * @return whether it matches.
     */
    public abstract boolean matches(String candidate, String target);
}
