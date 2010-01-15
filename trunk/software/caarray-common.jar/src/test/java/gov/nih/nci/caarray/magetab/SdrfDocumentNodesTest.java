/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Extract;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocumentNodes;
import gov.nih.nci.caarray.magetab.sdrf.Source;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the SdrfDocumentNodes class.
 *
 * @author Rashmi Srinivasa
 */
public class SdrfDocumentNodesTest extends AbstractCaarrayTest {
    private final SdrfDocumentNodes sdrfDocumentNodes = new SdrfDocumentNodes();

    private final Set<gov.nih.nci.caarray.magetab.sdrf.Source> allSources =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Source>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Sample> allSamples =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Sample>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Extract> allExtracts =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Extract>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.LabeledExtract> allLabeledExtracts =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.LabeledExtract>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Hybridization> allHybridizations =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Hybridization>();
    private final Set<ArrayDataFile> allArrayDataFiles = new HashSet<ArrayDataFile>();
    private final Set<DerivedArrayDataFile> allDerivedArrayDataFiles = new HashSet<DerivedArrayDataFile>();
    private final Set<ArrayDataMatrixFile> allArrayDataMatrixFiles = new HashSet<ArrayDataMatrixFile>();
    private final Set<DerivedArrayDataMatrixFile> allDerivedArrayDataMatrixFiles =
        new HashSet<DerivedArrayDataMatrixFile>();

    /**
     * Create a few nodes to initialize the SdrfDocumentNodes object with.
     */
    @Before
    public void setup() throws IOException {
        Source source = new Source();
        Sample sample = new Sample();
        sample.getPredecessors().add(source);
        source.getSuccessors().add(sample);
        Extract extract1 = new Extract();
        extract1.getPredecessors().add(sample);
        sample.getSuccessors().add(extract1);
        Extract extract2 = new Extract();
        extract2.getPredecessors().add(sample);
        sample.getSuccessors().add(extract2);
        allSources.add(source);
        allSamples.add(sample);
        allExtracts.add(extract1);
        allExtracts.add(extract2);
    }

    @Test
    public void testInitializeNodesComplete() {
        // Initialize the SDRF document with all the object graph nodes.
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, allExtracts, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, allArrayDataMatrixFiles, allDerivedArrayDataFiles,
                allDerivedArrayDataMatrixFiles);
        assertTrue("All SDRF document nodes have not been initialized.", sdrfDocumentNodes.isInitialized());
    }

    @Test
    public void testInitializeNodesIncomplete() {
        // Initialize the SDRF document with an incomplete set of nodes.
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, null, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, null, null, null);
        assertFalse("All SDRF document nodes have been initialized.", sdrfDocumentNodes.isInitialized());
    }

    @Test
    public void testAllNodes() {
        // Initialize the SDRF document with all the object graph nodes.
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, allExtracts, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, allArrayDataMatrixFiles, allDerivedArrayDataFiles,
                allDerivedArrayDataMatrixFiles);
        assertEquals(1, sdrfDocumentNodes.getAllSources().size());
        assertEquals(1, sdrfDocumentNodes.getAllSamples().size());
        assertEquals(2, sdrfDocumentNodes.getAllExtracts().size());
        assertEquals(0, sdrfDocumentNodes.getAllLabeledExtracts().size());
        assertEquals(0, sdrfDocumentNodes.getAllHybridizations().size());
        assertEquals(0, sdrfDocumentNodes.getAllArrayDataFiles().size());
        assertEquals(0, sdrfDocumentNodes.getAllArrayDataMatrixFiles().size());
        assertEquals(0, sdrfDocumentNodes.getAllDerivedArrayDataFiles().size());
        assertEquals(0, sdrfDocumentNodes.getAllDerivedArrayDataMatrixFiles().size());
    }
}
