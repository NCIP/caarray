//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 27 janv. 2010
// Creator: etienne_sf
// Last modified: $Date$
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.upload;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import wjhk.jupload2.JUploadDaemon;
import wjhk.jupload2.context.JUploadContext;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.filepanel.FilePanel;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.testhelpers.FileDataTestHelper;
import wjhk.jupload2.testhelpers.FilePanelTestHelper;
import wjhk.jupload2.testhelpers.FileUploadManagerThreadTestHelper;
import wjhk.jupload2.testhelpers.FileUploadThreadTestHelper;
import wjhk.jupload2.testhelpers.JUploadContextTestHelper;
import wjhk.jupload2.testhelpers.JUploadPanelTestHelper;
import wjhk.jupload2.testhelpers.UploadPolicyTestHelper;

/**
 * This class is the superclass of all test classes in this package. It creates
 * all common objects (upload policy, queues...)
 * 
 * @author etienne_sf
 */
public class AbstractJUploadTestHelper {
    /** Logger for this class */
    protected final Logger logger = Logger
            .getLogger(AbstractJUploadTestHelper.class);

    final static String DEFAULT_LOCAL_POST_URL = "http://localhost/index.html";

    final static String DEFAULT_POST_URL = "http://jupload.sourceforge.net/upload_dummy.html";

    /** Maximum time to wait for a thread to finish its work, in milliseconds */
    final static int MAX_WAIT_FOR_THREAD = 10000;

    /**
     * Indicates whether the postURL system property has been set for the
     * current unit test execution.
     */
    public static boolean postURLHasBeenSet = false;

    /** Interval of time, before checking if a thread has finished its work */
    final static int INTERVAL_BEFORE_CHECKING_THREAD = 1000;

    /** A default {@link JUploadDaemon} */
    public JUploadDaemon juploadDaemon;

    /** A default {@link JUploadContext} */
    public JUploadContext juploadContext = null;

    /** A default {@link FilePanel} */
    public FilePanel filePanel;

    /** A default {@link JUploadPanel} */
    public JUploadPanel juploadPanel;

    /** A default {@link UploadPolicy} */
    public UploadPolicy uploadPolicy = null;

    /** The root for the file to upload */
    public File fileroot = null;

    /**
     * The list of files that will be loaded. Initialized in
     * {@link #setupFileList(int)}.
     */
    public List<FileData> filesToUpload = null;

    /** A default {@link FilePreparationThread} */
    public FilePreparationThread filePreparationThread = null;

    /** A default {@link PacketConstructionThread} */
    public PacketConstructionThread packetConstructionThread = null;

    /** A default {@link FileUploadThread} */
    public FileUploadThread fileUploadThread = null;

    /** A default {@link FileUploadManagerThread} */
    public FileUploadManagerThread fileUploadManagerThread = null;

    /** The actual start of this test */
    public long uploadStartTime = -1;

    /** A default queue, for the prepared files */
    public BlockingQueue<UploadFileData> preparedFileQueue = new ArrayBlockingQueue<UploadFileData>(
            100);

    /** A default queue, for the packets to upload */
    public BlockingQueue<UploadFilePacket> packetQueue = new ArrayBlockingQueue<UploadFilePacket>(
            100);

    /**
     * Constructs the UploadPolicy, and all threads to simulate a real upload.
     * Maybe too long for real unit testing.
     * 
     * @throws Exception
     */
    @Before
    public void setupFullUploadEnvironment() throws Exception {
        // Set the postURL for the current unit test, according to the local
        // network access.
        setPostURL();

        this.juploadDaemon = new JUploadDaemon();
        this.filePanel = new FilePanelTestHelper(this.filesToUpload);
        this.juploadPanel = new JUploadPanelTestHelper(this.filePanel);
        this.uploadPolicy = new UploadPolicyTestHelper(this.juploadPanel);

        this.juploadContext = this.uploadPolicy.getContext();

        this.fileUploadThread = new FileUploadThreadTestHelper(this.packetQueue);
        this.fileUploadManagerThread = new FileUploadManagerThreadTestHelper();
        ((JUploadPanelTestHelper) this.juploadPanel).fileUploadManagerThread = this.fileUploadManagerThread;

        // Set up the file data, for the simulated upload.
        this.fileroot = new File(JUploadContextTestHelper.TEST_FILES_FOLDER);

        setupFileList(1);

        // Let's note the current system time. It should be almost the upload
        // start time.
        this.uploadStartTime = System.currentTimeMillis();
    }

