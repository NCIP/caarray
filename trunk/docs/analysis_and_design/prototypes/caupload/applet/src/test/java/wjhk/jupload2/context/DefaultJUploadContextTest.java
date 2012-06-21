package wjhk.jupload2.context;

import java.awt.Cursor;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JWindow;
import javax.swing.RootPaneContainer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.testhelpers.UploadPolicyTestHelper;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;

/**
 * A test to trap calls to jsHandlers, and check what DefaultJUploadContext
 * actually does.
 * 
 * @author etienne_sf
 */
class JavascriptHandlerTestHelper extends JavascriptHandler {
	String lastCommandReceived = null;

	public JavascriptHandlerTestHelper(UploadPolicy uploadPolicy,
			JUploadPanel theJUploadPanel) {
		super(uploadPolicy, theJUploadPanel);
	}

	public synchronized String doCommand(String command) {
		this.lastCommandReceived = command;
		return RETURN_STARTED;
	}
}

/**
 * @author etienne_sf
 */
public class DefaultJUploadContextTest extends AbstractJUploadTestHelper {

	Frame frame = new Frame();

	RootPaneContainer rootPaneContainer = new JWindow();

	DefaultJUploadContext defaultJUploadContext = null;

	boolean unloadHasBeenExecuted = false;

	/** */
	@Before
	public void setUp() {
		this.defaultJUploadContext = new DefaultJUploadContext();
		this.defaultJUploadContext.uploadPolicy = this.uploadPolicy;
		((UploadPolicyTestHelper) this.uploadPolicy).uploadContext = this.juploadContext;
		this.defaultJUploadContext.init(this.frame, this.rootPaneContainer);

		this.juploadContext = this.defaultJUploadContext;
	}

	/** Test of attributes default values */
	@Test
	public void testAttributeCreation() {
		Assert.assertNotNull("svn Properties",
				this.defaultJUploadContext.svnProperties);
		Assert.assertNotNull("svn Properties (jupload.version)",
				this.defaultJUploadContext.svnProperties
						.getProperty("jupload.version"));
		Assert.assertNotNull("svn Properties (jupload.svn.revision)",
				this.defaultJUploadContext.svnProperties
						.getProperty("jupload.svn.revision"));
		Assert.assertNotNull("svn Properties (jupload.buildTimestamp)",
				this.defaultJUploadContext.svnProperties
						.getProperty("jupload.buildTimestamp"));

		Assert.assertNotNull("unloadCallbacks",
				this.defaultJUploadContext.unloadCallbacks);
	}

	/** */
	@Test
	public void testInit() {
		Assert.assertNotNull("uploadPolicy must be set",
				this.defaultJUploadContext.uploadPolicy);
		Assert
				.assertEquals(
						"Check upload policy (mainly to be sure that next tests will work: there must be only one uploadPolicy in the system)",
						this.uploadPolicy,
						this.defaultJUploadContext.uploadPolicy);
		Assert.assertNotNull("logWindow must be set",
				this.defaultJUploadContext.logWindow);
		Assert.assertNotNull("jUploadPanel must be set",
				this.defaultJUploadContext.jUploadPanel);
		Assert.assertEquals("rootPaneContainer",
				this.defaultJUploadContext.jUploadPanel, this.rootPaneContainer
						.getContentPane());
		Assert.assertNotNull("jsHandler must be set",
				this.defaultJUploadContext.jsHandler);
		Assert.assertTrue("jsHandler must be alive",
				this.defaultJUploadContext.jsHandler.isAlive());
		Assert.assertTrue("The thread must be a daemon",
				this.defaultJUploadContext.jsHandler.isDaemon());

		Assert
				.assertEquals(
						"unloadCallbacks must contain one item: for the current context",
						1, this.defaultJUploadContext.unloadCallbacks.size());
		Assert
				.assertEquals(
						"unloadCallbacks must contain one item: for the current context",
						this.defaultJUploadContext,
						this.defaultJUploadContext.unloadCallbacks.get(0)
								.getObject());
	}

