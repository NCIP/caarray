//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.genepix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.internal.matchers.StringContains.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of Gal design handler.
 * 
 * @author dkokotov, dharley
 */
public class GalDesignHandlerTest {
    private static final String QUOTE = "\"";
    private static final String NO_QUOTE = "";
    private static final String TAB = "\t";
    private static final String COMMA_SPACE = ", ";
    private static final URI TEST_URI = CaArrayUtils.makeUriQuietly("foo:bar");

    private GalDesignHandler sut;
    private CaArrayFile caArrayFile;
    private File testDataFile;
    private DataStorageFacade dataStorageFacade;

    @Before
    public void setUp() throws IOException {
        final SessionTransactionManager sessionTransactionManager = new SessionTransactionManagerNoOpImpl();
        final ArrayDao arrayDao = mock(ArrayDao.class);
        final SearchDao searchDao = mock(SearchDao.class);

        this.dataStorageFacade = mock(DataStorageFacade.class);
        this.sut = new GalDesignHandler(sessionTransactionManager, this.dataStorageFacade, arrayDao, searchDao);

        this.caArrayFile = mock(CaArrayFile.class);
        when(this.caArrayFile.getFileType()).thenReturn(GalDesignHandler.GAL_FILE_TYPE);
        when(this.caArrayFile.getDataHandle()).thenReturn(TEST_URI);
        when(this.caArrayFile.getName()).thenReturn("test_data.gal");
    }

    @After
    public void tearDown() {
        if (null != this.testDataFile) {
            this.testDataFile.delete();
        }
    }

    @Test
    public void allowsDoubleValueInBlockXOrigin() throws PlatformFileReadException, IOException {
        final String xOrigin = "10875.876627999995";
        final String yOrigin = "32775";
        final String testData = generateTestData(xOrigin, yOrigin);

        assertGalDesignHandlerValidatesData(testData);
    }

    @Test
    public void allowsDoubleValueInBlockYOrigin() throws PlatformFileReadException, IOException {
        final String xOrigin = "10875";
        final String yOrigin = "32775.75";
        final String testData = generateTestData(xOrigin, yOrigin);

        assertGalDesignHandlerValidatesData(testData);
    }

    @Test
    public void loadsWithoutExceptionWhenDoubleValueInBlockXOrigin() throws PlatformFileReadException, IOException {
        final String xOrigin = "10875.876627999995";
        final String yOrigin = "32775";
        final String testData = generateTestData(xOrigin, yOrigin);

        galDesignHandlerLoadsDataWithoutThrowing(testData);
    }

    @Test
    public void loadsWithoutExceptionWhenDoubleValueInBlockYOrigin() throws PlatformFileReadException, IOException {
        final String xOrigin = "10875";
        final String yOrigin = "32775.75";
        final String testData = generateTestData(xOrigin, yOrigin);

        galDesignHandlerLoadsDataWithoutThrowing(testData);
    }

    @Test
    public void createsDesignDetailsWithoutExceptionWhenDoubleValueInBlockXOrigin() throws PlatformFileReadException,
            IOException {
        final String xOrigin = "10875.876627999995";
        final String yOrigin = "32775";
        final String testData = generateTestData(xOrigin, yOrigin);

        galDesignHandlerCreatesDesignDetailsWithoutThrowing(testData);
    }

    @Test
    public void createsDesignDetailsWithoutExceptionWhenDoubleValueInBlockYOrigin() throws PlatformFileReadException,
            IOException {
        final String xOrigin = "10875";
        final String yOrigin = "32775.75";
        final String testData = generateTestData(xOrigin, yOrigin);

        galDesignHandlerCreatesDesignDetailsWithoutThrowing(testData);
    }

    @Test
    public void allowsHeaderVariant() throws PlatformFileReadException, IOException {
        final String xOrigin = "10875.876627999995";
        final String yOrigin = "32775";
        final String testData = generateTestDataWithHeaderVariant(xOrigin, yOrigin);

        assertGalDesignHandlerValidatesData(testData);
    }

    private void assertGalDesignHandlerValidatesData(String testData) throws PlatformFileReadException, IOException {
        final ValidationResult validationResult = validateData(testData);
        assertTrue(validationResult.isValid());
    }

    private ValidationResult validateData(String testData) throws IOException, PlatformFileReadException {
        createTestDataFile(testData);

        final ValidationResult validationResult = new ValidationResult();

        this.sut.openFiles(Collections.singleton(this.caArrayFile));
        this.sut.validate(validationResult);
        this.sut.closeFiles();
        return validationResult;
    }

    private void galDesignHandlerLoadsDataWithoutThrowing(String testData) throws PlatformFileReadException,
            IOException {
        createTestDataFile(testData);

        final ArrayDesign arrayDesign = mock(ArrayDesign.class);

        this.sut.openFiles(Collections.singleton(this.caArrayFile));
        this.sut.load(arrayDesign);
        this.sut.closeFiles();

        // Passes if no exception thrown
    }