    /**
     * This method tries to determine if the current computer can access to
     * jupload.sourceforge.net. If yes, the
     * http://jupload.sourceforge.net/upload_dummy.html URL is used. <BR>
     * If no, the http://localhost/index.html URL is used for test postURL.<BR>
     * <BR>
     * The reason for this test, is that I often work unconnected, in the train.
     * And I still want the unit tests to work properly.
     */
    private void setPostURL() {
        // If the system property has not been set yet, let's determine if
        // we're connected to the network.
        if (!postURLHasBeenSet) {
            String postURL;
            try {
                URL url = new URL(DEFAULT_POST_URL);
                new Socket(url.getHost(), url.getPort());
                // The given host is valid. We use this URL.
                postURL = DEFAULT_POST_URL;
            } catch (Exception e) {
                logger.warn(e.getClass().getName() + " when creating URL from "
                        + DEFAULT_POST_URL + " (will use "
                        + DEFAULT_LOCAL_POST_URL + " instead");
                //
                postURL = DEFAULT_LOCAL_POST_URL;
            }

            System.setProperty(UploadPolicy.PROP_POST_URL, postURL);
            postURLHasBeenSet = true;
        }
    }

    /**
     * @param nbFiles
     */
    public void setupFileList(int nbFiles) {

        this.filesToUpload = new ArrayList<FileData>(nbFiles);
        File[] fArray = new File[nbFiles];

        for (int i = 0; i < nbFiles; i += 1) {
            FileData fileData = new FileDataTestHelper(i);
            // We must be able to load the file. Otherwise, it's useless to
            // start. And there seems to be problem with user dir, depending on
            // the java tool used.
            Assert.assertTrue(fileData.getFileName() + " must be readable !",
                    fileData.canRead());
            this.filesToUpload.add(fileData);
        }

        // Let's add these files to the FilePanel
        if (this.filePanel instanceof FilePanelTestHelper) {
            ((FilePanelTestHelper) this.filePanel).filesToUpload = this.filesToUpload;
        } else {
            // We first clear the list, to be sure of the final content.
            this.filePanel.removeAll();
            this.filePanel.addFiles(fArray, null);
        }
    }

    /**
     * Call the beforeUpload method for all files.
     * 
     * @throws JUploadException
     */
    void prepareFileList() throws JUploadException {
        for (FileData fileData : this.filesToUpload) {
            fileData.beforeUpload();
        }
    }

