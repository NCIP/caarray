package gov.nih.nci.caarray.platforms.genepix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.StringContains.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GalDesignHandlerTest {
	private static final String QUOTE = "\"";
	private static final String NO_QUOTE = "";
	private static final String TAB = "\t";
	private static final String COMMA_SPACE = ", ";
	
	private GalDesignHandler sut;
	private FileManager fileManager;
	private CaArrayFile caArrayFile;
	private File testDataFile;
	
	@Before
	public void setUp() throws IOException {
		SessionTransactionManager sessionTransactionManager = new SessionTransactionManagerNoOpImpl();
		fileManager = mock(FileManager.class);
		ArrayDao arrayDao = mock(ArrayDao.class);
		SearchDao searchDao = mock(SearchDao.class);

		sut = new GalDesignHandler(sessionTransactionManager, fileManager, arrayDao, searchDao);
		
		caArrayFile = mock(CaArrayFile.class);
		when(caArrayFile.getFileType()).thenReturn(FileType.GENEPIX_GAL);		
	}
	
	@After
	public void tearDown() {
		if (null != testDataFile) {
			testDataFile.delete();
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
	public void failsValidationIfInvalidDoubleValueInBlockXOrigin() throws PlatformFileReadException, IOException {						
		final String xOrigin = "NOT-A-VALID-DOUBLE";
		final String yOrigin = "32775.75";
		final String testData = generateTestData(xOrigin, yOrigin);

		final int expectedColumnNumber = 1;
		final String expectedMessageSubstring = "xOrigin";

		assertMessageInColumnContainsString(testData, expectedColumnNumber,
				expectedMessageSubstring);		
	}

	@Test
	public void failsValidationIfDoubleValueInBlockYOrigin() throws PlatformFileReadException, IOException {						
		final String xOrigin = "10875.876627999995";
		final String yOrigin = "NOT-A-VALID-DOUBLE";
		final String testData = generateTestData(xOrigin, yOrigin);
		
		final int expectedColumnNumber = 2;
		final String expectedMessageSubstring = "yOrigin";

		assertMessageInColumnContainsString(testData, expectedColumnNumber,
				expectedMessageSubstring);		
	}

	private void assertMessageInColumnContainsString(final String testData,
			final int expectedColumnNumber,
			final String expectedMessageSubstring) throws IOException,
			PlatformFileReadException {
		ValidationResult validationResult = validateData(testData);		
		assertFalse(validationResult.isValid());
		assertEquals(1, validationResult.getFileValidationResults().size());
		FileValidationResult fileValidationResult = validationResult.getFileValidationResults().get(0);
		assertEquals(1, fileValidationResult.getMessages().size());
		ValidationMessage message = fileValidationResult.getMessages().get(0);
		assertEquals(expectedColumnNumber, message.getColumn());
		assertThat(message.getMessage(), containsString(expectedMessageSubstring));
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
	public void createsDesignDetailsWithoutExceptionWhenDoubleValueInBlockXOrigin() throws PlatformFileReadException, IOException {						
		final String xOrigin = "10875.876627999995";
		final String yOrigin = "32775";
		final String testData = generateTestData(xOrigin, yOrigin);
		
		galDesignHandlerCreatesDesignDetailsWithoutThrowing(testData);
	}

	@Test
	public void createsDesignDetailsWithoutExceptionWhenDoubleValueInBlockYOrigin() throws PlatformFileReadException, IOException {						
		final String xOrigin = "10875";
		final String yOrigin = "32775.75";
		final String testData = generateTestData(xOrigin, yOrigin);
		
		galDesignHandlerCreatesDesignDetailsWithoutThrowing(testData);
	}
	
	private void assertGalDesignHandlerValidatesData(String testData)
			throws PlatformFileReadException, IOException {
		ValidationResult validationResult = validateData(testData);		
		assertTrue(validationResult.isValid());
	}

	private ValidationResult validateData(String testData) throws IOException,
			PlatformFileReadException {
		createTestDataFile(testData);
		
		ValidationResult validationResult = new ValidationResult();
		
		sut.openFiles(Collections.singleton(caArrayFile));
		sut.validate(validationResult);
		sut.closeFiles();
		return validationResult;
	}
	
	private void galDesignHandlerLoadsDataWithoutThrowing(String testData)
			throws PlatformFileReadException, IOException {
		createTestDataFile(testData);

		ArrayDesign arrayDesign = mock(ArrayDesign.class);

		sut.openFiles(Collections.singleton(caArrayFile));
		sut.load(arrayDesign);
		sut.closeFiles();
		
		// Passes if no exception thrown
	}

	
	private void galDesignHandlerCreatesDesignDetailsWithoutThrowing(String testData)
			throws PlatformFileReadException, IOException {
		createTestDataFile(testData);

		ArrayDesign arrayDesign = mock(ArrayDesign.class);

		sut.openFiles(Collections.singleton(caArrayFile));
		sut.createDesignDetails(arrayDesign);
		sut.closeFiles();
		
		// Passes if no exception thrown
	}


	private void createTestDataFile(String contents) throws IOException {
		testDataFile = createTempFile("gal", contents);
		when(fileManager.openFile(caArrayFile)).thenReturn(testDataFile);
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
		StringBuilder builder = new StringBuilder();

		appendTabDelimitedLine(builder, NO_QUOTE, "ATF", "1.0");
		appendTabDelimitedLine(builder, NO_QUOTE, "7", "10");
		builder.append("\"Type=GenePix ArrayList V1.0\"\n")
				.append("\"BlockCount=1\"\n")
				.append("\"BlockType=3\"\n")
				.append("\"URL=null\"\n")
				.append("\"Supplier=Agilent Technologies\"\n")
				.append("\"ArrayName=UNC_Perou_Lab_244K_custom_V3\"\n")
				.append("\"Block1=");
		appendDelimitedList(builder, COMMA_SPACE, NO_QUOTE, xOrigin, yOrigin, "65.0", "5", "73.32348400000001", "1", "63.5").append("\"\n");
		appendTabDelimitedLine(builder, QUOTE, "Block", "Column", "Row", "Name", "ID", "RefNumber", "ControlType", "GeneName", "TopHit", "Description");
		appendTabDelimitedLine(builder, QUOTE, "1", "1", "1", "", "SPOT_1", "10", "false", "", "", "");
		appendTabDelimitedLine(builder, QUOTE, "1", "2", "1", "", "SPOT_2", "20", "false", "", "", "");
		appendTabDelimitedLine(builder, QUOTE, "1", "3", "1", "", "SPOT_3", "30", "false", "", "", "");
		appendTabDelimitedLine(builder, QUOTE, "1", "4", "1", "", "SPOT_4", "40", "false", "", "", "");
		appendTabDelimitedLine(builder, QUOTE, "1", "5", "1", "", "SPOT_5", "50", "false", "", "", "");

		return builder.toString();
	}
	

	private StringBuilder appendDelimitedList(StringBuilder builder, String delimiter,
			String quote, String... fields) {
		String nextDelimiter = "";
		
		for (String field : fields) {
			builder.append(nextDelimiter)
			.append(quote)
			.append(field)
			.append(quote);
			
			nextDelimiter = delimiter;
		}
		
		return builder;
	}
	
	private StringBuilder appendTabDelimitedLine(StringBuilder builder, String quote, String... fields) {
		return appendDelimitedList(builder, TAB, quote, fields).append('\n');
	}
}