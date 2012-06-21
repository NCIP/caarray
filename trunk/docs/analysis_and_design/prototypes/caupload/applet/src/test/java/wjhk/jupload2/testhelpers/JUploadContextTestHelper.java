package wjhk.jupload2.testhelpers;

import java.awt.Cursor;
import java.awt.Frame;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JApplet;

import org.apache.log4j.Logger;

import wjhk.jupload2.context.JUploadContext;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.JUploadTextArea;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * This class is a used as a context, out of real JUpload application. It is
 * used to build contexts for JUnit tests in the JUpload package.
 * 
 * @author etienne_sf
 */
public class JUploadContextTestHelper implements JUploadContext {

	/** We mask real mime types here. */
	public static String TEST_CASE_MIME_TYPE = "X-jupload/noFileTypeIn_JUploadContextTestCase";

	/** Logger for this class */
	protected final Logger logger = Logger.getLogger(getClass());

	/** */
	public UploadPolicy uploadPolicy = null;

	/** */
	public JUploadPanel juploadPanel = null;

	/** */
	public JUploadTextArea logWindow = null;

	/**
	 * Root folder for policies configuration files, that are used in JUnit
	 * tests. The current user dir is the root of the eclipse project.
	 */
	public final static String TEST_ROOT_FOLDER = "tests/";

	/**
	 * Root folder for policies configuration files, that are used in JUnit
	 * tests.
	 */
	public final static String TEST_PROPERTIES_FOLDER = TEST_ROOT_FOLDER
			+ "policies/";

	/**
	 * Root folder for policies configuration files, that are used in JUnit
	 * tests.
	 */
	public final static String TEST_FILES_FOLDER = TEST_ROOT_FOLDER + "files/";

	/**
	 * The policy default configuration file. It contains default values that
	 * are shared by all policy configuration files.
	 */
	public final static String TEST_DEFAULT_PROPERTIES_FILE = TEST_PROPERTIES_FOLDER
			+ "default_uploadPolicy.properties";

	/**
	 * Used to control the last call to {@link #registerUnload(Object, String)}
	 */
	public Object lastRegisterUnloadObject = null;

	/**
	 * Used to control the last call to {@link #registerUnload(Object, String)}
	 */
	public String lastRegisterUnloadMethod = null;

	/**
	 * @param uploadPolicy
	 * @param juploadPanel
	 */
	public JUploadContextTestHelper(UploadPolicy uploadPolicy,
			JUploadPanel juploadPanel) {
		this.uploadPolicy = uploadPolicy;
		this.juploadPanel = juploadPanel;
		this.logWindow = new JUploadTextArea(100, 100, uploadPolicy);
	}

	/**
	 * If using this constructor, you should set afteward: uploadPolicy,
	 * logWindow.
	 * 
	 * @param juploadPanel
	 */
	public JUploadContextTestHelper(JUploadPanel juploadPanel) {
		this.juploadPanel = juploadPanel;
		this.logWindow = new JUploadTextArea(100, 100, uploadPolicy);
	}

	/**
	 * Creates and loads a property file, and return the loaded result.
	 * 
	 * @param filename
	 *            The name of the file, which contains the properties to load
	 * @param defaultProperties
	 *            The default properties value. Put null if no default
	 *            Properties should be used.
	 * @return The loaded properties. It's empty if an error occurs.
	 */
	Properties loadPropertiesFromTestFile(String filename,
			Properties defaultProperties) {
		Properties properties = new Properties(defaultProperties);
		try {
			InputStream isProperties = new FileInputStream(filename);
			properties.load(isProperties);
			isProperties.close();
		} catch (IOException e1) {
			System.out.println("Error while loading " + filename + " ("
					+ e1.getClass().getName() + ")");
			e1.printStackTrace();
		}

		return properties;
	}

	boolean getBooleanProperty(String property, boolean def) {
		if (property.startsWith("xxx")) {
			// Hum, if we go here, there are some unchanged "xxx" in the code.
			// To be changed.
			throw new UnsupportedOperationException(this.getClass()
					+ ".getBooleanProperty() unknown property: '" + property
					+ "'");
		} else {
			return parseBoolean(property, def);
		}
	}

