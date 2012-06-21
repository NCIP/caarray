//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 25 fevr. 2010
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
package wjhk.jupload2.filedata;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.policies.PictureUploadPolicy;
import wjhk.jupload2.testhelpers.JUploadContextTestHelper;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;

/**
 * @author etienne_sf
 */
public class PictureFileDataTest extends AbstractJUploadTestHelper {

    // The test file root is ./src/test/resources/files
    private String myFileRoot = AbstractJUploadTestHelper
            .getTestFilesRootPath();

    private static String DEFAULT_FILENAME = "test.bmp";

    private PictureFileData pictureFileData;

    private File file;

    private File root;

    /**
     * @throws JUploadException
     */
    @Before
    public void setUp() throws JUploadException {
        setUp(DEFAULT_FILENAME);
    }

    /**
     * Let's construct a test case, for the given filename.
     * 
     * @param filename
     * @throws JUploadException
     */
    private void setUp(String filename) throws JUploadException {
        this.file = new File(this.myFileRoot + filename);
        this.root = new File(this.myFileRoot);

        System.setProperty("pictureTransmitMetadata", "true");
        this.uploadPolicy = new PictureUploadPolicy(this.juploadContext);
        ((JUploadContextTestHelper) this.juploadContext).uploadPolicy = this.uploadPolicy;
        this.pictureFileData = new PictureFileData(this.file, this.root,
                (PictureUploadPolicy) this.uploadPolicy);
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testConstructor() throws JUploadException {
        // Default filename: test.bmp
        Assert.assertTrue("We have a picture, here (attribute)",
                this.pictureFileData.isPicture);
        Assert.assertTrue("We have a picture, here (getter)",
                this.pictureFileData.isPicture());

        Assert.assertEquals("It's a gif, here (mime-type)", "image/bmp",
                this.pictureFileData.getMimeType());

        // Test with a non-picture file
        setUp("level1/ATestFile.txt");
        Assert.assertFalse("We have a non-picturefile , here (attribute)",
                this.pictureFileData.isPicture);
        Assert.assertFalse("We have a non-picturefile , here (attribute)",
                this.pictureFileData.isPicture());

        Assert.assertEquals("It's a text, here (mime-type)",
                JUploadContextTestHelper.TEST_CASE_MIME_TYPE,
                this.pictureFileData.getMimeType());
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testBeforeUpload_noTransformation() throws JUploadException {
        this.pictureFileData.beforeUpload();
        Assert.assertTrue("The file is now prepared",
                this.pictureFileData.preparedForUpload);

        Assert.assertEquals("Non transformed file (length - attribute)",
                this.file.length(), this.pictureFileData.uploadLength);
        Assert.assertEquals("Non transformed file (length - getter)", this.file
                .length(), this.pictureFileData.getUploadLength());
        Assert.assertNull("Non transformed file (no transformed file)",
                this.pictureFileData.transformedPictureFile);
        Assert.assertNull("Non transformed file (working copy file)",
                this.pictureFileData.workingCopyTempFile);
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testBeforeUpload_oneRotation() throws JUploadException {
        this.pictureFileData.quarterRotation = 1;
        this.pictureFileData.beforeUpload();
        Assert.assertTrue("The file is now prepared",
                this.pictureFileData.preparedForUpload);

        Assert.assertNotNull(
                "File is transformed (there must exist one transformed file)",
                this.pictureFileData.transformedPictureFile);
        Assert.assertNotNull(
                "File is transformed (there must exist one working copy file)",
                this.pictureFileData.workingCopyTempFile);
        // Let's remove the temporary files.
        this.pictureFileData.afterUpload();
    }

    /** */
    @Test
    public void testGetUploadLength() {
        // Already tested in beforeUpload. We just check here the response, when
        // the file is not prepared.
        Assert.assertFalse("By default, the file is not prepared",
                this.pictureFileData.preparedForUpload);
        try {
            this.pictureFileData.getUploadLength();
            Assert
                    .fail("getUploadLength() on non prepared file should raise an exception");
        } catch (java.lang.IllegalStateException e) {
            // Success!
        }
    }

    /**
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testGetInputStream_nonPreparedFile() throws JUploadException,
            IOException {
        try {
            this.pictureFileData.getInputStream();
            Assert
                    .fail("getInputStream() should raise an exception when called on a non prepared file");
        } catch (java.lang.IllegalStateException e) {
            // Success!
        }
    }

    /**
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testGetInputStream_nonTransformedFile()
            throws JUploadException, IOException {
        this.pictureFileData.beforeUpload();
        InputStream isTest = this.pictureFileData.getInputStream();
        InputStream isAssert = new FileInputStream(this.file);
        int charAssert;
        int charTest;
        while ((charAssert = isAssert.read()) >= 0) {
            charTest = isTest.read();
            if (charTest < 0) {
                isTest.close();
                isAssert.close();
                Assert
                        .fail("Can't read one byte for the pictureFileData input stream");
            }
            Assert.assertEquals("File content should be identical", charAssert,
                    charTest);
        }
        charTest = isTest.read();
        if (charTest >= 0) {
            isTest.close();
            isAssert.close();
            Assert.fail("The pictureFileData input stream should be finished");
        }
        isTest.close();
        isAssert.close();
    }

    /**
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testGetInputStream_RotatedFile() throws JUploadException,
            IOException {
        this.pictureFileData.quarterRotation = 1;
        this.pictureFileData.beforeUpload();
        InputStream isTest = this.pictureFileData.getInputStream();
        InputStream isAssert = new FileInputStream(this.file);
        int charAssert;
        int charTest;
        while ((charAssert = isAssert.read()) >= 0) {
            charTest = isTest.read();
            if (charTest < 0) {
                // There is a difference between the two files: the file has
                // been transformed. We can't really test more here, so let's
                // stop here.
                isTest.close();
                isAssert.close();
                return;
            }
            if (charTest != charAssert) {
                // There is a difference between the two files: the file has
                // been transformed. We can't really test more here, so let's
                // stop here.
                isTest.close();
                isAssert.close();
                return;
            }
        }// while
        charTest = isTest.read();
        if (charTest >= 0) {
            // There is a difference between the two files: the file has
            // been transformed. We can't really test more here, so let's
            // stop here.
            isTest.close();
            isAssert.close();
            return;
        }
        isTest.close();
        isAssert.close();

        Assert
                .fail("The files are identifical (although the pictureFileData should have transformed it !");
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testAfterUpload() throws JUploadException {
        // We need a transformed picture file.
        this.pictureFileData.quarterRotation = 1;
        this.pictureFileData.beforeUpload();
        Assert.assertNotNull("The transformed file should exist",
                this.pictureFileData.transformedPictureFile);
        Assert.assertNotNull("The transformed file should exist",
                this.pictureFileData.workingCopyTempFile);
        this.pictureFileData.afterUpload();
        Assert.assertNull("The transformed file should now be deleted",
                this.pictureFileData.transformedPictureFile);
        Assert.assertNull("The transformed file should now be deleted",
                this.pictureFileData.workingCopyTempFile);
    }

    /**
     * @throws JUploadException
     */
    @Test
    public void testGetImage() throws JUploadException {
        try {
            this.pictureFileData.getImage(null, false);
            Assert.fail("Canvas null should not be accepted");
        } catch (JUploadException e) {
            // Success !
        }

        int width = 10;
        int height = 10;
        Canvas canvas = new Canvas();
        canvas.setSize(width, height);
        Image image = this.pictureFileData.getImage(canvas, false);
        Assert.assertNull(
                "No cache here: the offscreenImage should remain null",
                this.pictureFileData.offscreenImage);
        // The orginal picture is a square: all dimension must be the same as
        // the canvas's ones.
        Assert
                .assertEquals("Width resizing check", width, image
                        .getWidth(null));
        Assert.assertEquals("Height resizing check", height, image
                .getHeight(null));

        // Check shadow management.
        image = this.pictureFileData.getImage(canvas, true);
        Assert.assertEquals("The returned image must be the one stored", image,
                this.pictureFileData.offscreenImage);
        Assert
                .assertEquals(
                        "The newly returned image must still be the one stored (real image)",
                        image, this.pictureFileData.getImage(canvas, true));
    }

    /**
     * @throws IOException
     */
    @Test
    public void testAddRotation() throws IOException {
        this.pictureFileData.offscreenImage = ImageIO.read(this.file);
        Assert.assertEquals("Default rotation: 0", 0,
                this.pictureFileData.quarterRotation);
        Assert.assertNotNull(
                "Default rotation: 0 (offscreen image should not be changed)",
                this.pictureFileData.offscreenImage);
        this.pictureFileData.addRotation(1);
        Assert.assertEquals("First rotation", 1,
                this.pictureFileData.quarterRotation);
        Assert.assertNull("First rotation:  offscreen image should be cleared",
                this.pictureFileData.offscreenImage);
        this.pictureFileData.addRotation(1);
        Assert.assertEquals("Second rotation", 2,
                this.pictureFileData.quarterRotation);
        this.pictureFileData.addRotation(1);
        Assert.assertEquals("Third rotation", 3,
                this.pictureFileData.quarterRotation);
        this.pictureFileData.addRotation(1);
        Assert.assertEquals("Fourth rotation", 0,
                this.pictureFileData.quarterRotation);

        this.pictureFileData.addRotation(2);
        Assert.assertEquals("Double rotation", 2,
                this.pictureFileData.quarterRotation);
        this.pictureFileData.addRotation(2);
        Assert.assertEquals("Second double rotation", 0,
                this.pictureFileData.quarterRotation);

        this.pictureFileData.addRotation(-3);
        Assert.assertEquals("Double rotation", 1,
                this.pictureFileData.quarterRotation);

        this.pictureFileData.addRotation(25);
        Assert.assertEquals("'Big' rotation", 2,
                this.pictureFileData.quarterRotation);
    }

    /**
     * @throws IOException
     */
    @Test
    public void testDeleteTransformedPictureFile() throws IOException {
        File tempFile = null;
        try {
            // First test: call after initialieation (no transformed file)
            Assert.assertNull(
                    "No transformed file at PictureFileData creation",
                    this.pictureFileData.transformedPictureFile);
            // The next line should do nothing.
            this.pictureFileData.deleteTransformedPictureFile();
            Assert.assertNull(
                    "No transformed file at PictureFileData creation",
                    this.pictureFileData.transformedPictureFile);

            // Second: real case. There a file to delete.
            tempFile = File.createTempFile("JUploadTest",
                    "_testDeleteTransformedPictureFile");
            Assert.assertTrue(
                    "The file should be removed from the file system", tempFile
                            .canRead());
            this.pictureFileData.transformedPictureFile = tempFile;
            this.pictureFileData.deleteTransformedPictureFile();
            Assert.assertNull("The transformed file should be deleted",
                    this.pictureFileData.transformedPictureFile);
            Assert.assertFalse(
                    "The file should be removed from the file system", tempFile
                            .canRead());
        } finally {
            if (tempFile != null) {
                tempFile.delete();
                tempFile = null;
            }
        }
    }

    /**
     * @throws IOException
     */
    @Test
    public void testDeleteWorkingCopyPictureFile() throws IOException {
        File tempFile = null;
        try {
            // First test: call after initialization (no working file)
            Assert.assertNull(
                    "No working copy file at PictureFileData creation",
                    this.pictureFileData.workingCopyTempFile);
            // The next line should do nothing.
            this.pictureFileData.deleteWorkingCopyPictureFile();
            Assert.assertNull(
                    "No working copy file at PictureFileData creation",
                    this.pictureFileData.workingCopyTempFile);

            // Second: real case. There a file to delete.
            tempFile = File.createTempFile("JUploadTest",
                    "_testDeleteWorkingCopyTempFile");
            Assert.assertTrue(
                    "The file should be removed from the file system", tempFile
                            .canRead());
            this.pictureFileData.workingCopyTempFile = tempFile;
            this.pictureFileData.deleteWorkingCopyPictureFile();
            Assert.assertNull("The working copy file should be deleted",
                    this.pictureFileData.workingCopyTempFile);
            Assert.assertFalse(
                    "The file should be removed from the file system", tempFile
                            .canRead());
        } finally {
            if (tempFile != null) {
                tempFile.delete();
                tempFile = null;
            }
        }
    }

    /**
     * Check the the maxWidth is respected, and applied to all dimensions of the
     * original picture.
     * 
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testInitTransformedPictureFile_Smaller_maxWidth()
            throws JUploadException, IOException {
        int maxWidth = 11;// Should the max that is really used
        int maxHeight = 1001;// Should be 'overriden' by maxWidth

        // We need to control the PictureUploadPolicy parameters. In the test
        // case, we use the system properties for that.
        System.setProperty("maxPicWidth", Integer.toString(maxWidth));
        System.setProperty("maxPicHeight", Integer.toString(maxHeight));
        setUp();
        Assert.assertNull("No transformed file at startup",
                this.pictureFileData.transformedPictureFile);
        this.pictureFileData.initTransformedPictureFile();
        Assert.assertNotNull("The transformed file should now be created",
                this.pictureFileData.transformedPictureFile);
        BufferedImage transformedImage = ImageIO
                .read(this.pictureFileData.transformedPictureFile);
        Assert.assertEquals("maxWidth must be respected (width)", maxWidth,
                transformedImage.getWidth());
        Assert.assertEquals("maxWidth must be respected (height)", maxWidth,
                transformedImage.getHeight());

        // Let's clean these files
        this.pictureFileData.deleteTransformedPictureFile();
        this.pictureFileData.deleteWorkingCopyPictureFile();
    }

    /**
     * Check the the maxHeight is respected, and applied to all dimensions of
     * the original picture.
     * 
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testInitTransformedPictureFile_Smaller_maxHeight()
            throws JUploadException, IOException {
        int maxWidth = 12345;// Should be 'overriden' by maxHeight
        int maxHeight = 12;// Should the max that is really used

        // We need to control the PictureUploadPolicy parameters. In the test
        // case, we use the system properties for that.
        System.setProperty("maxPicWidth", Integer.toString(maxWidth));
        System.setProperty("maxPicHeight", Integer.toString(maxHeight));
        setUp();
        Assert.assertNull("No transformed file at startup",
                this.pictureFileData.transformedPictureFile);
        this.pictureFileData.initTransformedPictureFile();
        Assert.assertNotNull("The transformed file should now be created",
                this.pictureFileData.transformedPictureFile);
        BufferedImage transformedImage = ImageIO
                .read(this.pictureFileData.transformedPictureFile);
        Assert.assertEquals("maxHeight must be respected (width)", maxHeight,
                transformedImage.getWidth());
        Assert.assertEquals("maxHeight must be respected (height)", maxHeight,
                transformedImage.getHeight());

        // Let's clean these files
        this.pictureFileData.deleteTransformedPictureFile();
        this.pictureFileData.deleteWorkingCopyPictureFile();
    }

    /**
     * Check the the maxHeight is respected, and applied to all dimensions of
     * the original picture.
     * 
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testInitTransformedPictureFile_Bigger_maxHeight()
            throws JUploadException, IOException {
        int maxWidth = 12345;// Should be 'overriden' by maxHeight
        int maxHeight = 120;// Should be ignored, as it's bigger from the
        // original size

        // We need to control the PictureUploadPolicy parameters. In the test
        // case, we use the system properties for that.
        System.setProperty("maxPicWidth", Integer.toString(maxWidth));
        System.setProperty("maxPicHeight", Integer.toString(maxHeight));
        setUp();
        Assert.assertNull("No transformed file at startup",
                this.pictureFileData.transformedPictureFile);
        this.pictureFileData.initTransformedPictureFile();
        // The original image is a square of 48x48 pixels. Should be unchanged.
        Assert.assertNull("The transformed file should now be created",
                this.pictureFileData.transformedPictureFile);
    }

    /**
     * @throws JUploadIOException
     */
    @Test
    public void testInitWidthAndHeight() throws JUploadIOException {
        Assert.assertTrue("originalHeight is not set at startup",
                this.pictureFileData.originalHeight < 0);
        Assert.assertTrue("originalWidth is not set at startup",
                this.pictureFileData.originalWidth < 0);
        this.pictureFileData.initWidthAndHeight();
        Assert.assertEquals("originalHeight is now set (attribute)", 48,
                this.pictureFileData.originalHeight);
        Assert.assertEquals("originalHeight is now set (getter)", 48,
                this.pictureFileData.getOriginalHeight());
        Assert.assertEquals("originalWidth is now set (attribute)", 48,
                this.pictureFileData.originalWidth);
        Assert.assertEquals("originalWidth is now set (getter)", 48,
                this.pictureFileData.getOriginalWidth());
    }

    /**
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws JUploadIOException
     */
    @Test
    public void testCreateTransformedPictureFile() throws IOException,
            IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, JUploadIOException {
        Assert.assertNull("Default transformedPictureFile is null",
                this.pictureFileData.transformedPictureFile);
        this.pictureFileData.createTransformedTempFile();
        // Let's check the creation of the file.
        Assert.assertTrue("The transformedPictureFile is now created",
                this.pictureFileData.transformedPictureFile.canWrite());
        // Let's check the call to registerUnload.
        File f = this.pictureFileData.transformedPictureFile;
        testLastRegisteredUnload(PictureFileData.class);
        Assert.assertNull(
                "The transformedPictureFile attribute has been cleaned",
                this.pictureFileData.transformedPictureFile);
        Assert.assertFalse("The transformedPictureFile is now deleted", f
                .canWrite());
    }

    /**
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    @Test
    public void testCreateWorkingCopyTempFile() throws IOException,
            IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Assert.assertNull("Default workingCopyTempFile is null",
                this.pictureFileData.workingCopyTempFile);
        this.pictureFileData.createWorkingCopyTempFile();
        // Let's check the creation of the file.
        Assert.assertTrue("The workingCopyTempFile is now created",
                this.pictureFileData.workingCopyTempFile.canWrite());
        // Let's check the call to registerUnload.
        File f = this.pictureFileData.workingCopyTempFile;
        testLastRegisteredUnload(PictureFileData.class);
        Assert.assertNull("The workingCopyTempFile is now deleted",
                this.pictureFileData.workingCopyTempFile);
        Assert.assertFalse("The workingCopyTempFile is now deleted", f
                .canWrite());
    }

    /**
     * @throws JUploadIOException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    @Test
    public void testGetWorkingSourceFile() throws JUploadIOException,
            IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        File fDummy = new File("This file does not exist");
        this.pictureFileData.workingCopyTempFile = fDummy;
        // First: let's check that the getter won't do anthing, if the file is
        // already created.
        Assert
                .assertEquals(
                        "Check the response of getWorkingSourceFile, when the file is set)",
                        fDummy, this.pictureFileData.getWorkingSourceFile());

        // Then, check file creation.
        this.pictureFileData.workingCopyTempFile = null;
        // We'll just check the length.
        Assert
                .assertEquals(
                        "Check the response of getWorkingSourceFile, when the file is set)",
                        this.file.length(), this.pictureFileData
                                .getWorkingSourceFile().length());

        File f = this.pictureFileData.workingCopyTempFile;
        testLastRegisteredUnload(PictureFileData.class);
        Assert.assertNull("The workingCopyTempFile is now deleted",
                this.pictureFileData.workingCopyTempFile);
        Assert.assertFalse("The workingCopyTempFile is now deleted", f
                .canWrite());
    }

    /**
     * @throws JUploadException
     * @throws IOException
     */
    @Test
    public void testGetImageIcon() throws JUploadException, IOException {
        int maxWidth = 20;
        int maxHeight = 20;

        ImageIcon icon = PictureFileData.getImageIcon(this.file, maxWidth,
                maxHeight, this.uploadPolicy);
        Assert.assertEquals("maxWidth should be exactly respected", maxWidth,
                icon.getIconWidth());
        Assert.assertEquals("maxHeight should be exactly respected", maxHeight,
                icon.getIconHeight());

        maxWidth = 23;
        maxHeight = 251;
        icon = PictureFileData.getImageIcon(this.file, maxWidth, maxHeight,
                this.uploadPolicy);
        Assert.assertEquals("maxWidth should be exactly respected", maxWidth,
                icon.getIconWidth());
        Assert.assertTrue("maxHeight should be respected", maxHeight > icon
                .getIconHeight());

        // A test with a 'must be respected' height
        maxWidth = 239;
        maxHeight = 25;
        icon = PictureFileData.getImageIcon(this.file, maxWidth, maxHeight,
                this.uploadPolicy);
        Assert.assertTrue("maxWidth should be respected", maxWidth > icon
                .getIconWidth());
        Assert.assertEquals("maxHeight should be exactly respected", maxHeight,
                icon.getIconHeight());
    }

    /** */
    @Test
    public void testIsFileAPicture() {
        File f = new File("/qdqd/test.gif");
        Assert.assertTrue("gif files are valid pictures", PictureFileData
                .isFileAPicture(f));
        f = new File("/qdqd/test.jpg");
        Assert.assertTrue("jpg files are valid pictures", PictureFileData
                .isFileAPicture(f));
        f = new File("/qdqd/test.jpeg");
        Assert.assertTrue("jpeg files are valid pictures", PictureFileData
                .isFileAPicture(f));
        f = new File("/qdqd/test.bmp");
        Assert.assertTrue("bmp files are valid pictures", PictureFileData
                .isFileAPicture(f));
        f = new File("/qdqd/test.abc");
        Assert.assertFalse("abc files are not valid pictures", PictureFileData
                .isFileAPicture(f));
        f = new File("/qdqd/test.txt");
        Assert.assertFalse("txt files are not valid pictures", PictureFileData
                .isFileAPicture(f));
    }
}
