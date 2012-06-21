package org.twdata.pkgscanner.pattern;

/**
 * Compiles simple wildcard patterns
 */
public class SimpleWildcardPatternFactory implements PatternFactory {
    public CompiledPattern compile(String pattern) {
        return new SimpleWildcardPattern(pattern);
    }
}
