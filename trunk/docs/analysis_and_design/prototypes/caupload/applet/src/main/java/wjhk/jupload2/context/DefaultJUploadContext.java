// $Id: JUploadApplet.java 750 2009-05-06 14:36:50Z etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2009-05-06 16:36:50 +0200 (mer., 06 mai 2009) $
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version. This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details. You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software Foundation, Inc.,
// 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.context;

import java.awt.Cursor;
import java.awt.Frame;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.JUploadPanelImpl;
import wjhk.jupload2.gui.JUploadTextArea;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.policies.UploadPolicyFactory;
import wjhk.jupload2.upload.FileUploadManagerThread;

/**
 * The Jupload Context. One such context is created at run time. It can be the
 * Applet, or the 'main' class, depending on the launch type. <BR>
 * It contains the call to the creation of the
 * {@link wjhk.jupload2.gui.JUploadPanel}, which contains the real code, and
 * some technical stuff that depend on the technical context (mainly applet or
 * stand alone application). <BR>
 * The functional control of JUpload is done by using {@link UploadPolicy}. This
 * class should not be changed, in order to remain compatible with next JUpload
 * releases. <BR>
 * <BR>
 * <B>Technical note:</B> This class should be abstract. But it is used by the
 * build.xml file, to load the version. So all methods of the
 * {@link JUploadContext} interface are implemented. Those who actually can't be
 * coded here, just generate a UnsupportedOperationException exception.
 * 
 * @author etienne_sf
 * @version $Revision: 750 $
 */
public class DefaultJUploadContext implements JUploadContext {

	/**
	 * The final that contains the SVN properties. These properties are
	 * generated during compilation, by the build.xml ant file.
	 */
	private final static String SVN_PROPERTIES_FILENAME = "/conf/svn.properties";

	/**
	 * Used as default value when calling getProperty, to identify missing
	 * properties, and have a correct behavior in this case.
	 */
	private final static String DEFAULT_PROP_UNKNOWN = "Unknown";

	/**
	 * The properties, created at build time, by the build.xml ant file. Or a
	 * dummy property set, with 'unknown' values.
	 */
	Properties svnProperties = getSvnProperties();

	/**
	 * The frame that contains the application. Mainly used to attached modal
	 * dialog.
	 */
	Frame frame = null;

	/**
	 * variable to hold reference to JavascriptHandler object
	 */
	JavascriptHandler jsHandler = null;

	/**
	 * the mime type list, coming from: http://www.mimetype.org/ Thanks to them!
	 */
	Properties mimeTypesProperties = null;

	/**
	 * The current upload policy. This class is responsible for the call to the
	 * UploadPolicyFactory.
	 */
	UploadPolicy uploadPolicy = null;

	/**
	 * The JUploadPanel, which actually contains all the applet components.
	 */
	JUploadPanel jUploadPanel = null;

	/**
	 * The log messages should go there ...
	 */
	JUploadTextArea logWindow = null;

	/**
	 * This class represent the Callback method. It is then possible to run the
	 * {@link JUploadContext#registerUnload(Object, String)} method to register
	 * new callback methods. These callback methods are executed when the applet
	 * or the application closes, by calling the
	 * {@link JUploadContext#runUnload()} method.
	 */
	static class Callback {
		private String method;

		private Object object;

		Callback(Object object, String method) {
			this.object = object;
			this.method = method;
		}

