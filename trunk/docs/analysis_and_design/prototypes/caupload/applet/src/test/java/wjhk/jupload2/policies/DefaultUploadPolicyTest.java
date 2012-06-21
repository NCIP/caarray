package wjhk.jupload2.policies;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.*;

import wjhk.jupload2.JUploadDaemon;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadExceptionStopAddingFiles;
import wjhk.jupload2.exception.JUploadExceptionUploadFailed;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.testhelpers.FileDataTestHelper;
import wjhk.jupload2.testhelpers.JUploadContextTestHelper;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;

/**
 * @author etienne_sf
 */
public class DefaultUploadPolicyTest extends AbstractJUploadTestHelper {

    final static String DEFAULT_UPLOADPOLICY_PROPERTIES = "/tests/policies/default_uploadPolicy.properties";

    Properties defaultProperties;

    /**
     * Creation of JUploadApplet or JUploadExecutable
     * 
     * @throws JUploadException
     */
    @Before
    public void initJUpload() throws JUploadException {
        this.juploadDaemon = new JUploadDaemon();
        this.juploadContext = new JUploadContextTestHelper(this.juploadPanel);

        // A try with Mockito. Very nice framework. As the new generates
        this.uploadPolicy = spy(new DefaultUploadPolicy(this.juploadContext));
        ((JUploadContextTestHelper) this.juploadContext).uploadPolicy = this.uploadPolicy;
        doNothing().when(this.uploadPolicy).alertStr(anyString());
        doNothing().when(this.uploadPolicy).alert(anyString());
        doNothing().when(this.uploadPolicy).displayErr(anyString());

    }

