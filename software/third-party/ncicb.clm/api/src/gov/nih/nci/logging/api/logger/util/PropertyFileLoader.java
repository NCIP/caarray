package gov.nih.nci.logging.api.logger.util;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Ekagra Software Technologes Limited ('Ekagra')
 * 
 * This class actually loads the propety file in the classpath
 * 
 */
public class PropertyFileLoader
{

	private static PropertyFileLoader myInstance = null;

	private static boolean THROW_ON_LOAD_FAILURE = true;

	private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;

	private static final String SUFFIX = ".xml";

	PropertyFileLoader()
	{
	} 

	/**
	 * Looks up a resource named 'name' in the classpath. The resource must map
	 * to a file with .properties extention. The name is assumed to be absolute
	 * and can use either "/" or "." for package segment separation with an
	 * optional leading "/" and optional ".properties" suffix. Thus, the
	 * following names refer to the same resource:
	 * 
	 * <pre>
	 *         some.pkg.Resource
	 *         some.pkg.Resource.properties
	 *         some/pkg/Resource
	 *         some/pkg/Resource.properties
	 *         /some/pkg/Resource
	 *         /some/pkg/Resource.properties
	 * </pre>
	 * 
	 * @param name
	 *            classpath resource name [may not be null]
	 * @param loader
	 *            classloader through which to load the resource [null is
	 *            equivalent to the application loader]
	 * 
	 * @return resource converted to java.util.Properties [may be null if the
	 *         resource was not found and THROW_ON_LOAD_FAILURE is false]
	 * @throws IllegalArgumentException
	 *             if the resource was not found and THROW_ON_LOAD_FAILURE is
	 *             true
	 */
	public InputStream loadPropertyFile(String name, ClassLoader loader) throws IllegalArgumentException
	{
		if (name == null)
			throw new IllegalArgumentException("null input: name");

		if (name.startsWith("/"))
			name = name.substring(1);

		if (name.endsWith(SUFFIX))
			name = name.substring(0, name.length() - SUFFIX.length());
		
		InputStream in = null;
		try
		{
			if (loader == null)
				loader = ClassLoader.getSystemClassLoader();

			if (LOAD_AS_RESOURCE_BUNDLE)
			{
				name = name.replace('/', '.');

				// throws MissingResourceException on lookup failures:
				final ResourceBundle rb = ResourceBundle.getBundle(name, Locale.getDefault(), loader);			
			}
			else
			{
				name = name.replace('.', '/');

				if (!name.endsWith(SUFFIX))
					name = name.concat(SUFFIX);

				// returns null on lookup failures:
				in = loader.getResourceAsStream(name);
				if (in != null)
				{

					// result = new Properties();
					// result.load(in); // can throw IOException
					THROW_ON_LOAD_FAILURE = false;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}

		if (THROW_ON_LOAD_FAILURE)
		{
			throw new IllegalArgumentException("could not load [" + name + "]" + " as " + (LOAD_AS_RESOURCE_BUNDLE ? "a resource bundle" : "a classloader resource"));
		}

		return in;
	}

	/**
	 * A convenience overload of {@link #loadPropertyFile(String, ClassLoader)}
	 * that uses the current thread's context classloader.
	 */
	public InputStream loadPropertyFile(final String name)
	{
		return loadPropertyFile(name, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * @return --returns the single instance of the class
	 */
	public static PropertyFileLoader getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new PropertyFileLoader();
		}
		return myInstance;

	}
}