/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2-branch
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2-branch Software License (the License) is between NCI and You. You (or 
 * Your) shall mean a person or an entity, and all other entities that control, 
 * are controlled by, or are under common control with the entity. Control for 
 * purposes of this definition means (i) the direct or indirect power to cause 
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares, 
 * or (iii) beneficial ownership of such entity. 
 *
 * This License is granted provided that You agree to the conditions described 
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up, 
 * no-charge, irrevocable, transferable and royalty-free right and license in 
 * its rights in the caArray2-branch Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2-branch Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2-branch Software and any 
 * modifications and derivative works thereof; and (iii) sublicense the 
 * foregoing rights set out in (i) and (ii) to third parties, including the 
 * right to license such rights to further third parties. For sake of clarity, 
 * and not by way of limitation, NCI shall have no right of accounting or right 
 * of payment from You or Your sub-licensees for the rights granted under this 
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the 
 * above copyright notice, this list of conditions and the disclaimer and 
 * limitation of liability of Article 6, below. Your redistributions in object 
 * code form must reproduce the above copyright notice, this list of conditions 
 * and the disclaimer of Article 6 in the documentation and/or other materials 
 * provided with the distribution, if any. 
 *
 * Your end-user documentation included with the redistribution, if any, must 
 * include the following acknowledgment: This product includes software 
 * developed by 5AM and the National Cancer Institute. If You do not include 
 * such end-user documentation, You shall include this acknowledgment in the 
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM" 
 * to endorse or promote products derived from this Software. This License does 
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the 
 * terms of this License. 
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this 
 * Software into Your proprietary programs and into any third party proprietary 
 * programs. However, if You incorporate the Software into third party 
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software 
 * into such third party proprietary programs and for informing Your 
 * sub-licensees, including without limitation Your end-users, of their 
 * obligation to secure any required permissions from such third parties before 
 * incorporating the Software into such third party proprietary software 
 * programs. In the event that You fail to obtain such permissions, You agree 
 * to indemnify NCI for any claims against NCI by such third parties, except to 
 * the extent prohibited by law, resulting from Your failure to obtain such 
 * permissions. 
 *
 * For sake of clarity, and not by way of limitation, You may add Your own 
 * copyright statement to Your modifications and to the derivative works, and 
 * You may provide additional or different license terms and conditions in Your 
 * sublicenses of modifications of the Software, or any derivative works of the 
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR 
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
