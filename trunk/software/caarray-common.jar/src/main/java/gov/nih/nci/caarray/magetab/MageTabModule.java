package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.splitter.MageTabFileSetSplitter;
import gov.nih.nci.caarray.magetab.splitter.MageTabFileSetSplitterImpl;
import gov.nih.nci.caarray.magetab.splitter.SdrfDataFileFinder;
import gov.nih.nci.caarray.magetab.splitter.SdrfDataFileFinderImpl;
import gov.nih.nci.caarray.magetab.splitter.SdrfSplitter;
import gov.nih.nci.caarray.magetab.splitter.SdrfSplitterImpl;

import com.google.inject.AbstractModule;

/**
 * Guice module for the magetab packages.
 * 
 * @author tparnell
 */
public class MageTabModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(MageTabFileSetSplitter.class).to(MageTabFileSetSplitterImpl.class);
        bind(SdrfSplitter.class).to(SdrfSplitterImpl.class);
        bind(SdrfDataFileFinder.class).to(SdrfDataFileFinderImpl.class);
    }
}