    /**
     * @throws Exception
     */
    @Test
    public void testSetPostURL() throws Exception {
        String postURL = "/test.jsp";

        ((DefaultUploadPolicy) this.uploadPolicy).setPostURL(postURL);
        String url = this.uploadPolicy.getPostURL();
        Assert.assertNotNull("The postURL is mandatory", url);
        Assert.assertTrue("postURL set to '" + postURL + "'", url
                .endsWith(postURL));

        postURL = "ftp://127.0.0.1/pub";
        initJUpload();
        ((DefaultUploadPolicy) this.uploadPolicy).setPostURL(postURL);
        url = this.uploadPolicy.getPostURL();
        Assert.assertTrue("postURL set to '" + postURL + "'", url
                .endsWith(postURL));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSetCurrentBrowsingDirectoryString() throws Exception {
        String dir = AbstractJUploadTestHelper.getTestFilesRootPath();

        ((DefaultUploadPolicy) this.uploadPolicy)
                .setCurrentBrowsingDirectory(dir);

        Assert.assertEquals("current browsing directory set to '" + dir + "'",
                dir, this.uploadPolicy.getCurrentBrowsingDirectory()
                        .getAbsolutePath()
                        + File.separator);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSetCurrentBrowsingDirectoryFile() throws Exception {
        // We need an existing folder.
        File tmpFile = File.createTempFile("prefix", "suffix");
        File dir = tmpFile.getParentFile();

        ((DefaultUploadPolicy) this.uploadPolicy)
                .setCurrentBrowsingDirectory(dir);

        Assert.assertEquals("current browsing directory set to '"
                + dir.getAbsolutePath() + "'", dir.getAbsolutePath(),
                this.uploadPolicy.getCurrentBrowsingDirectory()
                        .getAbsolutePath());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSetAfterUploadTarget_Null() throws Exception {
        ((DefaultUploadPolicy) this.uploadPolicy).setAfterUploadURL(null);
        Assert.assertNull("AfterUploadURL (null)", this.uploadPolicy
                .getAfterUploadURL());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSetAfterUploadTarget_RelativeURL() throws Exception {
        String afterUploadURL = "/after/upload/url.jsp";
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAfterUploadURL(afterUploadURL);
        Assert.assertEquals("AfterUploadURL (URL)", afterUploadURL,
                this.uploadPolicy.getAfterUploadURL());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSetAfterUploadTarget_javascript() throws Exception {
        String afterUploadURL = "javascript:afterUpload()";
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAfterUploadURL(afterUploadURL);
        Assert.assertEquals("AfterUploadURL (javascript)", afterUploadURL,
                this.uploadPolicy.getAfterUploadURL());
    }

    /**
     * 
     */
    @Test
    public void testSetAllowHttpPersistent() {
        boolean allowHttpPersistent = true;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAllowHttpPersistent(allowHttpPersistent);
        Assert.assertEquals("allowHttpPersistent (true)", allowHttpPersistent,
                this.uploadPolicy.getAllowHttpPersistent());
        allowHttpPersistent = false;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAllowHttpPersistent(allowHttpPersistent);
        Assert.assertEquals("allowHttpPersistent (false)", allowHttpPersistent,
                this.uploadPolicy.getAllowHttpPersistent());
    }

    /** */
    @Test
    public void testSetAllowedFileExtensions() {
        String allowedFileExtensions = "txt/jpg";
        String allowedFileExtensionsCorrected = "/" + allowedFileExtensions
                + "/";
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAllowedFileExtensions(allowedFileExtensions);
        Assert.assertEquals("allowedFileExtensions (" + allowedFileExtensions
                + ")", allowedFileExtensionsCorrected, this.uploadPolicy
                .getAllowedFileExtensions());
        allowedFileExtensions = null;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAllowedFileExtensions(allowedFileExtensions);
        Assert.assertEquals("allowedFileExtensions (null)",
                allowedFileExtensions, this.uploadPolicy
                        .getAllowedFileExtensions());
    }

    /** */
    @Test
    public void testSetFileFilterName() {
        String allowedFileExtensions = "txt/jpg";
        String fileFilterName = "A name for the file filter";

        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAllowedFileExtensions(allowedFileExtensions);
        Assert.assertFalse("fileFilterName not set (" + allowedFileExtensions
                + ")", fileFilterName.equals(this.uploadPolicy
                .getFileFilterName()));

        // Let's set our fileFilterName
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFileFilterName(fileFilterName);
        Assert.assertTrue("getFileFilterName(): fileFilterName not set ("
                + allowedFileExtensions + ")", this.uploadPolicy
                .getFileFilterName().equals(fileFilterName));
        Assert.assertTrue(
                "fileFilterGetDescription(): fileFilterName not set ("
                        + allowedFileExtensions + ")", this.uploadPolicy
                        .fileFilterGetDescription().equals(fileFilterName));

        // If allowedFileExtensions is not provided, there should be no
        // fileFilterName
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setAllowedFileExtensions(null);
        Assert
                .assertNull(
                        "fileFilterGetDescription() when allowedFileExtensions is null",
                        this.uploadPolicy.fileFilterGetDescription());
        ((DefaultUploadPolicy) this.uploadPolicy).setAllowedFileExtensions("");
        Assert
                .assertNull(
                        "fileFilterGetDescription() when allowedFileExtensions is an empty String",
                        this.uploadPolicy.fileFilterGetDescription());
    }

    /** */
    @Test
    public void testGetContext() {
        Assert.assertEquals("getContext", this.juploadContext,
                this.uploadPolicy.getContext());
    }

    /** */
    @Test
    public void testSetDebutLevel() {
        int debugLevel = 47;
        this.uploadPolicy.setDebugLevel(debugLevel);
        Assert.assertEquals("debugLevel (" + debugLevel + ")", debugLevel,
                this.uploadPolicy.getDebugLevel());
    }

    /** */
    @Test
    public void testSetFileChooserIconFromFileContent() {
        int fileChooserIconFromFileContent = -1;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFileChooserIconFromFileContent(fileChooserIconFromFileContent);
        Assert.assertEquals("fileChooserIconFromFileContent",
                fileChooserIconFromFileContent, this.uploadPolicy
                        .getFileChooserIconFromFileContent());

        fileChooserIconFromFileContent = 0;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFileChooserIconFromFileContent(fileChooserIconFromFileContent);
        Assert.assertEquals("fileChooserIconFromFileContent",
                fileChooserIconFromFileContent, this.uploadPolicy
                        .getFileChooserIconFromFileContent());

        fileChooserIconFromFileContent = 1;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFileChooserIconFromFileContent(fileChooserIconFromFileContent);
        Assert.assertEquals("fileChooserIconFromFileContent",
                fileChooserIconFromFileContent, this.uploadPolicy
                        .getFileChooserIconFromFileContent());

        fileChooserIconFromFileContent = 2;
        try {
            // This should throw an IllegalValueException
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setFileChooserIconFromFileContent(fileChooserIconFromFileContent);
            Assert
                    .fail("fileChooserIconFromFileContent = 2   should throw an exception");
        } catch (java.lang.IllegalArgumentException e) {
            // We're happy if we go here !
        }
    }

    /** */
    @Test
    public void testSetFileChooserIconSize() {
        int fileChooserIconSize = 100;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFileChooserIconSize(fileChooserIconSize);
        Assert.assertEquals("fileChooserIconSize", fileChooserIconSize,
                this.uploadPolicy.getFileChooserIconSize());

        fileChooserIconSize = 0;
        try {
            // This should throw an IllegalValueException
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setFileChooserIconSize(fileChooserIconSize);
            Assert.fail("fileChooserIconSize <= 0   should throw an exception");
        } catch (java.lang.IllegalArgumentException e) {
            // We're happy if we go here !
        }
    }

    /** */
    @Test
    public void testSetLangNull() {
        String lang = null;
        ((DefaultUploadPolicy) this.uploadPolicy).setLang(lang);

        // The choosen translaton is available through the
        // UploadPolicy.getLocalizedString() method
        String languageSet = this.uploadPolicy.getLocalizedString("language");

        ((DefaultUploadPolicy) this.uploadPolicy).setLang(Locale.getDefault()
                .getLanguage());
        String languageDefault = this.uploadPolicy
                .getLocalizedString("language");
        Assert.assertEquals("lang=null => default", languageDefault,
                languageSet);
    }

    /** */
    @Test
    public void testSetLangFr() {
        String lang = "fr";
        ((DefaultUploadPolicy) this.uploadPolicy).setLang(lang);

        // The choosen translaton is available through the
        // UploadPolicy.getLocalizedString() method
        Assert.assertEquals("lang=fr", "French", this.uploadPolicy
                .getLocalizedString("language"));
    }

    /** */
    @Test
    public void testSetLookAndFeel() {
        String lookAndFeel = null;
        ((DefaultUploadPolicy) this.uploadPolicy).setLookAndFeel(lookAndFeel);
        // The default look and feel is the java one.
        Assert.assertEquals("lookAndFeel", "The Java(tm) Look and Feel",
                UIManager.getLookAndFeel().getDescription());

        lookAndFeel = "java";
        ((DefaultUploadPolicy) this.uploadPolicy).setLookAndFeel(lookAndFeel);
        Assert.assertEquals("lookAndFeel", lookAndFeel,
                ((DefaultUploadPolicy) this.uploadPolicy).getLookAndFeel());
        Assert.assertEquals("lookAndFeel", "The Java(tm) Look and Feel",
                UIManager.getLookAndFeel().getDescription());

        lookAndFeel = "system";
        ((DefaultUploadPolicy) this.uploadPolicy).setLookAndFeel(lookAndFeel);
        Assert.assertEquals("lookAndFeel", lookAndFeel,
                ((DefaultUploadPolicy) this.uploadPolicy).getLookAndFeel());
        if (System.getProperty("os.name").contains("Windows")) {
            Assert.assertEquals("lookAndFeel", "The Microsoft Windows Look and Feel",
                    UIManager.getLookAndFeel().getDescription());
        } else if (System.getProperty("os.name").contains("Mac")) {
            Assert.assertEquals("lookAndFeel", "Aqua Look and Feel for Mac OS X",
                    UIManager.getLookAndFeel().getDescription());
        } else {
            Assert.assertEquals("lookAndFeel", "The Java(tm) Look and Feel",
                    UIManager.getLookAndFeel().getDescription());
        }

        lookAndFeel = "a non existing look and feel";
        ((DefaultUploadPolicy) this.uploadPolicy).setLookAndFeel(lookAndFeel);
        Assert.assertEquals("lookAndFeel", "java",
                ((DefaultUploadPolicy) this.uploadPolicy).getLookAndFeel());
        Assert.assertEquals("lookAndFeel", "The Java(tm) Look and Feel",
                UIManager.getLookAndFeel().getDescription());
    }

    /** */
    @Test
    public void testSetMaxChunkSize() {
        long maxChunkSize = 0;
        ((DefaultUploadPolicy) this.uploadPolicy).setMaxChunkSize(maxChunkSize);
        Assert.assertEquals("maxChunkSize: forced to Long.MAX_VALUE when <=0",
                Long.MAX_VALUE, this.uploadPolicy.getMaxChunkSize());

        maxChunkSize = 1;
        ((DefaultUploadPolicy) this.uploadPolicy).setMaxChunkSize(maxChunkSize);
        Assert.assertEquals("maxChunkSize", maxChunkSize, this.uploadPolicy
                .getMaxChunkSize());
    }

    /** */
    @Test
    public void testSetMaxFileSize() {
        long maxFileSize = 0;
        ((DefaultUploadPolicy) this.uploadPolicy).setMaxFileSize(maxFileSize);
        Assert.assertEquals("maxFileSize: forced to Long.MAX_VALUE when <=0",
                Long.MAX_VALUE, this.uploadPolicy.getMaxFileSize());

        maxFileSize = 1;
        ((DefaultUploadPolicy) this.uploadPolicy).setMaxFileSize(maxFileSize);
        Assert.assertEquals("maxFileSize", maxFileSize, this.uploadPolicy
                .getMaxFileSize());
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testSetNbFilesPerRequest() throws JUploadException {
        int nbFilesPerRequest = 0;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setNbFilesPerRequest(nbFilesPerRequest);
        Assert.assertEquals(
                "nbFilesPerRequest: forced to Long.MAX_VALUE when <=0",
                Integer.MAX_VALUE, this.uploadPolicy.getNbFilesPerRequest());

        nbFilesPerRequest = 1;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setNbFilesPerRequest(nbFilesPerRequest);
        Assert.assertEquals("nbFilesPerRequest", nbFilesPerRequest,
                this.uploadPolicy.getNbFilesPerRequest());

        nbFilesPerRequest = 2;
        try {
            ((DefaultUploadPolicy) this.uploadPolicy).httpUploadParameterType = UploadPolicy.HTTPUPLOADPARAMETERTYPE_ONE_FILE;
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setNbFilesPerRequest(nbFilesPerRequest);
            Assert
                    .fail("nbFilesPerRequest=2 is not allowed with HTTPUPLOADPARAMETERTYPE_ONE_FILE");
        } catch (JUploadException e) {
            // Success! ;-)
        }
    }

    /**  */
    @Test
    public void testSetFilenameEncoding() {
        String filenameEncoding = "test";
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setFilenameEncoding(filenameEncoding);
            Assert.fail("The filenameEncoding '" + filenameEncoding
                    + "' should throw a UnsupportedCharsetException");
        } catch (UnsupportedCharsetException e) {
            // success !
        }

        filenameEncoding = "UTF-8";
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFilenameEncoding(filenameEncoding);
        Assert.assertEquals("filenameEncoding", filenameEncoding,
                this.uploadPolicy.getFilenameEncoding());
    }

    /**  */
    @Test
    public void testSetFtpCreateDirectoryStructure() {
        boolean ftpCreateDirectoryStructure = true;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFtpCreateDirectoryStructure(ftpCreateDirectoryStructure);
        Assert.assertEquals("ftpCreateDirectoryStructure",
                ftpCreateDirectoryStructure, this.uploadPolicy
                        .getFtpCreateDirectoryStructure());

        ftpCreateDirectoryStructure = false;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFtpCreateDirectoryStructure(ftpCreateDirectoryStructure);
        Assert.assertEquals("ftpCreateDirectoryStructure",
                ftpCreateDirectoryStructure, this.uploadPolicy
                        .getFtpCreateDirectoryStructure());
    }

    /**  */
    @Test
    public void testSetFtpTransfertBinary() {
        boolean ftpTransfertBinary = true;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFtpTransfertBinary(ftpTransfertBinary);
        Assert.assertEquals("ftpTransfertBinary", ftpTransfertBinary,
                this.uploadPolicy.getFtpTransfertBinary());

        ftpTransfertBinary = false;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFtpTransfertBinary(ftpTransfertBinary);
        Assert.assertEquals("ftpTransfertBinary", ftpTransfertBinary,
                this.uploadPolicy.getFtpTransfertBinary());
    }

    /**  */
    @Test
    public void testSetFtpTransfertPassive() {
        boolean ftpTransfertPassive = true;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFtpTransfertPassive(ftpTransfertPassive);
        Assert.assertEquals("ftpTransfertPassive", ftpTransfertPassive,
                this.uploadPolicy.getFtpTransfertPassive());

        ftpTransfertPassive = false;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setFtpTransfertPassive(ftpTransfertPassive);
        Assert.assertEquals("ftpTransfertPassive", ftpTransfertPassive,
                this.uploadPolicy.getFtpTransfertPassive());
    }

    /**  */
    @Test
    public void testSetReadCookieFromNavigator() {
        boolean readCookieFromNavigator = true;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setReadCookieFromNavigator(readCookieFromNavigator);
        Assert.assertEquals("readCookieFromNavigator", readCookieFromNavigator,
                this.uploadPolicy.getReadCookieFromNavigator());

        readCookieFromNavigator = false;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setReadCookieFromNavigator(readCookieFromNavigator);
        Assert.assertEquals("readCookieFromNavigator", readCookieFromNavigator,
                this.uploadPolicy.getReadCookieFromNavigator());
    }

    /**  */
    @Test
    public void testSetReadUserAgentFromNavigator() {
        boolean readCookieFromNavigator = true;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setReadUserAgentFromNavigator(readCookieFromNavigator);
        Assert.assertEquals("readCookieFromNavigator", readCookieFromNavigator,
                this.uploadPolicy.getReadUserAgentFromNavigator());

        readCookieFromNavigator = false;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setReadUserAgentFromNavigator(readCookieFromNavigator);
        Assert.assertEquals("readCookieFromNavigator", readCookieFromNavigator,
                this.uploadPolicy.getReadUserAgentFromNavigator());
    }

    /** */
    @Test
    public void testSetRetryMaxNumberOf() {
        int retryMaxNumberOf = 0;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setRetryMaxNumberOf(retryMaxNumberOf);
        Assert.assertEquals("RetryMaxNumberOf", retryMaxNumberOf,
                this.uploadPolicy.getRetryMaxNumberOf());

        retryMaxNumberOf = 1;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setRetryMaxNumberOf(retryMaxNumberOf);
        Assert.assertEquals("RetryMaxNumberOf", retryMaxNumberOf,
                this.uploadPolicy.getRetryMaxNumberOf());

        retryMaxNumberOf = -1;
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setRetryMaxNumberOf(retryMaxNumberOf);
            Assert
                    .fail("With retryMaxNumberOf set to -1, we should have an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Success !
        }
    }

    /** */
    @Test
    public void testSetRetryNbSecondsBetween() {
        int retryNbSecondsBetween = 0;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setRetryNbSecondsBetween(retryNbSecondsBetween);
        Assert.assertEquals("RetryNbSecondsBetween", retryNbSecondsBetween,
                this.uploadPolicy.getRetryNbSecondsBetween());

        retryNbSecondsBetween = 1;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setRetryNbSecondsBetween(retryNbSecondsBetween);
        Assert.assertEquals("RetryNbSecondsBetween", retryNbSecondsBetween,
                this.uploadPolicy.getRetryNbSecondsBetween());

        retryNbSecondsBetween = -1;
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setRetryNbSecondsBetween(retryNbSecondsBetween);
            Assert
                    .fail("With retryNbSecondsBetween set to -1, we should have an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Success !
        }
    }

    /** */
    @Test
    public void testSetServerProtocol() {
        String serverProtocol = "HTTP/1.2"; // It's a new one : ;-)
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setServerProtocol(serverProtocol);
        Assert.assertEquals("ServerProtocol", serverProtocol, this.uploadPolicy
                .getServerProtocol());
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testCheckUploadSuccess_failure() throws JUploadException {
        int status = 59;
        String msg = "Hum, hum";
        String body = "We're in the case\r\n of an unknown failure!\r\n";

        try {
            this.uploadPolicy.checkUploadSuccess(status, msg, body);
            Assert.fail("Status value " + status
                    + " should raise a JUploadExceptionUploadFailed");
        } catch (JUploadExceptionUploadFailed e1) {
            // Success!
        }

        // Let's check a response without error or success string
        // Special case (always success)
        status = 100;
        try {
            Assert.assertTrue(this.uploadPolicy.checkUploadSuccess(status, msg,
                    body));
        } catch (JUploadExceptionUploadFailed e1) {
            Assert.fail("Expected always success for http status 100");
        }

        // Let's check a response without error or success string
        status = 200;
        try {
            this.uploadPolicy.checkUploadSuccess(status, msg, body);
            Assert
                    .fail("Response without success nor error string should raise a JUploadExceptionUploadFailed");
        } catch (JUploadExceptionUploadFailed e1) {
            // Success!
        }

        // Let's check a response without error or success string
        String errMsg = "this is an error!";
        body = "We're in the case\r\n of a known failure!\r\nERROR: " + errMsg
                + "\r\nbla bla bla";
        try {
            this.uploadPolicy.checkUploadSuccess(status, msg, body);
            Assert
                    .fail("Response with an error message should raise a JUploadExceptionUploadFailed");
        } catch (JUploadExceptionUploadFailed e1) {
            // Success!
            Assert.assertEquals("Check of the error message retrieval", errMsg,
                    e1.getMessage());
        }

        // Let's change the stringUploadError value
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setStringUploadError("^OUPS: (.*)$");
        body = "We're in the case\r\n of a known failure!\r\nOUPS: " + errMsg
                + "\r\nbla bla bla";
        try {
            this.uploadPolicy.checkUploadSuccess(status, msg, body);
            Assert
                    .fail("Response with an error message should raise a JUploadExceptionUploadFailed");
        } catch (JUploadExceptionUploadFailed e1) {
            // Success!
            Assert.assertEquals("Check of the error message retrieval", errMsg,
                    e1.getMessage());
        }
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testCheckUploadSuccess_warning() throws JUploadException {
        int status = 200;
        String msg = "Hum, hum";
        String warnMsg = "this is an warning!";
        String body = "We're in the case\r\n of success then warning!\r\nSUCCESS\r\nWARNING: "
                + warnMsg + "\r\nbla bla bla";
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setStringUploadWarning(UploadPolicy.DEFAULT_STRING_UPLOAD_WARNING);
        Assert.assertTrue("The upload should be a success", this.uploadPolicy
                .checkUploadSuccess(status, msg, body));
        verify(this.uploadPolicy).displayWarn(warnMsg);
        /*
         * Assert .assertEquals( "Check of the warning message retrieval",
         * warnMsg, ((DefaultUploadPolicy_WithoutAlertBox)
         * this.uploadPolicy).lastAlertMessage);
         */

        // Let's change the stringUploadError value
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setStringUploadWarning("OUPS: (.*)$");
        warnMsg = "this is another warning!";
        body = "We're in the case\r\n of warning then success!\r\nOUPS: "
                + warnMsg + "\r\nbla bla bla\r\nSUCCESS";
        Assert.assertTrue("The upload should be a success", this.uploadPolicy
                .checkUploadSuccess(status, msg, body));
        verify(this.uploadPolicy).displayWarn(warnMsg);
        /*
         * Assert .assertEquals( "Check of the warning message retrieval",
         * warnMsg, ((DefaultUploadPolicy_WithoutAlertBox)
         * this.uploadPolicy).lastAlertMessage);
         */
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testCheckUploadSuccess_success() throws JUploadException {
        int status = 100;
        String msg = "Hum, hum";
        String body = "We're in the case\r\n of a complete success!\r\nSUCCESS\r\nbla bla bla";

        Assert.assertTrue("The upload should be a success", this.uploadPolicy
                .checkUploadSuccess(status, msg, body));
        verify(this.uploadPolicy, never()).displayWarn(anyString());
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testAfterUpload() throws JUploadException {

        // A specific class, to check the call of displayURL.
        class JUploadContextCheckDisplayURL extends JUploadContextTestHelper {

            boolean displayURLHasBeenCalled = false;

            public JUploadContextCheckDisplayURL(UploadPolicy uploadPolicy,
                    JUploadPanel juploadPanel) {
                super(uploadPolicy, juploadPanel);
            }

            public void displayURL(String url, boolean success) {
                this.displayURLHasBeenCalled = true;
            }
        }

        // Let's use our specific class.
        this.juploadContext = new JUploadContextCheckDisplayURL(
                this.uploadPolicy, this.juploadPanel);
        ((DefaultUploadPolicy) this.uploadPolicy).juploadContext = this.juploadContext;

        String serverOutput = "Some output";
        Exception e = null;
        ((DefaultUploadPolicy) this.uploadPolicy).setAfterUploadURL(null);
        this.uploadPolicy.afterUpload(e, serverOutput);
        Assert
                .assertFalse(
                        "setAfterUploadURL should not have been called",
                        ((JUploadContextCheckDisplayURL) this.juploadContext).displayURLHasBeenCalled);

        String url = "An URL";
        ((DefaultUploadPolicy) this.uploadPolicy).setAfterUploadURL(url);
        this.uploadPolicy.afterUpload(e, serverOutput);
        Assert
                .assertTrue(
                        "setAfterUploadURL should  have been called",
                        ((JUploadContextCheckDisplayURL) this.juploadContext).displayURLHasBeenCalled);
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testCreateFileData() throws JUploadException {
        doReturn(JOptionPane.CANCEL_OPTION).when(this.uploadPolicy).confirmDialogStr(anyString(), anyInt());
        doReturn(false).when(this.uploadPolicy).fileFilterAccept((File)anyObject());
        File file = new File("/aTestFile");
        File root = new File("/");
        try {
            this.uploadPolicy.createFileData(file, root);
            Assert.fail("A JUploadExceptionStopAddingFiles should have been thrown");
        } catch (JUploadExceptionStopAddingFiles e) {
            // We're happy here ! Test is a success
        }

        // Not allowed file. The user may chooses Ok to the confirmDialog
        doReturn(JOptionPane.OK_OPTION).when(this.uploadPolicy).confirmDialogStr(anyString(), anyInt());
        Assert
                .assertNull(
                        "There should be no creation of file data, if the file is not accepted",
                        this.uploadPolicy.createFileData(file, root));

        //((DefaultUploadPolicy_WithoutAlertBox_FileFilterAccept) this.uploadPolicy).acceptFile = true;
        doReturn(true).when(this.uploadPolicy).fileFilterAccept((File)anyObject());
        Assert
                .assertNull(
                        "There should be a creation of file data, if the file is accepted",
                        this.uploadPolicy.createFileData(file, root));
    }

    /**
     * @throws JUploadException
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testGetEncodedFilename() throws JUploadException,
            UnsupportedEncodingException {
        String encoding = null;
        String filename = "A_file_name_with_accents_\u00f8\u00e5";
        ((DefaultUploadPolicy) this.uploadPolicy).setFilenameEncoding(encoding);
        Assert.assertEquals("No encoding for encoding=" + encoding, filename,
                ((DefaultUploadPolicy) this.uploadPolicy)
                        .getEncodedFilename(filename));

        encoding = "";
        ((DefaultUploadPolicy) this.uploadPolicy).setFilenameEncoding(encoding);
        Assert.assertEquals("No encoding for encoding=" + encoding, filename,
                ((DefaultUploadPolicy) this.uploadPolicy)
                        .getEncodedFilename(filename));

        encoding = "UTF-8";
        ((DefaultUploadPolicy) this.uploadPolicy).setFilenameEncoding(encoding);
        Assert.assertEquals("Encoding for encoding=" + encoding, URLEncoder
                .encode(filename, encoding),
                ((DefaultUploadPolicy) this.uploadPolicy)
                        .getEncodedFilename(filename));
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testGetUploadName() throws JUploadException {
        int fileNumber = 59;
        FileDataTestHelper fileData = new FileDataTestHelper(fileNumber);
        String uploadParameterName = "aName";
        int index = 8;
        int nbFilesPerRequest = 4;

        // Let's check that it works with a forced value (different from the
        // default one)
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterName(uploadParameterName);
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setNbFilesPerRequest(nbFilesPerRequest);

        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterType(UploadPolicy.HTTPUPLOADPARAMETERTYPE_ARRAY);
        Assert.assertEquals("Array mode", uploadParameterName + "[]",
                this.uploadPolicy.getUploadName(fileData, index));

        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterType(UploadPolicy.HTTPUPLOADPARAMETERTYPE_ITERATION);
        Assert.assertEquals("Array mode", uploadParameterName + index,
                this.uploadPolicy.getUploadName(fileData, index));

        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setHttpUploadParameterType(UploadPolicy.HTTPUPLOADPARAMETERTYPE_ONE_FILE);
            this.uploadPolicy.getUploadName(fileData, index);
            Assert.fail(UploadPolicy.HTTPUPLOADPARAMETERTYPE_ONE_FILE
                    + " is not compatible with nbFilesPerRequest="
                    + nbFilesPerRequest);
        } catch (JUploadException e) {
            // Success !
        }

        nbFilesPerRequest = 1;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setNbFilesPerRequest(nbFilesPerRequest);
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterType(UploadPolicy.HTTPUPLOADPARAMETERTYPE_ONE_FILE);
        Assert.assertEquals("One file mode", uploadParameterName,
                this.uploadPolicy.getUploadName(fileData, index));
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testSetHttpUploadParameterName() throws JUploadException {
        String httpUploadParameterName = "AName";
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterName(httpUploadParameterName);
        Assert.assertEquals("Standard name", httpUploadParameterName,
                this.uploadPolicy.getHttpUploadParameterName());

        httpUploadParameterName = null;
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setHttpUploadParameterName(httpUploadParameterName);
            Assert
                    .fail("null is not a valid value for httpUploadParameterName");
        } catch (JUploadException e) {
            // Success !
        }

        httpUploadParameterName = "";
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setHttpUploadParameterName(httpUploadParameterName);
            Assert
                    .fail("\"\" is not a valid value for httpUploadParameterName");
        } catch (JUploadException e) {
            // Success !
        }

        httpUploadParameterName = "\u00f8\u00e5#";
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setHttpUploadParameterName(httpUploadParameterName);
            Assert.fail(httpUploadParameterName
                    + " is not a valid value for httpUploadParameterName");
        } catch (JUploadException e) {
            // Success !
        }
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testSetHttpUploadParameterType() throws JUploadException {
        String httpUploadParameterType = "Not a valid one!";
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setHttpUploadParameterType(httpUploadParameterType);
            Assert.fail(httpUploadParameterType
                    + " is not a valid one for httpUploadParameterType");
        } catch (JUploadException e) {
            // Success!
        }

        httpUploadParameterType = null;
        try {
            ((DefaultUploadPolicy) this.uploadPolicy)
                    .setHttpUploadParameterType(httpUploadParameterType);
            Assert.fail("null is not a valid one for httpUploadParameterType");
        } catch (JUploadException e) {
            // Success!
        }

        httpUploadParameterType = UploadPolicy.HTTPUPLOADPARAMETERTYPE_ARRAY;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterType(httpUploadParameterType);
        Assert.assertEquals(
                "null is not a valid value for httpUploadParameterType",
                httpUploadParameterType, this.uploadPolicy
                        .getHttpUploadParameterType());

        httpUploadParameterType = UploadPolicy.HTTPUPLOADPARAMETERTYPE_ITERATION;
        ((DefaultUploadPolicy) this.uploadPolicy)
                .setHttpUploadParameterType(httpUploadParameterType);
        Assert.assertEquals(
                "null is not a valid value for httpUploadParameterType",
                httpUploadParameterType, this.uploadPolicy
                        .getHttpUploadParameterType());
    }
}
