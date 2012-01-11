package gov.nih.nci.caarray.application.util;

import gov.nih.nci.caarray.domain.file.CaArrayFileSet;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;

/**
 * Guice module for the util package.

 * @author tparnell
 */
public class UtilModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // TODO: ARRAY-2189 - wiring with fake version for now.  Uncomment to use real version.
//        bind(CaArrayFileSetSplitter.class).to(CaArrayFileSetSplitterImpl.class);
        bind(CaArrayFileSetSplitter.class).toInstance(new CaArrayFileSetSplitter() {
            @Override
            public Set<CaArrayFileSet> split(CaArrayFileSet largeFileSet)
                    throws IOException {
                return ImmutableSet.of(largeFileSet);
            }
        });
    }

}
