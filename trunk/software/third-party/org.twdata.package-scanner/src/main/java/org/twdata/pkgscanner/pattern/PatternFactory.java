package org.twdata.pkgscanner.pattern;

/**
 * Compiles wildcard patterns
 */
public interface PatternFactory {

    /**
     * Compiles a wildcard pattern
     * @param pattern The original pattern
     * @return The compiled pattern
     */
    CompiledPattern compile(String pattern);
}
