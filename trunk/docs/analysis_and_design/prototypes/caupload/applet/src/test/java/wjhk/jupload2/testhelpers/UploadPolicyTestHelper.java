package wjhk.jupload2.testhelpers;

import java.awt.Cursor;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import wjhk.jupload2.context.JUploadContext;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadExceptionStopAddingFiles;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.JUploadFileChooser;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.helper.ByteArrayEncoder;
import wjhk.jupload2.upload.helper.InteractiveTrustManager;

/**
 * This basic {@link UploadPolicy} is used to run test cases, and manage what
 * happens in them.
 * 
 * @author etienne_sf
 */
public class UploadPolicyTestHelper implements UploadPolicy {
	/** Logger for this class */
	protected final Logger logger = Logger.getLogger(getClass());

	/** */
	public JUploadContext uploadContext = null;

	/** */
	public long maxFileSize;

	/** */
	public String lastMsgReceived = null;

	/**
	 * Creates a JUploadContextTextCase, which will read the default test
	 * properties file.
	 * 
	 * @param juploadPanel
	 */
	public UploadPolicyTestHelper(JUploadPanel juploadPanel) {
		this.uploadContext = new JUploadContextTestHelper(this, juploadPanel);

		this.maxFileSize = this.uploadContext.getParameter(PROP_MAX_FILE_SIZE,
				DEFAULT_MAX_FILE_SIZE);
	}