	/**
	 * @throws InterruptedException
	 */
	@Test
	public void testUnload() throws InterruptedException {
		// Initial condition: the jsHandler thread is alive. Tested in
		// testInit().
		JavascriptHandler jsHandler = this.defaultJUploadContext.jsHandler;
		this.defaultJUploadContext.unload();
		Assert.assertNull("jsHandler must be cleared",
				this.defaultJUploadContext.jsHandler);
		Thread.sleep(100);
		Assert.assertFalse("jsHandler must be stopped", jsHandler.isAlive());
	}

	/**
	 * @throws JUploadException
	 */
	@Test
	public void testGetters() throws JUploadException {
		Assert.assertEquals("Last modified is taken from svnProperties",
				this.defaultJUploadContext.svnProperties
						.getProperty("jupload.lastSrcDirModificationDate"),
				this.juploadContext.getLastModified());
		Date date = new Date(Long
				.parseLong(this.defaultJUploadContext.svnProperties
						.getProperty("jupload.buildTimestamp")));
		Assert.assertEquals("buildDate is taken from svnProperties",
				MessageFormat.format("{0,date,medium}", date),
				this.juploadContext.getBuildDate());

		Assert
				.assertEquals(
						"buildNumber is taken from svnProperties (currently: no build number)",
						"-1", Integer.toString(this.juploadContext
								.getBuildNumber()));

		Assert.assertEquals("logWindow getter",
				this.defaultJUploadContext.logWindow, this.juploadContext
						.getLogWindow());
		Assert.assertEquals("jUploadPanel getter",
				this.defaultJUploadContext.jUploadPanel, this.juploadContext
						.getUploadPanel());
		Assert.assertEquals("uploadPolicy getter",
				this.defaultJUploadContext.uploadPolicy, this.juploadContext
						.getUploadPolicy());

		Assert.assertEquals("frame getter", this.defaultJUploadContext.frame,
				this.juploadContext.getFrame());
		try {
			this.juploadContext.getCursor();
			Assert.fail("cursor getter should throw an exception");
		} catch (UnsupportedOperationException e) {
			// Success!
		}

		String dummy = "dummy parameter name";
		try {
			String def = "0";
			this.juploadContext.getParameter(dummy, def);
			Assert.fail("getParameter(String, int) should throw an exception");
		} catch (UnsupportedOperationException e) {
			// Success!
		}
		try {
			int def = 0;
			this.juploadContext.getParameter(dummy, def);
			Assert.fail("getParameter(String, int) should throw an exception");
		} catch (UnsupportedOperationException e) {
			// Success!
		}
		try {
			float def = 0;
			this.juploadContext.getParameter(dummy, def);
			Assert.fail("getParameter(String, int) should throw an exception");
		} catch (UnsupportedOperationException e) {
			// Success!
		}
		try {
			long def = 0;
			this.juploadContext.getParameter(dummy, def);
			Assert.fail("getParameter(String, int) should throw an exception");
		} catch (UnsupportedOperationException e) {
			// Success!
		}
		try {
			boolean def = true;
			this.juploadContext.getParameter(dummy, def);
			Assert.fail("getParameter(String, int) should throw an exception");
		} catch (UnsupportedOperationException e) {
			// Success!
		}
	}

	/** */
	@Test
	public void testGetMimeType() {
		Assert.assertEquals("txt file", "text/plain", this.juploadContext
				.getMimeType("txt"));
		Assert.assertEquals("jepg file", "image/jpeg", this.juploadContext
				.getMimeType("jpeg"));
		Assert.assertEquals("jpg file", "image/jpeg", this.juploadContext
				.getMimeType("jpg"));
		Assert
				.assertEquals(
						"docx file",
						"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
						this.juploadContext.getMimeType("docx"));
	}

