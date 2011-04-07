package gov.nih.nci.caarray.web.plugins;

import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Finds Velocity templates in bundles
 * 
 * Adapted from the Struts2 OSGi integration module.
 * 
 * @author dkokotov
 */
public class VelocityBundleResourceLoader extends ClasspathResourceLoader {
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if ((name == null) || (name.length() == 0)) {
            throw new ResourceNotFoundException("No template name provided");
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        try {
            return DefaultBundleAccessor.getInstance().loadResourceAsStream(name);
        } catch (final Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
