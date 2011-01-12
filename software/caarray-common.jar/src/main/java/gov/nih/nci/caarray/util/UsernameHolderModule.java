package gov.nih.nci.caarray.util;

import com.google.inject.AbstractModule;

/**
 * @author jscott
 *
 */
public class UsernameHolderModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(UsernameHolder.class).to(UsernameHolderImpl.class).asEagerSingleton();            
    }

}