	/**
	 * @throws JUploadException
	 * @throws InterruptedException
	 */
	@Test
	public void testSetProperty() throws JUploadException, InterruptedException {
		// We change the value to times, to be sure that it's a success (not
		// just luck that the previous value was the same we are testing)
		this.juploadContext.setProperty(UploadPolicy.PROP_NB_FILES_PER_REQUEST,
				"3");
		// The setProperty is called by invokeLater. So we wait a little, so
		// that the setter is actually executed.
		Thread.sleep(100);

		Assert.assertEquals("Check the value set (nbFilesPerRequest)", 3,
				this.uploadPolicy.getNbFilesPerRequest());
		this.juploadContext.setProperty(UploadPolicy.PROP_NB_FILES_PER_REQUEST,
				"4");
		// The setProperty is called by invokeLater. So we wait a little, so
		// that the setter is actually executed.
		Thread.sleep(100);
		Assert.assertEquals("Check the value set (nbFilesPerRequest)", 4,
				this.uploadPolicy.getNbFilesPerRequest());
	}

	/** */
	@Test
	public void testStartUpload() {
		JavascriptHandlerTestHelper jsHandlerTestHelper = new JavascriptHandlerTestHelper(
				this.uploadPolicy, this.uploadPolicy.getContext()
						.getUploadPanel());
		this.defaultJUploadContext.jsHandler = jsHandlerTestHelper;
		Assert.assertNull("No command at initialization",
				jsHandlerTestHelper.lastCommandReceived);
		this.juploadContext.startUpload();
		Assert.assertEquals("The correct command after at the end of the test",
				JavascriptHandler.COMMAND_START_UPLOAD,
				jsHandlerTestHelper.lastCommandReceived);
	}