    private void galDesignHandlerCreatesDesignDetailsWithoutThrowing(String testData) throws PlatformFileReadException,
            IOException {
        createTestDataFile(testData);

        final ArrayDesign arrayDesign = mock(ArrayDesign.class);

        this.sut.openFiles(Collections.singleton(this.caArrayFile));
        this.sut.createDesignDetails(arrayDesign);
        this.sut.closeFiles();

        // Passes if no exception thrown
    }

    private void createTestDataFile(String contents) throws IOException {
        this.testDataFile = createTempFile("gal", contents);
        when(this.dataStorageFacade.openFile(TEST_URI, false)).thenReturn(this.testDataFile);
    }

    private File createTempFile(final String fileNameSuffix, final String contents) throws IOException {
        return createTempFile(fileNameSuffix, contents.getBytes("US-ASCII"));
    }

    private File createTempFile(final String fileNameSuffix, final byte[] contents) throws IOException {
        final String fileNamePrefix = this.getClass().getName();
        final File testFile = File.createTempFile(fileNamePrefix, fileNameSuffix);
        final FileOutputStream outputStream = new FileOutputStream(testFile);
        try {
            outputStream.write(contents);
        } finally {
            outputStream.close();
        }
        return testFile;
    }

    private String generateTestData(String xOrigin, String yOrigin) {
        final StringBuilder builder = new StringBuilder();

        appendTabDelimitedLine(builder, NO_QUOTE, "ATF", "1.0");
        appendTabDelimitedLine(builder, NO_QUOTE, "7", "10");
        builder.append("\"Type=GenePix ArrayList V1.0\"\n").append("\"BlockCount=1\"\n").append("\"BlockType=3\"\n")
                .append("\"URL=null\"\n").append("\"Supplier=Agilent Technologies\"\n")
                .append("\"ArrayName=UNC_Perou_Lab_244K_custom_V3\"\n").append("\"Block1=");
        appendDelimitedList(builder, COMMA_SPACE, NO_QUOTE, xOrigin, yOrigin, "65.0", "5", "73.32348400000001", "1",
                "63.5").append("\"\n");
        appendTabDelimitedLine(builder, QUOTE, "Block", "Column", "Row", "Name", "ID", "RefNumber", "ControlType",
                "GeneName", "TopHit", "Description");
        appendTabDelimitedLine(builder, QUOTE, "1", "1", "1", "", "SPOT_1", "10", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "2", "1", "", "SPOT_2", "20", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "3", "1", "", "SPOT_3", "30", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "4", "1", "", "SPOT_4", "40", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "5", "1", "", "SPOT_5", "50", "false", "", "", "");

        return builder.toString();
    }

    /**
     * Test of a variant of the header where there is whitespace around the equals sign, and
     * the origin and other values are separated by tabs instead of commas.
     */
    private String generateTestDataWithHeaderVariant(String xOrigin, String yOrigin) {
        final StringBuilder builder = new StringBuilder();

        appendTabDelimitedLine(builder, NO_QUOTE, "ATF", "1.0");
        appendTabDelimitedLine(builder, NO_QUOTE, "7", "10");
        builder.append("\"Type=GenePix ArrayList V1.0\"\n").append("\"BlockCount=1\"\n").append("\"BlockType=3\"\n")
                .append("\"URL=null\"\n").append("\"Supplier=Agilent Technologies\"\n")
                .append("\"ArrayName=UNC_Perou_Lab_244K_custom_V3\"\n").append("\"Block1 =\t");
        appendDelimitedList(builder, TAB, NO_QUOTE, xOrigin, yOrigin, "65.0", "5", "73.32348400000001", "1",
                "63.5").append("\"\n");
        appendTabDelimitedLine(builder, QUOTE, "Block", "Column", "Row", "Name", "ID", "RefNumber", "ControlType",
                "GeneName", "TopHit", "Description");
        appendTabDelimitedLine(builder, QUOTE, "1", "1", "1", "", "SPOT_1", "10", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "2", "1", "", "SPOT_2", "20", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "3", "1", "", "SPOT_3", "30", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "4", "1", "", "SPOT_4", "40", "false", "", "", "");
        appendTabDelimitedLine(builder, QUOTE, "1", "5", "1", "", "SPOT_5", "50", "false", "", "", "");

        return builder.toString();
    }

    private StringBuilder appendDelimitedList(StringBuilder builder, String delimiter, String quote, String... fields) {
        String nextDelimiter = "";

        for (final String field : fields) {
            builder.append(nextDelimiter).append(quote).append(field).append(quote);

            nextDelimiter = delimiter;
        }

        return builder;
    }

    private StringBuilder appendTabDelimitedLine(StringBuilder builder, String quote, String... fields) {
        return appendDelimitedList(builder, TAB, quote, fields).append('\n');
    }
}
