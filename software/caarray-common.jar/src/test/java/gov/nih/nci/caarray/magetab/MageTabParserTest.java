/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.Investigation;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDesign;
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.List;

import org.junit.Test;

/**
 * Tests for the MageTabParser subsystem.
 */
@SuppressWarnings("PMD")
public class MageTabParserTest {

    private static final int ONE = 1;
    private static final int SIX = 6;
    private MageTabParser parser = MageTabParser.INSTANCE;

    @Test
    public void testValidate() {
        MageTabInputFileSet fileSet = TestMageTabSets.MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET;
        ValidationResult result;
        try {
            result = parser.validate(fileSet);
            assertFalse(result.isValid());
            assertEquals(15, result.getMessages().size());
            
        } catch (MageTabParsingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseEmptySet() throws InvalidDataException, MageTabParsingException  {
            MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
            MageTabDocumentSet documentSet = parser.parse(inputFileSet);
            assertNotNull(documentSet);
    }

    @Test
    public void testValidateMissingSdrf() throws MageTabParsingException  {
            MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
            inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
            ValidationResult result = parser.validate(inputFileSet);
            assertNotNull(result);
            assertFalse(result.isValid());
            assertEquals(1, result.getFileValidationResults().size());
            FileValidationResult fileValidationResult = result.getFileValidationResults().get(0);
            assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF, fileValidationResult.getFile());
            assertEquals(1, fileValidationResult.getMessages().size());
            ValidationMessage message = fileValidationResult.getMessages().get(0);
            assertEquals(33, message.getLine());
            assertEquals(2, message.getColumn());
            assertTrue(message.getMessage().startsWith("Referenced SDRF file "));
            assertTrue(message.getMessage().endsWith( " was not included in the MAGE-TAB document set"));
    }
    
    @Test
    public void testValidateMissingDataFiles() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        ValidationResult result = parser.validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
    }

    @Test
    public void testParse() throws MageTabParsingException, InvalidDataException {
        testSpecificationDocuments();
        testTcgaBroadDocuments();
    }


    private void testTcgaBroadDocuments() throws MageTabParsingException, InvalidDataException {
        MageTabInputFileSet fileSet = TestMageTabSets.TCGA_BROAD_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertEquals(1, documentSet.getIdfDocuments().size());
        assertEquals(1, documentSet.getSdrfDocuments().size());
        assertEquals(1, documentSet.getDataMatrixes().size());
        assertEquals(26, documentSet.getNativeDataFiles().size());
        checkSdrfTranslation(documentSet.getSdrfDocuments().iterator().next());
        checkArrayDesigns(documentSet);
        assertTrue(documentSet.getValidationResult().isValid());
    }

    private void checkSdrfTranslation(SdrfDocument document) {
        List<Hybridization> hybridizations = document.getAllHybridizations();
        assertEquals(26, hybridizations.size());
        for (Hybridization hybridization : hybridizations) {
            assertEquals(1, hybridization.getSuccessorArrayDataFiles().size());
        }
        List<ArrayDataFile> arrayDataFiles = document.getAllArrayDataFiles();
        assertEquals(26, arrayDataFiles.size());
    }

    private void checkArrayDesigns(MageTabDocumentSet documentSet) {
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertEquals(1, sdrfDocument.getAllArrayDesigns().size());
        ArrayDesign arrayDesign = sdrfDocument.getAllArrayDesigns().get(0);
        for (Hybridization hybridization : sdrfDocument.getAllHybridizations()) {
            assertEquals(arrayDesign, hybridization.getArrayDesign());
        }
    }

    private void testSpecificationDocuments() throws MageTabParsingException, InvalidDataException {
        MageTabInputFileSet fileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        checkTerms(documentSet);
        assertEquals(1, documentSet.getIdfDocuments().size());
        IdfDocument idfDocument = documentSet.getIdfDocument(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        Investigation investigation = idfDocument.getInvestigation();
        assertEquals(3, investigation.getProtocols().size());
        assertEquals("submitter", investigation.getPersons().get(0).getRoles().get(0).getValue());
        assertEquals(2, investigation.getPersons().get(0).getRoles().size());
        assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php", investigation.getProtocols().get(0)
                .getType().getTermSource().getFile());
        assertEquals("University of Heidelberg H sapiens TK6", investigation.getTitle());
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertNotNull(sdrfDocument);
        assertEquals(ONE, documentSet.getSdrfDocuments().size());
        assertTrue(idfDocument.getDocumentSet().getSdrfDocuments().contains(sdrfDocument));
        assertEquals(idfDocument, sdrfDocument.getIdfDocument());
        assertEquals(6, sdrfDocument.getLeftmostNodes().size());
        assertEquals(SIX, sdrfDocument.getAllSources().size());
        assertEquals(SIX, sdrfDocument.getAllSamples().size());
        assertEquals(SIX, sdrfDocument.getAllExtracts().size());
        assertEquals(SIX, sdrfDocument.getAllHybridizations().size());
        assertEquals(SIX, sdrfDocument.getAllLabeledExtracts().size());
        assertEquals(1, sdrfDocument.getAllHybridizations().get(0).getSuccessorArrayDataFiles().size());
        ArrayDataFile arrayDataFile = sdrfDocument.getAllHybridizations().get(0).getSuccessorArrayDataFiles().iterator().next();
        assertNotNull(arrayDataFile);
        assertEquals("H_TK6 replicate 1.CEL", arrayDataFile.getName());
    }

    private void checkTerms(MageTabDocumentSet documentSet) {
        for (OntologyTerm term : documentSet.getTerms()) {
            assertNotNull("null category for term " + term.getValue(), term.getCategory());
            assertNotNull("null value for term", term.getValue());
            assertNotNull("null source for term " + term.getValue(), term.getTermSource());
        }
    }

}
