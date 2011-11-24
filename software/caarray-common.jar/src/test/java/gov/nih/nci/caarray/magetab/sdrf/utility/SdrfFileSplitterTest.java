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
package gov.nih.nci.caarray.magetab.sdrf.utility;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.RowOrientedSdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfInvalidSplitRowCountException;
import gov.nih.nci.caarray.magetab.sdrf.testdata.SdrfTestDataSets;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.util.List;

import org.junit.Test;


/** 
 * Test of SdrfFileSplitter
 * @author asy
 *
 */
public class SdrfFileSplitterTest {

    @Test
    public void testSplitByRowCountUsingFileRef() throws Exception {
        final File origSdrfFile = MageTabDataFiles.GENEPIX_GENE_EXPRESSION_SDRF;
        final JavaIOFileRef origSdrfFileRef = new JavaIOFileRef(origSdrfFile);

        final int bodyRowsCountInOrigSdrf = 14; 
        final int bodyRowsCountPerSplitSdrf = 3;

        List<RowOrientedSdrfDocument> splitSdrfs = new SdrfFileSplitter().splitByRowCount(origSdrfFileRef, 
                bodyRowsCountPerSplitSdrf);

        verifyNumberOfFilesProducedInSplit(bodyRowsCountInOrigSdrf, bodyRowsCountPerSplitSdrf, splitSdrfs);

        final String expectedHeaderRow = "Source Name\tSample Name\tExtract Name\tLabeled Extract Name" 
            + "\tHybridization Name\tArray Data File\tDerived Array Data File";
        verifyHeadersProducedInSplit(expectedHeaderRow, splitSdrfs);

        verifyBodyRowCountsProducedInSplit(bodyRowsCountInOrigSdrf, bodyRowsCountPerSplitSdrf, splitSdrfs);
    }

    @Test(expected = SdrfInvalidSplitRowCountException.class)
    public void testSplitByRowCountGreaterThanSdrfBodyRowsCount() throws Exception {
        final RowOrientedSdrfDocument origRowOrientedSdrf = SdrfTestDataSets.DATA_SET_2.createRowOrientedSdrfDocument();
        final int bodyRowsCountPerSplitSdrf = origRowOrientedSdrf.bodyRowsCount() + 1;
        executeSplitByRowCountUsingRowOrientedDoc(origRowOrientedSdrf, bodyRowsCountPerSplitSdrf);
    }

    @Test
    public void testSplitSdrfWithBodyRowsCountOfOneByRowCountOfOne() throws Exception {
        final RowOrientedSdrfDocument origRowOrientedSdrf = SdrfTestDataSets.DATA_SET_1.createRowOrientedSdrfDocument();
        final int bodyRowsCountPerSplitSdrf = 1;
        executeSplitByRowCountUsingRowOrientedDoc(origRowOrientedSdrf, bodyRowsCountPerSplitSdrf);
    }

    @Test
    public void testSplitSdrfWithBodyRowsCountGreaterThanOneByRowCountOfOne() throws Exception {
        final RowOrientedSdrfDocument origRowOrientedSdrf = SdrfTestDataSets.DATA_SET_2.createRowOrientedSdrfDocument();
        final int bodyRowsCountPerSplitSdrf = 1;
        executeSplitByRowCountUsingRowOrientedDoc(origRowOrientedSdrf, bodyRowsCountPerSplitSdrf);
    }

    @Test
    public void testSplitByRowCountGreaterThanOriginalSdrfBodyRowsCount() throws Exception {
        final RowOrientedSdrfDocument origRowOrientedSdrf = SdrfTestDataSets.DATA_SET_3.createRowOrientedSdrfDocument();
        final int bodyRowsCountPerSplitSdrf = origRowOrientedSdrf.bodyRowsCount() - 1;
        if (bodyRowsCountPerSplitSdrf <= 1) {
            throw new IllegalStateException("Original SDRF has bodyRowsCount=" + origRowOrientedSdrf.bodyRowsCount()
                    + ", bodyRowsCountPerSplitSdrf=" + bodyRowsCountPerSplitSdrf 
                    + ". bodyRowsCount of Original SDRF needs to be >= 3" 
                    + ", so that we could test the case where bodyRowsCountPerSplitSdrf > 1" 
                    + ". The case where bodyRowsCountPerSplitSdrf == 1 is already covered by"
                    + "  testSplitSdrfWithBodyRowsCountOfOneByRowCountOfOne()"
                    + ", and by testSplitSdrfWithBodyRowsCountGreaterThanOneByRowCountOfOne()"
            );
        }
        executeSplitByRowCountUsingRowOrientedDoc(origRowOrientedSdrf, bodyRowsCountPerSplitSdrf);
    }

    public void executeSplitByRowCountUsingRowOrientedDoc(final RowOrientedSdrfDocument origRowOrientedSdrf, 
            final int bodyRowsCountPerSplitSdrf) 
    throws Exception {
        final int bodyRowsCountInOrigSdrf = origRowOrientedSdrf.bodyRowsCount(); 
        List<RowOrientedSdrfDocument> splitSdrfs = new SdrfFileSplitter().splitByRowCount(origRowOrientedSdrf, 
                bodyRowsCountPerSplitSdrf);

        verifyNumberOfFilesProducedInSplit(bodyRowsCountInOrigSdrf, bodyRowsCountPerSplitSdrf, splitSdrfs);

        final String expectedHeaderRow = SdrfTestDataSets.DATA_SET_2.getHeader();
        verifyHeadersProducedInSplit(expectedHeaderRow, splitSdrfs);

        verifyBodyRowCountsProducedInSplit(bodyRowsCountInOrigSdrf, bodyRowsCountPerSplitSdrf, splitSdrfs);
    }

    private void verifyNumberOfFilesProducedInSplit(final int bodyRowsCountInOrigFile, 
            final int bodyRowsCountPerSplitFile, final List<RowOrientedSdrfDocument> splitSdrfs) {
        final int expectedNumberOfFiles = bodyRowsCountInOrigFile % bodyRowsCountPerSplitFile == 0 ? 
                bodyRowsCountInOrigFile / bodyRowsCountPerSplitFile
                : (bodyRowsCountInOrigFile / bodyRowsCountPerSplitFile) + 1;
        assertEquals(expectedNumberOfFiles, splitSdrfs.size());
    }

    private void verifyHeadersProducedInSplit(final String expectedHeaderRow, 
            final List<RowOrientedSdrfDocument> splitSdrfs) {
        for (RowOrientedSdrfDocument aSplitSdrf : splitSdrfs) {
            final String actualHeaderRow = aSplitSdrf.getHeaderRow().getRawString();
            assertEquals(expectedHeaderRow, actualHeaderRow);
        }
    }

    private void verifyBodyRowCountsProducedInSplit(final int bodyRowsCountInOrigFile, 
            final int bodyRowsCountPerSplitFile, 
            final List<RowOrientedSdrfDocument> splitSdrfs) {
        for (int i = 0; i < splitSdrfs.size(); i++) {
            final RowOrientedSdrfDocument aSplitSdrf = splitSdrfs.get(i); 
            if (i < splitSdrfs.size() - 1) {
                //all files except last one 
                assertEquals(bodyRowsCountPerSplitFile, aSplitSdrf.bodyRowsCount());
            } else { 
                //final file holds the left-over rows. 
                final int numberOfLeftOverRows = bodyRowsCountPerSplitFile == 1 ? 1 : 
                    bodyRowsCountInOrigFile % bodyRowsCountPerSplitFile;
                assertEquals(numberOfLeftOverRows, aSplitSdrf.bodyRowsCount());
            }
        }
    }


}