	/** @see UploadPolicy#start() */
	public void start() {
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#addComponentsToJUploadPanel(wjhk.jupload2.gui.JUploadPanel)
	 */
	public void addComponentsToJUploadPanel(JUploadPanel jUploadPanel) {
		displayWarn("UnsupportedOperationException: addComponentsToJUploadPanel() is not implemented in tests cases");

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#addHeader(java.lang.String)
	 */
	public void addHeader(String header) {
		this.logger.warn(this.getClass()
				+ ".addHeader() is not implemented in tests cases");

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#afterFileDropped(java.awt.dnd.DropTargetDropEvent)
	 */
	public void afterFileDropped(DropTargetDropEvent dropEvent) {
		this.logger.warn(this.getClass()
				+ ".afterFileDropped() is not implemented in tests cases");

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#afterUpload(java.lang.Exception,
	 *      java.lang.String)
	 */
	public void afterUpload(Exception e, String serverOutput)
			throws JUploadException {
		this.logger.warn(this.getClass()
				+ ".afterUpload() is not implemented in tests cases");

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#alert(java.lang.String)
	 */
	public void alert(String key) {
		this.logger.warn(this.getClass()
				+ ".alert() is not implemented in tests cases");

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#alertStr(java.lang.String)
	 */
	public void alertStr(String str) {
		this.logger.warn(this.getClass()
				+ ".alertStr() is not implemented in tests cases");

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#beforeUpload()
	 */
	public boolean beforeUpload() {
		this.logger.warn(this.getClass()
				+ ".beforeUpload() is not implemented in tests cases");
		return true;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#checkUploadSuccess(int,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean checkUploadSuccess(int status, String msg, String body)
			throws JUploadException {
		this.logger.warn(this.getClass()
				+ ".checkUploadSuccess() is not implemented in tests cases");
		return true;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#confirmDialogStr(java.lang.String,
	 *      int)
	 */
	public int confirmDialogStr(String str, int optionTypes) {
		this.logger.warn(this.getClass()
				+ ".confirmDialogStr() is not implemented in tests cases");
		return JOptionPane.OK_OPTION;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#createFileChooser()
	 */
	public JUploadFileChooser createFileChooser() {
		this.logger.warn(this.getClass()
				+ ".createFileChooser() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#createFileData(java.io.File,
	 *      java.io.File)
	 */
	public FileData createFileData(File file, File root)
			throws JUploadExceptionStopAddingFiles {
		this.logger.warn(this.getClass()
				+ ".createFileData() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#createProgressPanel(javax.swing.JProgressBar,
	 *      javax.swing.JProgressBar, javax.swing.JButton, javax.swing.JButton,
	 *      JUploadPanel)
	 */
	public JPanel createProgressPanel(JProgressBar preparationProgressBar,
			JProgressBar uploadProgressBar, JButton uploadButton,
			JButton stopButton, JUploadPanel mainPanel) {
		this.logger.warn(this.getClass()
				+ ".createProgressPanel() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#createStatusBar(javax.swing.JLabel,
	 *      JUploadPanel)
	 */
	public JPanel createStatusBar(JLabel statusContent, JUploadPanel mainPanel) {
		this.logger.warn(this.getClass()
				+ ".createStatusBar() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#createTopPanel(javax.swing.JButton,
	 *      javax.swing.JButton, javax.swing.JButton,
	 *      wjhk.jupload2.gui.JUploadPanel)
	 */
	public JPanel createTopPanel(JButton browse, JButton remove,
			JButton removeAll, JUploadPanel mainPanel) {
		this.logger.warn(this.getClass()
				+ ".createTopPanel() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayDebug(java.lang.String,
	 *      int)
	 */
	public void displayDebug(String debug, int minDebugLevel) {
		this.lastMsgReceived = debug;
		this.logger.debug(debug);
	}

	void displayMsg(String msg) {
		this.lastMsgReceived = msg;
		this.logger.info(msg);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayErr(java.lang.Exception)
	 */
	public void displayErr(Exception e) {
		this.lastMsgReceived = null;
		this.logger.error(e.getClass().getName() + ": " + e.getMessage());
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayErr(java.lang.String,
	 *      java.lang.Exception, int)
	 */
	public int displayErr(String err, Exception e, int optionType) {
		this.lastMsgReceived = err;
		this.logger.error("[ERROR] " + e.getClass().getName() + ": "
				+ e.getMessage() + " [" + err + "]");
		return JOptionPane.OK_OPTION;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayErr(java.lang.String)
	 */
	public void displayErr(String err) {
		this.lastMsgReceived = err;
		this.logger.error("[ERROR] " + err);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayErr(java.lang.String,
	 *      java.lang.Exception)
	 */
	public void displayErr(String err, Exception e) {
		this.lastMsgReceived = err;
		this.logger.error("[ERROR] " + e.getClass().getName() + ": "
				+ e.getMessage() + " [" + err + "]");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayInfo(java.lang.String)
	 */
	public void displayInfo(String info) {
		this.lastMsgReceived = info;
		this.logger.info("[INFO] " + info);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayParameterStatus()
	 */
	public void displayParameterStatus() {
		this.logger
				.warn(this.getClass()
						+ ".displayParameterStatus() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#displayWarn(java.lang.String)
	 */
	public void displayWarn(String warn) {
		this.lastMsgReceived = warn;
		displayMsg("[WARNING] " + warn);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#fileFilterAccept(java.io.File)
	 */
	public boolean fileFilterAccept(File file) {
		this.logger.warn(this.getClass()
				+ ".fileFilterAccept() is not implemented in tests cases");
		return true;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#fileFilterGetDescription()
	 */
	public String fileFilterGetDescription() {
		this.logger
				.warn(this.getClass()
						+ ".fileFilterGetDescription() is not implemented in tests cases");
		return null;

	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#fileViewGetIcon(java.io.File)
	 */
	public Icon fileViewGetIcon(File file) {
		this.logger.warn(this.getClass()
				+ ".fileViewGetIcon() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getAfterUploadTarget()
	 */
	public String getAfterUploadTarget() {
		return this.uploadContext.getParameter(PROP_AFTER_UPLOAD_TARGET,
				DEFAULT_AFTER_UPLOAD_TARGET);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getAfterUploadURL()
	 */
	public String getAfterUploadURL() {
		return this.uploadContext.getParameter(PROP_AFTER_UPLOAD_URL,
				DEFAULT_AFTER_UPLOAD_URL);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getAllowHttpPersistent()
	 */
	public boolean getAllowHttpPersistent() {
		return this.uploadContext.getParameter(PROP_ALLOW_HTTP_PERSISTENT,
				DEFAULT_ALLOW_HTTP_PERSISTENT);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getAllowedFileExtensions()
	 */
	public String getAllowedFileExtensions() {
		return this.uploadContext.getParameter(PROP_ALLOWED_FILE_EXTENSIONS,
				DEFAULT_ALLOWED_FILE_EXTENSIONS);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getContext()
	 */
	public JUploadContext getContext() {
		return this.uploadContext;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getCurrentBrowsingDirectory()
	 */
	public File getCurrentBrowsingDirectory() {
		this.logger
				.warn(this.getClass()
						+ ".getCurrentBrowsingDirectory() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getDateFormat()
	 */
	public String getDateFormat() {
		return new SimpleDateFormat().toPattern();
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getDebugLevel()
	 */
	public int getDebugLevel() {
		return this.uploadContext.getParameter(PROP_DEBUG_LEVEL,
				DEFAULT_DEBUG_LEVEL);
	}

	/** @see UploadPolicy#getFileFilterName() */
	public String getFileFilterName() {
		return this.uploadContext.getParameter(PROP_FILE_FILTER_NAME,
				DEFAULT_FILE_FILTER_NAME);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFileChooserIconFromFileContent()
	 */
	public int getFileChooserIconFromFileContent() {
		return this.uploadContext.getParameter(
				PROP_FILE_CHOOSER_ICON_FROM_FILE_CONTENT,
				DEFAULT_FILE_CHOOSER_ICON_FROM_FILE_CONTENT);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFileChooserIconSize()
	 */
	public int getFileChooserIconSize() {
		return this.uploadContext.getParameter(PROP_FILE_CHOOSER_ICON_SIZE,
				DEFAULT_FILE_CHOOSER_ICON_SIZE);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFilenameEncoding()
	 */
	public String getFilenameEncoding() {
		return this.uploadContext.getParameter(PROP_FILENAME_ENCODING,
				DEFAULT_FILENAME_ENCODING);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFormdata()
	 */
	public String getFormdata() {
		return this.uploadContext.getParameter(PROP_FORMDATA, DEFAULT_FORMDATA);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFtpCreateDirectoryStructure()
	 */
	public boolean getFtpCreateDirectoryStructure() {
		return this.uploadContext.getParameter(
				PROP_FTP_CREATE_DIRECTORY_STRUCTURE,
				DEFAULT_FTP_CREATE_DIRECTORY_STRUCTURE);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFtpTransfertBinary()
	 */
	public boolean getFtpTransfertBinary() {
		return this.uploadContext.getParameter(PROP_FTP_TRANSFERT_BINARY,
				DEFAULT_FTP_TRANSFERT_BINARY);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getFtpTransfertPassive()
	 */
	public boolean getFtpTransfertPassive() {
		return this.uploadContext.getParameter(PROP_FTP_TRANSFERT_PASSIVE,
				DEFAULT_FTP_TRANSFERT_PASSIVE);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getHttpUploadParameterName()
	 */
	public String getHttpUploadParameterName() {
		return this.uploadContext.getParameter(PROP_HTTP_UPLOAD_PARAMETER_NAME,
				DEFAULT_HTTP_UPLOAD_PARAMETER_NAME);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getHttpUploadParameterType()
	 */
	public String getHttpUploadParameterType() {
		return this.uploadContext.getParameter(PROP_HTTP_UPLOAD_PARAMETER_TYPE,
				DEFAULT_HTTP_UPLOAD_PARAMETER_TYPE);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getLastException()
	 */
	public JUploadException getLastException() {
		this.logger.warn(this.getClass()
				+ ".getLastException() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getLastResponseBody()
	 */
	public String getLastResponseBody() {
		this.logger.warn(this.getClass()
				+ ".getLastResponseBody() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getLastResponseMessage()
	 */
	public String getLastResponseMessage() {
		this.logger
				.warn(this.getClass()
						+ ".getLastResponseMessage() is not implemented in tests cases");
		return null;
	}

	/** {@inheritDoc} */
	public Locale getLocale() {
		return Locale.getDefault();
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getLocalizedString(java.lang.String,
	 *      java.lang.Object[])
	 */
	public String getLocalizedString(String key, Object... args) {
		if (key.equals("dateformat")) {
			return "MM/dd/yyyy";
		} else {
			return key;
		}
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getMaxChunkSize()
	 */
	public long getMaxChunkSize() {
		return this.uploadContext.getParameter(PROP_MAX_CHUNK_SIZE,
				DEFAULT_MAX_CHUNK_SIZE);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getMaxFileSize()
	 */
	public long getMaxFileSize() {
		return this.maxFileSize;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getNbFilesPerRequest()
	 */
	public int getNbFilesPerRequest() {
		return this.uploadContext.getParameter(PROP_NB_FILES_PER_REQUEST,
				DEFAULT_NB_FILES_PER_REQUEST);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getPostURL()
	 */
	public String getPostURL() {
		return this.uploadContext.getParameter(PROP_POST_URL, DEFAULT_POST_URL);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getReadCookieFromNavigator()
	 */
	public boolean getReadCookieFromNavigator() {
		return this.uploadContext.getParameter(PROP_READ_COOKIE_FROM_NAVIGATOR,
				DEFAULT_READ_COOKIE_FROM_NAVIGATOR);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getReadUserAgentFromNavigator()
	 */
	public boolean getReadUserAgentFromNavigator() {
		return this.uploadContext.getParameter(
				PROP_READ_USER_AGENT_FROM_NAVIGATOR,
				DEFAULT_READ_USER_AGENT_FROM_NAVIGATOR);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getServerProtocol()
	 */
	public String getServerProtocol() {
		return this.uploadContext.getParameter(PROP_SERVER_PROTOCOL,
				DEFAULT_SERVER_PROTOCOL);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getShowLogWindow()
	 */
	public String getShowLogWindow() {
		return this.uploadContext.getParameter(PROP_SHOW_LOGWINDOW,
				DEFAULT_SHOW_LOGWINDOW);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getSpecificHeaders()
	 */
	public String getSpecificHeaders() {
		return this.uploadContext.getParameter(PROP_SPECIFIC_HEADERS,
				DEFAULT_SPECIFIC_HEADERS);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getSslVerifyCert()
	 */
	public int getSslVerifyCert() {
		final String sslVerifyCert = this.uploadContext.getParameter(
				PROP_SSL_VERIFY_CERT, DEFAULT_SSL_VERIFY_CERT);
		if (sslVerifyCert.toLowerCase().equals("none")) {
			return InteractiveTrustManager.NONE;
		} else if (sslVerifyCert.toLowerCase().equals("server")) {
			return InteractiveTrustManager.SERVER;
		} else if (sslVerifyCert.toLowerCase().equals("client")) {
			return InteractiveTrustManager.CLIENT;
		} else if (sslVerifyCert.toLowerCase().equals("strict")) {
			return InteractiveTrustManager.STRICT;
		} else {
			this.logger.warn("Invalid parameter sslVerifyCert ("
					+ sslVerifyCert + ")");
			return InteractiveTrustManager.NONE;
		}
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getStringUploadError()
	 */
	public String getStringUploadError() {
		return this.uploadContext.getParameter(PROP_STRING_UPLOAD_ERROR,
				DEFAULT_STRING_UPLOAD_ERROR);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getStringUploadSuccess()
	 */
	public String getStringUploadSuccess() {
		return this.uploadContext.getParameter(PROP_STRING_UPLOAD_SUCCESS,
				DEFAULT_STRING_UPLOAD_SUCCESS);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getStringUploadWarning()
	 */
	public String getStringUploadWarning() {
		return this.uploadContext.getParameter(PROP_STRING_UPLOAD_WARNING,
				DEFAULT_STRING_UPLOAD_WARNING);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getUploadFilename(wjhk.jupload2.filedata.FileData,
	 *      int)
	 */
	public String getUploadFilename(final FileData fileData, final int index)
			throws JUploadException {
		this.logger.warn(this.getClass()
				+ ".getUploadFilename() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getUploadName(wjhk.jupload2.filedata.FileData,
	 *      int)
	 */
	public String getUploadName(final FileData fileData, final int index)
			throws JUploadException {
		this.logger.warn(this.getClass()
				+ ".getUploadName() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#getUrlToSendErrorTo()
	 */
	public String getUrlToSendErrorTo() {
		return this.uploadContext.getParameter(PROP_URL_TO_SEND_ERROR_TO,
				DEFAULT_URL_TO_SEND_ERROR_TO);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#onAppendHeader(wjhk.jupload2.upload.helper.ByteArrayEncoder)
	 */
	public ByteArrayEncoder onAppendHeader(final ByteArrayEncoder sb)
			throws JUploadIOException {
		this.logger.warn(this.getClass()
				+ ".onAppendHeader() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#onFileDoubleClicked(wjhk.jupload2.filedata.FileData)
	 */
	public void onFileDoubleClicked(FileData fileData) {
		this.logger.warn(this.getClass()
				+ ".onFileDoubleClicked() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#onFileSelected(wjhk.jupload2.filedata.FileData)
	 */
	public void onFileSelected(FileData fileData) {
		this.logger.warn(this.getClass()
				+ ".onFileSelected() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#sendDebugInformation(java.lang.String,
	 *      java.lang.Exception)
	 */
	public void sendDebugInformation(String reason, Exception exception) {
		this.logger.warn(this.getClass()
				+ ".sendDebugInformation() is not implemented in tests cases");
	}

	/**
	 * @param currentBrowsingDirectoryParam
	 */
	public void setCurrentBrowsingDirectory(File currentBrowsingDirectoryParam) {
		this.logger
				.warn(this.getClass()
						+ ".setCurrentBrowsingDirectory() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setCurrentBrowsingDirectory(java.lang.String)
	 */
	public void setCurrentBrowsingDirectory(String currentBrowsingDirectoryParam) {
		this.logger
				.warn(this.getClass()
						+ ".setCurrentBrowsingDirectory() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setCursor(java.awt.Cursor)
	 */
	public Cursor setCursor(Cursor cursor) {
		this.logger.warn(this.getClass()
				+ ".setCursor() is not implemented in tests cases");
		return null;
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setDebugLevel(int)
	 */
	public void setDebugLevel(int debugLevel) {
		this.logger.warn(this.getClass()
				+ ".setDebugLevel() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setLang(java.lang.String)
	 */
	public void setLang(String lang) {
		this.logger.warn(this.getClass()
				+ ".setLang() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setPostURL(java.lang.String)
	 */
	public void setPostURL(String postURL) throws JUploadException {
		this.logger.warn(this.getClass()
				+ ".setPostURL() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty(String prop, String value) throws JUploadException {
		System.setProperty(prop, value);
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setShowLogWindow(java.lang.String)
	 */
	public void setShowLogWindow(String showLogWindow) {
		this.logger.warn(this.getClass()
				+ ".setShowLogWindow() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setUrlToSendErrorTo(java.lang.String)
	 */
	public void setUrlToSendErrorTo(String urlToSendErrorTo)
			throws JUploadException {
		this.logger.warn(this.getClass()
				+ ".setUrlToSendErrorTo() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setWaitCursor()
	 */
	public Cursor setWaitCursor() {
		this.logger.warn(this.getClass()
				+ ".setWaitCursor() is not implemented in tests cases");
		return null;
	}

	/** @see UploadPolicy#getSendMD5Sum() */
	public boolean getSendMD5Sum() {
		this.logger.warn(this.getClass()
				+ ".getSendMD5Sum() is not implemented in tests cases");
		return false;
	}

	/** @see UploadPolicy#setSendMD5Sum(boolean) */
	public void setSendMD5Sum(boolean sendMD5Sum) {
		this.logger.warn(this.getClass()
				+ ".setSendMD5Sum() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.policies.UploadPolicy#setServerProtocol(java.lang.String)
	 */
	public void setServerProtocol(String serverProtocol) {
		this.logger.warn(this.getClass()
				+ ".setServerProtocol() is not implemented in tests cases");
	}

	/** @see UploadPolicy#getRetryMaxNumberOf() */
	public int getRetryMaxNumberOf() {
		this.logger.warn(this.getClass()
				+ ".getRetryMaxNumberOf() is not implemented in tests cases");
		return 0;
	}

	/** @see UploadPolicy#getRetryNbSecondsBetween() */
	public int getRetryNbSecondsBetween() {
		this.logger
				.warn(this.getClass()
						+ ".getRetryNbSecondsBetween() is not implemented in tests cases");
		return 0;
	}

	/**
	 * @param executionStatus
	 */
	public void updateButtonState(int executionStatus) {
		// No action here
	}

	
    @Override
    public void setCurrentDirectory(File currentDirectory) {
    }

    @Override
    public void setSelectedFiles(File[] selectedFiles) {
    }

    @Override
    public String getSelectedFilesInfoJSON() {
        return "";
    }
}
