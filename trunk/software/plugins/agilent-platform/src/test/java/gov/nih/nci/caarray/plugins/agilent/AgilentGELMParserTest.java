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
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;

import org.junit.Ignore;
import org.junit.Test;

public class AgilentGELMParserTest {
    final private TokenizerStub tokenizer = new TokenizerStub();
    final private AgilentGELMParser parser = new AgilentGELMParser(tokenizer);

    @Test
    public void acceptsDocumentWithProject() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.BY);
        add(Token.COMPANY);
        add(Token.DATE);
        add(Token.ID);
        add(Token.NAME);
        add(Token.ORGANIZATION);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithPrinting() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWith2Printings() {

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithPattern() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWith2Patterns() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWith2Others() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsPrintingWithNoChip() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPrintingWithOneChip() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithTwoChips() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);        
        add(Token.DATE);
        add(Token.PATTERN_NAME);
        add(Token.PREPARED_AT_SITE);
        add(Token.PREPARED_BY);
        add(Token.PREPARED_BY_ORG);
        add(Token.PRINTER);
        add(Token.RUN_DESCRIPTION);
        add(Token.TYPE);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsChipWithNoBarcode() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsChipWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.PREPARED_FOR);
        add(Token.PREPARED_FOR_ORG);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsChipWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsChipWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithOneReporter() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithTwoReporters() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsPatternWithNoReporter() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPatternWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ACCESS);
        add(Token.DESCRIPTION);
        add(Token.NAME);
        add(Token.OWNER);
        add(Token.SPECIES_DATABASE);
        add(Token.TYPE);
        add(Token.TYPE_ID);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithOneFeature() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithTwoFeatures() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsReporterWithNoFeature() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsReporterWithNoName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsReporterWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.ACCESSION);
        add(Token.ACTIVE_SEQUENCE);
        add(Token.CONTROL_TYPE);
        add(Token.DELETION);
        add(Token.DESCRIPTION);
        add(Token.FAIL_TYPE);
        add(Token.LINKER_SEQUENCE);
        add(Token.MISMATCH_COUNT);
        add(Token.NAME, "reporter name");
        add(Token.PRIMER1_SEQUENCE);
        add(Token.PRIMER2_SEQUENCE);
        add(Token.START_COORD);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithOneGene() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsReporterWithTwoGenes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsFeatureWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.CTRL_FOR_FEAT_NUM);
        add(Token.NUMBER, 123456);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsFeatureWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsFeatureWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsFeatureWithOnePen() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsFeatureWithTwoPens() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsFeatureWithNoPosition() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsFeatureWithTwoPositions() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPenWithNoUnits() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPenWithNoX() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPenWithNoY() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPenWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPenWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsPositionWithNoUnits() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPositionWithNoX() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPositionWithNoY() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPositionWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPositionWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithOneAccession() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithTwoAccessions() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithOneAlias() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithTwoAliases() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsGeneWithNoPrimaryName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsGeneWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.CHROMOSOME);
        add(Token.DESCRIPTION);
        add(Token.MAP_POSITION);
        add(Token.PRIMARY_NAME);
        add(Token.SPECIES);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsAccessionNoDatabase() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ACCESSION_START);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsAccessionNoIdOrIdentifier() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsAccessionWithId() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAccessionWithIdentifier() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.IDENTIFIER);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAccessionWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAccessionWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsAliasWithNoName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsAliasWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAliasWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsOtherNoName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsOtherNoValue() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsOtherWithOneNestedOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsOtherWithTwoNestedOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsOtherWithDoublyNestedOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGridLayout() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.GRID_LAYOUT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGridLayoutWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.GRID_LAYOUT_START);
        add(Token.FEATURE_COUNT_X);
        add(Token.FEATURE_COUNT_Y);
        add(Token.FEATURE_SPACING_X);
        add(Token.FEATURE_SPACING_Y);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceRef() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.IDENTIFIER);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsBiosequenceRefWithNoIdentifier() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsBiosequenceRefWithNoSpecies() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.IDENTIFIER);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsBiosequenceRefWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.DATABASE);
        add(Token.IDENTIFIER);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts ();
    }

    @Test
    public void acceptsBiosequence() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsBiosequenceWithOutSpecies() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsBiosequenceWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);       
        add(Token.ACCESS);
        add(Token.CHROMOSOME);
        add(Token.CONTROL_TYPE);
        add(Token.DESCRIPTION);
        add(Token.EC_NUMBER);
        add(Token.MAP_POSITION);
        add(Token.PRIMARY_NAME);
        add(Token.SEQUENCEDB);
        add(Token.SPECIES);
        add(Token.TYPE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithOneAccession() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithTwoAccessions() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithOneAlias() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithTwoAliases() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    private void assertParserAccepts() {
        assertTrue(parser.validate());
    }

    private void assertParserRejects() {
        assertFalse(parser.validate());
    }

    private void add(Token token) {
        tokenizer.add(token);
    }

    private void repeat(int count, Token token) {
        tokenizer.repeat(count, token);
    }

    private void add(Token token, Object value) {
        tokenizer.add(token, value);
    }

}