		void invoke() throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException, SecurityException {
			Object args[] = {};
			Method methods[] = this.object.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(this.method)) {
					methods[i].invoke(this.object, args);
				}
			}
		}

		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}

		/**
		 * @return the object
		 */
		public Object getObject() {
			return object;
		}

	}

	/**
	 * All registered callbacks.
	 * 
	 * @see Callback
	 */
	List<Callback> unloadCallbacks = new ArrayList<Callback>(20);

	/**
	 * Reaction on the start of the applet: creation of each specific item of
	 * the GUI, and the upload policy. <BR>
	 * This method needs that the initialization of the called is finished. For
	 * instance, {@link JUploadContextApplet} needs to have set theApplet, to be
	 * able to properly execute some method calls that are in the init() method.
	 * So we can not do this initialization in the constructor of
	 * DefaultJUploadContext.
	 * 
	 * @param frame
	 *            The frame that contains the application. Mainly used to
	 *            attached modal dialog.
	 * @param rootPaneContainer
	 *            The mother window (JApplet, JFrame...), which contains the
	 *            rootPaneContainer. Used to set the {@link JUploadPanel} in it.
	 */
	public void init(Frame frame, RootPaneContainer rootPaneContainer) {
		try {
			this.frame = frame;

			// The standard thread name is: thread
			// applet-wjhk.jupload2.JUploadApplet.class
			// Too long ! :-)
			Thread.currentThread().setName(
					rootPaneContainer.getClass().getName());

			// The logWindow must exist before the uploadPolicy creation. But it
			// needs the uploadPolicy, to know the logging parameters. We'll set
			// the uploadPolicy just after.
			this.logWindow = new JUploadTextArea(20, 20, this.uploadPolicy);

			// Now we can create the upload policy: the logWindow exists.
			this.uploadPolicy = UploadPolicyFactory.getUploadPolicy(this);
			this.uploadPolicy.displayDebug(
					"After UploadPolicyFactory.getUploadPolicy(this)", 80);

			// We set the uploadPolicy to the logWindow. The logThread starts,
			// and it register its unload method, to be called when the JUpload
			// finishes.
			this.uploadPolicy.displayDebug(
					"Before this.logWindow.setUploadPolicy(this.uploadPolicy)",
					80);
			this.logWindow.setUploadPolicy(this.uploadPolicy);

			// getMainPanel().setLayout(new BorderLayout());
			this.uploadPolicy
					.displayDebug(
							"Before new JUploadPanelImpl(this.logWindow,this.uploadPolicy)",
							80);
			this.jUploadPanel = new JUploadPanelImpl(this.logWindow,
					this.uploadPolicy);

			// getMainPanel().add(this.jUploadPanel, BorderLayout.CENTER);
			this.uploadPolicy
					.displayDebug(
							"Before rootPaneContainer.setContentPane(this.jUploadPanel);",
							80);
			rootPaneContainer.setContentPane(this.jUploadPanel.getJComponent());

			// We start the jsHandler thread, that allows javascript to send
			// upload command to the applet.
			this.uploadPolicy
					.displayDebug(
							"Before new JavascriptHandler(this.uploadPolicy, this.jUploadPanel)",
							80);
			this.jsHandler = new JavascriptHandler(this.uploadPolicy,
					this.jUploadPanel);
			this.jsHandler.start();
			// Then we register the unload method, that'll be called like all
			// unload callbacks when the applet is stopped.
			registerUnload(this, "unload");
		} catch (final Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			// TODO Translate this sentence
			JOptionPane.showMessageDialog(null,
					"Error during applet initialization!\nHave a look in your Java console ("
							+ e.getClass().getName() + ")", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		this.uploadPolicy.displayDebug("Before new Properties();", 80);
		this.mimeTypesProperties = new Properties();
		final String mimetypePropertiesFilename = "/conf/mimetypes.properties";
		try {
			InputStream isProperties = this.getClass().getResourceAsStream(
					mimetypePropertiesFilename);
			this.mimeTypesProperties.load(isProperties);
			isProperties.close();
			this.uploadPolicy.displayDebug("Mime types list loaded Ok ("
					+ mimetypePropertiesFilename + ")", 50);
		} catch (Exception e) {
			this.uploadPolicy
					.displayWarn("Unable to load the mime types list ("
							+ mimetypePropertiesFilename + "): "
							+ e.getClass().getName() + " (" + e.getMessage()
							+ ")");
		}

		this.uploadPolicy.displayDebug("End of DefaultJUploadContext.init()",
				80);
	}

	/**
	 * This method is called when the applet is unloaded (actually, when it is
	 * stopped). it is registered as a callback in the
	 * {@link #init(Frame, RootPaneContainer)}, here above.
	 */
	public void unload() {
		if (this.jsHandler != null && this.jsHandler.isAlive()) {
			this.jsHandler.interrupt();
			this.jsHandler = null;
		}
	}

	/** {@inheritDoc} */
	public String getDetailedVersionMessage() {
		String version = getVersion();
		String svnRevision = getSvnRevision();
		boolean gotSvnRevision = !svnRevision.equals(DEFAULT_PROP_UNKNOWN);
		int buildNumber = getBuildNumber();
		boolean gotBuildNumber = buildNumber > 0;
		String buildDate = getBuildDate();
		boolean gotBuildDate = !buildDate.equals(DEFAULT_PROP_UNKNOWN);

		StringBuffer sb = new StringBuffer();
		sb.append(version);

		if (gotSvnRevision || gotBuildNumber) {
			sb.append(" [");
			String space = "";
			if (gotSvnRevision) {
				sb.append("SVN-Rev: ");
				sb.append(svnRevision);
				space = " ";
			}
			if (gotBuildNumber) {
				sb.append(space);
				sb.append("build ");
				sb.append(buildNumber);
			}
			sb.append("]");
		}

		if (gotBuildDate) {
			sb.append(" - ");
			sb.append(buildDate);
		}
		return sb.toString();
	}

	/** {@inheritDoc} */
	public String getVersion() {
		return getProperty("jupload.version", DEFAULT_PROP_UNKNOWN);
	}

	/** {@inheritDoc} */
	public String getSvnRevision() {
		String svnRevision = getProperty("jupload.svn.revision",
				DEFAULT_PROP_UNKNOWN);
		if (svnRevision.startsWith("{") || svnRevision.startsWith("${")) {
			// This particular case should not happen with standard maven build.
			// But it occurs when launching after an eclipse automatic build.
			// Returning DEFAULT_PROP_UNKNOWN in this case, make the applet
			// behave like if it was built by maven.
			return DEFAULT_PROP_UNKNOWN;
		} else {
			return svnRevision;
		}
	}

	/** {@inheritDoc} */
	public String getLastModified() {
		return getProperty("jupload.lastSrcDirModificationDate",
				DEFAULT_PROP_UNKNOWN);
	}

	/** {@inheritDoc} */
	public String getBuildDate() {
		String timestamp = getProperty("jupload.buildTimestamp",
				DEFAULT_PROP_UNKNOWN);
		if (timestamp.equals(DEFAULT_PROP_UNKNOWN)) {
			return DEFAULT_PROP_UNKNOWN;
		} else {
			Locale locale = Locale.getDefault();
			if (this.uploadPolicy != null) {
				locale = this.uploadPolicy.getLocale();
			}
			MessageFormat msgFormat = new MessageFormat("{0,date,medium}",
					locale);
			try {
				Object[] args = { new Date(Long.parseLong(timestamp)) };
				return msgFormat.format(args);
			} catch (NumberFormatException e) {
				// uploadPolicy is null at startup.
				// TODO Better handling logging, here
				System.out.println("[WARN] The timestamp can not be read ("
						+ timestamp + "). Will return '" + DEFAULT_PROP_UNKNOWN
						+ "'.");
				return DEFAULT_PROP_UNKNOWN;
			}
		}
	}

	/** {@inheritDoc} */
	public int getBuildNumber() {
		String valuePropBuildNumber = getProperty("jupload.buildNumber", "-1");
		try {
			return Integer.parseInt(valuePropBuildNumber);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("[WARN] " + e.getClass().getName()
					+ " when getting the buildNumber, while parsing '"
					+ valuePropBuildNumber + "'). Will return -1");
			return -1;
		}
	}

	/**
	 * Try to get a property from the loaded properties. If the property is not
	 * available, the default value is returned.
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	private String getProperty(String propertyName, String defaultValue) {
		String value = null;
		try {
			value = this.svnProperties.getProperty(propertyName);
		} catch (Exception e) {
			System.out.println("[WARN] " + e.getClass().getName()
					+ " when getting the " + propertyName + " property ("
					+ e.getMessage() + "). Will return '" + value + "'");
		}
		return (value == null) ? defaultValue : value;
	}

	/** {@inheritDoc} */
	public JUploadTextArea getLogWindow() {
		return this.logWindow;
	}

	/** {@inheritDoc} */
	public String getMimeType(String fileExtension) {
		String mimeType = this.mimeTypesProperties.getProperty(fileExtension
				.toLowerCase());
		return (mimeType == null) ? "application/octet-stream" : mimeType;
	}

	/** {@inheritDoc} */
	public JUploadPanel getUploadPanel() {
		return this.jUploadPanel;
	}

	/**
	 * Retrieves the current upload policy. The JUploadContext is responsible
	 * for storing the UploadPolicy associated with the current instance.
	 * 
	 * @return the current upload policy of this instance.
	 * @throws JUploadException
	 */
	public UploadPolicy getUploadPolicy() throws JUploadException {
		return this.uploadPolicy;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////:
	// //////////////// FUNCTIONS INTENDED TO BE CALLED BY JAVASCRIPT FUNCTIONS
	// ////////////////////////////:
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////:

	/**
	 * This allow runtime modifications of properties, from javascript.
	 * Currently, this can only be used after full initialization. This method
	 * only calls the UploadPolicy.setProperty method. <BR>
	 * Ex: document.jupload.setProperty(prop, value);
	 * 
	 * @param prop
	 *            The property name that must be set.
	 * @param value
	 *            The value of this property.
	 */
	public void setProperty(String prop, String value) {
		// FIXME setProperty should use jsHandler
		class PropertySetter implements Runnable {
			String prop;

			String value;

			PropertySetter(String prop, String value) {
				this.prop = prop;
				this.value = value;
			}

			public void run() {
				try {
					// We'll wait up to 2s until the applet initialized (we need
					// an
					// upload policy).
					// FIXME should be done in a separate thread (block the
					// browser)
					for (int i = 0; i < 20 && uploadPolicy == null; i += 1) {
						this.wait(100);
					}
					if (uploadPolicy == null) {
						System.out
								.println("uploadPolicy is null. Impossible to set "
										+ prop + " to " + value);
					} else {
						// FIXME There should be a boolean: initialized, to
						// indicate when property may be set.
						uploadPolicy.setProperty(prop, value);
					}
				} catch (Exception e) {
					uploadPolicy.displayErr(e);
				}
			}
		}
		try {
			SwingUtilities.invokeLater(new PropertySetter(prop, value));
		} catch (Exception e) {
			if (this.uploadPolicy != null) {
				this.uploadPolicy.displayErr(e);
			} else {
				System.out.println(e.getClass().getName() + ": "
						+ e.getMessage());
			}
		}
	}

	/** {@inheritDoc} */
	public String startUpload() {
		return this.jsHandler.doCommand(JavascriptHandler.COMMAND_START_UPLOAD);
	}

	/**
	 * Call to {@link UploadPolicy#displayErr(Exception)}
	 * 
	 * @param err
	 *            The error text to be displayed.
	 */
	public void displayErr(String err) {
		this.uploadPolicy.displayErr(err);
	}

	/**
	 * Call to {@link UploadPolicy#displayInfo(String)}
	 * 
	 * @param info
	 *            The info text to display
	 */
	public void displayInfo(String info) {
		this.uploadPolicy.displayInfo(info);
	}

	/**
	 * Call to {@link UploadPolicy#displayWarn(String)}
	 * 
	 * @param warn
	 *            The error text to be displayed.
	 */
	public void displayWarn(String warn) {
		this.uploadPolicy.displayWarn(warn);
	}

	/**
	 * Call to {@link UploadPolicy#displayDebug(String, int)}
	 * 
	 * @param debug
	 *            The debug message.
	 * @param minDebugLevel
	 *            The minimum level that debug level should have, to display
	 *            this message. Values can go from 0 to 100.
	 */
	public void displayDebug(String debug, int minDebugLevel) {
		this.uploadPolicy.displayDebug(debug, minDebugLevel);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ////////////////////// Helper functions
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Helper function, to get the Revision number, if available. The applet
	 * must be built from the build.xml ant file.
	 * 
	 * @return The svn properties
	 */
	public static Properties getSvnProperties() {
		Properties properties = new Properties();
		Boolean bPropertiesLoaded = false;

		// Let's try to load the properties file.
		// The upload policy is not created yet: we can not use its display
		// methods to trace what is happening here.
		try {
			InputStream isProperties = Class.forName(
					"wjhk.jupload2.JUploadApplet").getResourceAsStream(
					SVN_PROPERTIES_FILENAME);
			properties.load(isProperties);
			isProperties.close();
			bPropertiesLoaded = true;
		} catch (Exception e) {
			// An error occurred when reading the file. The applet was
			// probably not built with the build.xml ant file.
			// We'll create a fake property list. See below.

			// We can not output to the uploadPolicy display method, as the
			// upload policy is not created yet. We output to the system output.
			// Consequence: if this doesn't work during build, you'll see an
			// error during the build: the generated file name will contain the
			// following error message.
			System.out.println(e.getClass().getName()
					+ " in DefaultJUploadContext.getSvnProperties() ("
					+ e.getMessage() + ")");
		}

		// If we could not read the property file. The applet was probably not
		// built with the build.xml ant file, we create a fake property list.
		if (!bPropertiesLoaded) {
			properties.setProperty("buildDate",
					"Unknown build date (please use the build.xml ant script)");
			properties
					.setProperty("lastSrcDirModificationDate",
							"Unknown last modification date (please use the build.xml ant script)");
			properties.setProperty("revision",
					"Unknown revision (please use the build.xml ant script)");
		}
		return properties;
	}

	/** {@inheritDoc} */
	public void registerUnload(Object object, String method) {
		// We insert each item at the beginning, so that the callbacks are
		// called in the reverse order of the order in which they were
		// registered.
		// For instance: the removal of the log file is the first one to be
		// registered ... and must be the last one to be executed.
		this.unloadCallbacks.add(0, new Callback(object, method));
	}

	/** {@inheritDoc} */
	public synchronized void runUnload() {
		// If an upload is runing on, we have to stop it.
		FileUploadManagerThread fileUploadManagerThread = this.getUploadPanel()
				.getFileUploadManagerThread();
		if (fileUploadManagerThread != null) {
			fileUploadManagerThread.stopUpload();
		}

		// Then we call all unload callback.
		for (Callback callback : this.unloadCallbacks) {
			try {
				callback.invoke();
			} catch (Exception e) {
				System.out.println(e.getClass().getName()
						+ " while calling the callback: "
						+ callback.getObject().getClass().getName() + "."
						+ callback.getMethod());
				e.printStackTrace();
			}
		}
		this.unloadCallbacks.clear();
	}

	/**
	 * Displays the debug information for the current parameter.
	 */
	void displayDebugParameterValue(String key, String value) {
		if (this.uploadPolicy != null
				&& this.uploadPolicy.getDebugLevel() >= 80) {
			this.uploadPolicy.displayDebug("Parameter '" + key
					+ "' loaded. Value: " + value, 80);
		}
	}

	/** {@inheritDoc} */
	public int parseInt(String value, int def) {
		int ret = def;
		// Then, parse it as an integer.
		try {
			ret = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			ret = def;
			if (this.uploadPolicy != null) {
				this.uploadPolicy.displayWarn("Invalid int value: " + value
						+ ", using default value: " + def);
			}
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
			if (this.uploadPolicy != null) {
				this.uploadPolicy.displayWarn("Invalid float value: " + value
						+ ", using default value: " + def);
			}
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
			if (this.uploadPolicy != null) {
				this.uploadPolicy.displayWarn("Invalid long value: " + value
						+ ", using default value: " + def);
			}
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
			if (this.uploadPolicy != null) {
				this.uploadPolicy.displayWarn("Invalid boolean value: " + value
						+ ", using default value: " + def);
			}
			return def;
		}
	}

	/**
	 * @return The cursor that was active before the call to this method
	 * @see JUploadContext#setCursor(Cursor)
	 */
	public Cursor setWaitCursor() {
		return setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param url
	 * @param success
	 */
	public void displayURL(String url, boolean success) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.displayURL()");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @return Not used
	 */
	public JApplet getApplet() {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getApplet()");
	}

	/** @see JUploadContext#getFrame() */
	public Frame getFrame() {
		return this.frame;
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @return Not used.
	 */
	public Cursor getCursor() {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getCursor()");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param key
	 * @param def
	 * @return Not used
	 */
	public String getParameter(String key, String def) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getParameter(String, String)");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param key
	 * @param def
	 * @return Not used
	 */
	public int getParameter(String key, int def) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getParameter(String, int))");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param key
	 * @param def
	 * @return Not used
	 */
	public float getParameter(String key, float def) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getParameter(String, float)");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param key
	 * @param def
	 * @return Not used
	 */
	public long getParameter(String key, long def) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getParameter(String, long)");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param key
	 * @param def
	 * @return Not used
	 */
	public boolean getParameter(String key, boolean def) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.getParameter(String, boolean)");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param url
	 * @return Not used
	 * @throws JUploadException
	 */
	public String normalizeURL(String url) throws JUploadException {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.normalizeURL()");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param headers
	 */
	public void readCookieFromNavigator(Vector<String> headers) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.readCookieFromNavigator()");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param headers
	 */
	public void readUserAgentFromNavigator(Vector<String> headers) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.readUserAgentFromNavigator()");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param cursor
	 * @return Not used
	 */
	public Cursor setCursor(Cursor cursor) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.setCursor(Cursor)");
	}

	/**
	 * Just throws a UnsupportedOperationException exception.
	 * 
	 * @param status
	 */
	public void showStatus(String status) {
		throw new UnsupportedOperationException(
				"DefaultJUploadContext.showStatus()");
	}

    @Override
    public void selectFiles() {
        jUploadPanel.doSelectFiles();
    }

    @Override
    public String getSelectedFilesInfoJSON() {
        return uploadPolicy.getSelectedFilesInfoJSON();
    }

    @Override
    public String getProgressInfoJSON() {
        return jUploadPanel.getProgressInfoJSON();
    }

    @Override
    public boolean isUploadFinished() {
        return jUploadPanel.isUploadFinished();
    }
}