	/** */
	@Test
	public void testDisplayErr() {
		String msg = "An error message";
		this.defaultJUploadContext.displayErr(msg);
		Assert.assertEquals("Check message", msg,
				((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived);
	}

	/** */
	@Test
	public void testDisplayInfo() {
		String msg = "An information message";
		this.defaultJUploadContext.displayInfo(msg);
		Assert.assertEquals("Check message", msg,
				((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived);
	}

	/** */
	@Test
	public void testDisplayWarn() {
		String msg = "A warning message";
		this.defaultJUploadContext.displayWarn(msg);
		Assert.assertTrue("Check message",
				((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived
						.endsWith(msg));
	}

	/** */
	@Test
	public void testDisplayDebug() {
		String msg = "A debug message";
		this.defaultJUploadContext.displayDebug(msg, -1);
		Assert.assertEquals("Check message", msg,
				((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived);
	}

	/** */
	@Test
	public void testGetSvnProperties() {
		Properties p = DefaultJUploadContext.getSvnProperties();
		Assert.assertEquals("Check buildDate", "1111111111111", p
				.getProperty("jupload.buildTimestamp"));
		Assert.assertEquals("Check buildNumber", "A version", p
				.getProperty("jupload.version"));
		Assert.assertEquals("Check revision", "A revision number", p
				.getProperty("jupload.svn.revision"));
		Assert.assertEquals("Check lastSrcDirModificationDate",
				"A source modification date", p
						.getProperty("jupload.lastSrcDirModificationDate"));
	}

	/**
	 * This method is the entry point given to the
	 * {@link JUploadContext#registerUnload(Object, String)} method. It is not a
	 * JUnit test.
	 */
	public void anUnloadMethod() {
		this.unloadHasBeenExecuted = true;
	}

	/** */
	@Test
	public void testRegisterUnload() {
		Assert
				.assertEquals(
						"DefaultUploadContexwt should have registered one callback when starting the test",
						1, this.defaultJUploadContext.unloadCallbacks.size());
		this.juploadContext.registerUnload(this, "anUnloadMethod");
		Assert.assertEquals("One callback after the test", 2,
				this.defaultJUploadContext.unloadCallbacks.size());
		Assert.assertEquals("Checking callback object", this,
				this.defaultJUploadContext.unloadCallbacks.get(0).getObject());
		Assert.assertEquals("Checking callback method", "anUnloadMethod",
				this.defaultJUploadContext.unloadCallbacks.get(0).getMethod());
	}

	/** */
	@Test
	public void testRunUnload() {
		this.juploadContext.registerUnload(this, "anUnloadMethod");
		Assert.assertFalse("Unload has not been executed yet",
				this.unloadHasBeenExecuted);
		this.juploadContext.runUnload();
		Assert.assertTrue("Unload has been executed",
				this.unloadHasBeenExecuted);
	}

	/** */
	@Test
	public void testDisplayDebugParameterValue() {
		String key = "A key";
		String value = "A value";
		String dummyValue = "no real value";
		((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived = dummyValue;

		// With debugLevel set to 79, there should be no output.
		System.setProperty(UploadPolicy.PROP_DEBUG_LEVEL, "79");
		this.defaultJUploadContext.displayDebugParameterValue(key, value);
		Assert.assertEquals("Check message", dummyValue,
				((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived);

		// With debugLevel set to 80, there should be an output.
		System.setProperty(UploadPolicy.PROP_DEBUG_LEVEL, "80");
		this.defaultJUploadContext.displayDebugParameterValue(key, value);
		Assert.assertEquals("Check message", "Parameter '" + key
				+ "' loaded. Value: " + value,
				((UploadPolicyTestHelper) this.uploadPolicy).lastMsgReceived);
	}

	/** */
	@Test
	public void testParseInt() {
		Assert.assertEquals("A valid number", 12345, this.defaultJUploadContext
				.parseInt("12345", -1));
		Assert.assertEquals("An invalid number", -234,
				this.defaultJUploadContext.parseInt(" QSDQ ", -234));
	}

	/** */
	@Test
	public void testParseFloat() {
		Assert.assertEquals("A valid number", 12345.6789,
				this.defaultJUploadContext.parseFloat(" 12345.6789 ", -1),
				0.001);
		Assert.assertEquals("An invalid number", -456.11235,
				this.defaultJUploadContext.parseFloat(" ZAEZA ",
						(float) -456.11235), 0.0001);
	}

	/** */
	@Test
	public void testParseLong() {
		Assert
				.assertEquals("A valid number", 9223372036854775807L,
						this.defaultJUploadContext.parseLong(
								"9223372036854775807", -1));
		Assert.assertEquals("An invalid number", -9223372036854775808L,
				this.defaultJUploadContext.parseLong(" QSDQ ",
						-9223372036854775808L));
	}

	/** */
	@Test
	public void testParseBoolean() {
		Assert.assertEquals("A valid boolean", true, this.defaultJUploadContext
				.parseBoolean("true", false));
		Assert.assertEquals("A valid boolean", false,
				this.defaultJUploadContext.parseBoolean("false", true));
		Assert.assertEquals("A valid boolean", false,
				this.defaultJUploadContext.parseBoolean("FAlSE", true));
		Assert.assertEquals("An invalid boolean", false,
				this.defaultJUploadContext.parseBoolean("QSDQ", false));
		Assert.assertEquals("An invalid boolean", true,
				this.defaultJUploadContext.parseBoolean("QSDQ", true));
	}

	/** */
	@Test(expected = UnsupportedOperationException.class)
	public void testSetWaitCursor() {
		this.juploadContext.setWaitCursor();
	}

	/** */
	@Test(expected = UnsupportedOperationException.class)
	public void testDisplayURL() {
		this.defaultJUploadContext.displayURL("http://jupload.sourceforge.net",
				true);
	}

	/**
	 * @throws JUploadException
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testNormalizeURL() throws JUploadException {
		this.defaultJUploadContext
				.normalizeURL("http://jupload.sourceforge.net");
	}

	/** */
	@Test(expected = UnsupportedOperationException.class)
	public void testReadCookieFromNavigator() {
		this.defaultJUploadContext
				.readCookieFromNavigator(new Vector<String>());
	}

	/** */
	@Test(expected = UnsupportedOperationException.class)
	public void testReadUserAgentFromNavigator() {
		this.defaultJUploadContext
				.readUserAgentFromNavigator(new Vector<String>());
	}

	/** */
	@Test(expected = UnsupportedOperationException.class)
	public void testSetCursor() {
		this.defaultJUploadContext.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	/** */
	@Test(expected = UnsupportedOperationException.class)
	public void testShowStatus() {
		this.defaultJUploadContext.showStatus("status");
	}
}