    /**
     * Wait for a queue to be emptied by a consuming thread. This will wait
     * {@link #MAX_WAIT_FOR_THREAD} at most, and check this every
     * {@link INTERVAL_BEFORE_CHECKING_THREAD} ms.
     */
    @SuppressWarnings("unchecked")
    void waitForQueueToBeEmpty(Queue queue, String queueName) {
        int nbLoop = MAX_WAIT_FOR_THREAD / INTERVAL_BEFORE_CHECKING_THREAD;
        try {
            for (int i = 0; i < nbLoop; i += 1) {
                if (queue.isEmpty()) {
                    logger.info("The queue " + queueName + " has been emptied");
                    return;
                }
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.warn("waitForQueueToBeEmpty got interrupted");
        }
        logger.warn("The queue " + queueName + " was not emptied");
    }

    /**
     * Wait for a thread to finish normally. This will wait
     * {@link #MAX_WAIT_FOR_THREAD} at most, and check this every
     * {@link INTERVAL_BEFORE_CHECKING_THREAD} ms.
     */
    void waitForThreadToFinish(Thread thread, String threadName) {
        if (thread.isAlive()) {
            // Let's wait a little for this thread to finish...
            try {
                (thread).join(MAX_WAIT_FOR_THREAD);
            } catch (InterruptedException e) {
                Assert.fail("Was interrupted during the join: the thread "
                        + threadName + " did not finish on time ");
            }
        }

        // Is the thread finished now ?
        if (thread.isAlive()) {
            logger.warn("The thread " + threadName
                    + " did not finished alone: let's interrupt it");
            thread.interrupt();
        } else {
            logger.info("The thread " + threadName
                    + " finished alone (no interruption needed)");
        }
    }

    /** */
    @After
    public void cleanQueues() {
        logger
                .debug("Finishing the test: interrupting the threads, and cleaning the queues");

        if (this.fileUploadThread != null) {
            if (this.fileUploadThread.isAlive()) {
                this.fileUploadThread.interrupt();
                this.fileUploadThread = null;
            }
        }
        if (this.fileUploadManagerThread != null) {
            if (this.fileUploadManagerThread.isAlive()) {
                this.fileUploadManagerThread.interrupt();
                this.fileUploadManagerThread = null;
            }
        }
        if (this.packetConstructionThread != null) {
            if (this.packetConstructionThread.isAlive()) {
                this.packetConstructionThread.interrupt();
                this.packetConstructionThread = null;
            }
        }

        if (this.preparedFileQueue != null) {
            while (!this.preparedFileQueue.isEmpty()) {
                this.preparedFileQueue.poll();
            }
            this.preparedFileQueue = null;
        }

        if (this.packetQueue != null) {
            while (!this.packetQueue.isEmpty()) {
                this.packetQueue.poll();
            }
            this.packetQueue = null;
        }
    }

    /**
     * This method calls a given method onto a given object. It is used to
     * execute the call of the callback registered by the last call to
     * juploadContext.registerUnload.<BR>
     * This works only if juploadContext is an instance of
     * {@link JUploadContextTestHelper}
     * 
     * @param expectedClass Contains the class to which the
     *            lastRegisterUnloadObject should belong to.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @SuppressWarnings("unchecked")
    public void testLastRegisteredUnload(Class expectedClass)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        Assert
                .assertTrue(
                        "juploadContext must be an instance of JUploadContextTestHelper",
                        this.juploadContext instanceof JUploadContextTestHelper);
        Assert
                .assertTrue(
                        "The lastRegisterUnloadObject should be an instance of "
                                + expectedClass,
                        expectedClass
                                .isInstance(((JUploadContextTestHelper) this.juploadContext).lastRegisterUnloadObject));
        testUnload(
                ((JUploadContextTestHelper) this.juploadContext).lastRegisterUnloadObject,
                ((JUploadContextTestHelper) this.juploadContext).lastRegisterUnloadMethod);
    }

    /**
     * This method calls a given method onto a given object.
     * 
     * @param object
     * @param methodName
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public void testUnload(Object object, String methodName)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        // Let's find and call this method. This method must have no argument.
        Method method = object.getClass().getMethod(methodName);
        Assert.assertNotNull("The method '" + methodName
                + "' must exist (and have no argument)", method);
        method.invoke(object);
    }

    /**
     * This static class computes create a file instance for the given relative
     * path.<BR>
     * This method is taken from <A HREF=
     * "http://kozelka.net/blog/generating-temporary-files-in-junit-tests">Petr
     * Kozelka's blog</A>
     * 
     * @param relativeFilePath The path of the file, starting from
     *            src/test/resources.
     * @return The File for the file, whose relative path to tests folder is
     *         given in argument. This method is compatible with whatever tool
     *         is used to execute the tests: it can be from maven, from eclipse,
     *         or from any other tool.
     */
    public static File getTestFile(String relativeFilePath) {
        final String clsUri = AbstractJUploadTestHelper.class.getName()
                .replace('.', File.separatorChar)
                + ".class";
        final URL url = AbstractJUploadTestHelper.class.getClassLoader()
                .getResource(clsUri);
        final String clsPath = url.getPath().replaceAll("%20", " ").replaceAll(
                "%5c", ".");
        final File root = new File(clsPath.substring(0, clsPath.length()
                - clsUri.length()));
        return new File(root, relativeFilePath);
    }

    /**
     * This method returns the absolute path for the given file. It is based on
     * the {@link #getTestFile(String)} method.
     * 
     * @param relativeFilePath The path of the file, starting from
     *            src/test/resources.
     * @return The absolute path for the given file.
     */
    public static String getTestFilePath(String relativeFilePath) {
        return getTestFile(relativeFilePath).getAbsolutePath();
    }

    /**
     * Get the root for the tests files, whatever is the current tool used to
     * run JUnit test: maven, eclipse, any IDE...
     * 
     * @return The absolute path for the test files root, ending with the file
     *         separator character.
     */
    public static String getTestFilesRootPath() {
        return getTestFilePath("files") + File.separator;
    }

}