	/*********************************************************************************
	 * Methods of the JUploadContext interface
	 *********************************************************************************/
	/**
	 * @param url
	 * @param success
	 */
	public void displayURL(String url, boolean success) {
		throw new UnsupportedOperationException(this.getClass()
				+ ".displayURL() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getApplet()
	 */
	public JApplet getApplet() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getApplet() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getBuildDate()
	 */
	public String getBuildDate() {
		return "no build date for TestCase";
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getBuildNumber()
	 */
	public int getBuildNumber() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getBuildNumber() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getCursor()
	 */
	public Cursor getCursor() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getCursor() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getLastModified()
	 */
	public String getLastModified() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getLastModified() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getLogWindow()
	 */
	public JUploadTextArea getLogWindow() {
		return this.logWindow;
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(final String fileExtension) {
		return TEST_CASE_MIME_TYPE;
	}

	/**
	 * Get a String parameter value from applet properties or System properties.
	 * 
	 * @param key
	 *            The name of the parameter to fetch.
	 * @param def
	 *            A default value which is used, when the specified parameter is
	 *            not set.
	 * @return The value of the applet parameter (resp. system property). If the
	 *         parameter was not specified or no such system property exists,
	 *         returns the given default value.
	 */

	public String getParameter(String key, String def) {
		String paramStr = (System.getProperty(key) != null ? System
				.getProperty(key) : def);
		return paramStr;
	}

	/**
	 * @param key
	 * @param def
	 * @return The value for this parameter
	 */
	public int getParameter(String key, int def) {
		String paramDef = Integer.toString(def);
		String paramStr = System.getProperty(key) != null ? System
				.getProperty(key) : paramDef;
		return parseInt(paramStr, def);
	}

	/**
	 * @param key
	 * @param def
	 * @return The value for this parameter
	 */
	public float getParameter(String key, float def) {
		String paramDef = Float.toString(def);
		String paramStr = System.getProperty(key) != null ? System
				.getProperty(key) : paramDef;
		return parseFloat(paramStr, def);
	}

	/**
	 * @param key
	 * @param def
	 * @return The value for this parameter
	 */
	public long getParameter(String key, long def) {
		String paramDef = Long.toString(def);
		String paramStr = System.getProperty(key) != null ? System
				.getProperty(key) : paramDef;
		return parseLong(paramStr, def);
	}

	/**
	 * @param key
	 * @param def
	 * @return The value for this parameter
	 */

	public boolean getParameter(String key, boolean def) {
		String paramDef = (def ? "true" : "false");
		String paramStr = System.getProperty(key) != null ? System
				.getProperty(key) : paramDef;
		return parseBoolean(paramStr, def);
	}// getParameter(boolean)

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getUploadPanel()
	 */
	public JUploadPanel getUploadPanel() {
		return this.juploadPanel;
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getUploadPolicy()
	 */
	public UploadPolicy getUploadPolicy() {
		return this.uploadPolicy;
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getDetailedVersionMessage()
	 */
	public String getDetailedVersionMessage() {
		return "TestCase (detailed message)";
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getVersion()
	 */
	public String getVersion() {
		return "TestCase version";
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#getSvnRevision()
	 */
	public String getSvnRevision() {
		return "TestCase svn revision";
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#normalizeURL(java.lang.String)
	 */
	public String normalizeURL(String url) throws JUploadException {
		return url;
	}

	/** {@inheritDoc} */
	public int parseInt(String value, int def) {
		int ret = def;
		// Then, parse it as an integer.
		try {
			ret = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			ret = def;
			this.logger.warn("Invalid int value: " + value
					+ ", using default value: " + def);

		}
		return ret;
	}

	/** {@inheritDoc} */
	public float parseFloat(String value, float def) {
		float ret = def;
		// Then, parse it as an integer.
		try {
			ret = Float.parseFloat(value);
		} catch (NumberFormatException e) {
			ret = def;
			this.logger.warn("Invalid float value: " + value
					+ ", using default value: " + def);
		}
		return ret;
	}

	/** {@inheritDoc} */
	public long parseLong(String value, long def) {
		long ret = def;
		// Then, parse it as an integer.
		try {
			ret = Long.parseLong(value);
		} catch (NumberFormatException e) {
			ret = def;
			this.logger.warn("Invalid long value: " + value
					+ ", using default value: " + def);
		}
		return ret;
	}

	/** {@inheritDoc} */
	public boolean parseBoolean(String value, boolean def) {
		// Then, parse it as a boolean.
		if (value.toUpperCase().equals("FALSE")) {
			return false;
		} else if (value.toUpperCase().equals("TRUE")) {
			return true;
		} else {
			this.logger.warn("Invalid boolean value: " + value
					+ ", using default value: " + def);
		}
		return def;
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#readCookieFromNavigator(java.util.Vector)
	 */
	public void readCookieFromNavigator(Vector<String> headers) {
		this.logger
				.warn(this.getClass()
						+ ".readCookieFromNavigator() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#readUserAgentFromNavigator(java.util.Vector)
	 */
	public void readUserAgentFromNavigator(Vector<String> headers) {
		this.logger
				.warn(this.getClass()
						+ ".readUserAgentFromNavigator() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#registerUnload(java.lang.Object,
	 *      java.lang.String)
	 */
	public void registerUnload(Object object, String method) {
		this.lastRegisterUnloadObject = object;
		this.lastRegisterUnloadMethod = method;
		this.logger.warn(this.getClass()
				+ ".registerUnload() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#runUnload()
	 */
	public void runUnload() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".runUnload() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#setCursor(java.awt.Cursor)
	 */
	public Cursor setCursor(Cursor cursor) {
		throw new UnsupportedOperationException(this.getClass()
				+ ".setCursor() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty(String prop, String value) {
		throw new UnsupportedOperationException(this.getClass()
				+ ".setProperty() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#setWaitCursor()
	 */
	public Cursor setWaitCursor() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".setWaitCursor() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#showStatus(java.lang.String)
	 */
	public void showStatus(String status) {
		this.logger.warn(this.getClass()
				+ ".showStatus() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.context.JUploadContext#startUpload()
	 */
	public String startUpload() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".startUpload() is not implemented in tests cases");
	}

	/** @see JUploadContext#getFrame() */
	public Frame getFrame() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getFrame() is not implemented in tests cases");
	}

	
    @Override
    public void selectFiles() {
    }

    @Override
    public String getSelectedFilesInfoJSON() {
        return "";
    }

    @Override
    public String getProgressInfoJSON() {
        return "";
    }

    @Override
    public boolean isUploadFinished() {
        return false;
    }
}
