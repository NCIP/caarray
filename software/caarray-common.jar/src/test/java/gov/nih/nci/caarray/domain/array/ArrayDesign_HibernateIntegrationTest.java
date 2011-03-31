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
package gov.nih.nci.caarray.domain.array;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.net.URI;
import java.util.TreeSet;

import org.junit.Before;

@SuppressWarnings("PMD")
public class ArrayDesign_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest<ArrayDesign> {
    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");
    private static AssayType DUMMY_ASSAY_TYPE;

    @Before
    public void setUp() {
        // Initialize the dummy object needed for the tests.
        DUMMY_ASSAY_TYPE = new AssayType("Gene Expression");
        save(DUMMY_ASSAY_TYPE);
    }

    @Override
    protected void setValues(ArrayDesign arrayDesign) {
        super.setValues(arrayDesign);
        final TermSource ts = new TermSource();
        ts.setName("TS " + getUniqueStringValue());
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);
        final CaArrayFile file1 = new CaArrayFile();
        file1.setName(getUniqueStringValue());
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setDataHandle(DUMMY_HANDLE);
        arrayDesign.getDesignFiles().clear();
        arrayDesign.addDesignFile(file1);
        arrayDesign.setName(getUniqueStringValue());
        arrayDesign.setNumberOfFeatures(getUniqueIntValue());
        arrayDesign.setPolymerType(new Term());
        arrayDesign.getPolymerType().setValue("testval1");
        arrayDesign.getPolymerType().setCategory(cat);
        arrayDesign.getPolymerType().setSource(ts);
        arrayDesign.setPrinting(new ProtocolApplication());
        arrayDesign.setProvider(new Organization());
        arrayDesign.setSubstrateType(new Term());
        arrayDesign.getSubstrateType().setValue("testval2");
        arrayDesign.getSubstrateType().setCategory(cat);
        arrayDesign.getSubstrateType().setSource(ts);
        arrayDesign.setSurfaceType(new Term());
        arrayDesign.getSurfaceType().setValue("testval3");
        arrayDesign.getSurfaceType().setCategory(cat);
        arrayDesign.getSurfaceType().setSource(ts);
        arrayDesign.setTechnologyType(new Term());
        arrayDesign.getTechnologyType().setValue("testval4");
        arrayDesign.getTechnologyType().setCategory(cat);
        arrayDesign.getTechnologyType().setSource(ts);
        arrayDesign.setAssayTypes(new TreeSet<AssayType>());
        ;
        arrayDesign.getAssayTypes().add(DUMMY_ASSAY_TYPE);
        arrayDesign.setVersion(getUniqueStringValue());
        arrayDesign.setGeoAccession("GPL0000");
        arrayDesign.setOrganism(new Organism());
        arrayDesign.getOrganism().setScientificName("Homo sapiens");
        arrayDesign.getOrganism().setTermSource(ts);
        final ArrayDesignDetails designDetails = new ArrayDesignDetails();
        arrayDesign.setDesignDetails(designDetails);
        final Feature feature = new Feature(designDetails);
        feature.setBlockColumn((short) getUniqueIntValue());
        feature.setBlockRow((short) getUniqueIntValue());
        feature.setColumn((short) getUniqueIntValue());
        feature.setRow((short) getUniqueIntValue());
        designDetails.getFeatures().add(feature);
        final ProbeGroup probeGroup = new ProbeGroup(designDetails);
        designDetails.getProbeGroups().add(probeGroup);
        addExpressionProbeAnnotation(designDetails, probeGroup);
        addMiRNAProbeAnnotation(designDetails, probeGroup);
    }

    private void addExpressionProbeAnnotation(ArrayDesignDetails designDetails, ProbeGroup probeGroup) {
        final PhysicalProbe physicalProbe = new PhysicalProbe(designDetails, probeGroup);
        designDetails.getProbes().add(physicalProbe);

        final ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        final Gene gene = new Gene();
        gene.addAccessionNumber(getUniqueStringValue(), getUniqueStringValue());
        gene.addAccessionNumber(getUniqueStringValue(), getUniqueStringValue());
        annotation.setGene(gene);
        physicalProbe.setAnnotation(annotation);
    }

    private void addMiRNAProbeAnnotation(ArrayDesignDetails designDetails, ProbeGroup probeGroup) {
        final PhysicalProbe physicalProbe = new PhysicalProbe(designDetails, probeGroup);
        designDetails.getProbes().add(physicalProbe);

        final MiRNAProbeAnnotation annotation = new MiRNAProbeAnnotation();
        annotation.addAccessionNumber(getUniqueStringValue(), getUniqueStringValue());
        annotation.addAccessionNumber(getUniqueStringValue(), getUniqueStringValue());
        physicalProbe.setAnnotation(annotation);
    }

    @Override
    protected void compareValues(ArrayDesign original, ArrayDesign retrieved) {
        assertEquals(original.getAssayTypes(), retrieved.getAssayTypes());
        assertEquals(original.getDesignFiles().size(), retrieved.getDesignFiles().size());
        final CaArrayFile originalFile = original.getDesignFiles().iterator().next();
        final CaArrayFile retrievedFile = retrieved.getDesignFiles().iterator().next();
        assertEquals(originalFile.getName(), retrievedFile.getName());
        assertEquals(originalFile.getDataHandle(), retrievedFile.getDataHandle());
        assertEquals(original.getName(), retrieved.getName());
        assertEquals(original.getNumberOfFeatures(), retrieved.getNumberOfFeatures());
        assertEquals(original.getPolymerType(), retrieved.getPolymerType());
        assertEquals(original.getPrinting(), retrieved.getPrinting());
        assertEquals(original.getProvider(), retrieved.getProvider());
        assertEquals(original.getSubstrateType(), retrieved.getSubstrateType());
        assertEquals(original.getSurfaceType(), retrieved.getSurfaceType());
        assertEquals(original.getTechnologyType(), retrieved.getTechnologyType());
        assertEquals(original.getAssayTypes(), retrieved.getAssayTypes());
        assertEquals(original.getVersion(), retrieved.getVersion());
        assertEquals(original.getGeoAccession(), retrieved.getGeoAccession());
        assertEquals(original.getDesignDetails(), retrieved.getDesignDetails());
        if (original.getDesignDetails() != null) {
            final ArrayDesignDetails originalDetails = original.getDesignDetails();
            final ArrayDesignDetails retrievedDetails = retrieved.getDesignDetails();
            assertEquals(originalDetails.getFeatures().size(), retrievedDetails.getFeatures().size());
            assertEquals(originalDetails.getFeatures().iterator().next().toString(), retrievedDetails.getFeatures()
                    .iterator().next().toString());

            compareExpressionProbeAnnotations(originalDetails, retrievedDetails);
            compareMiRNAProbeAnnotations(originalDetails, retrievedDetails);
        }
    }

    private void compareExpressionProbeAnnotations(ArrayDesignDetails originalDetails,
            ArrayDesignDetails retrievedDetails) {

        final ExpressionProbeAnnotation originalAnnotation = getAnnotation(ExpressionProbeAnnotation.class,
                originalDetails);
        final ExpressionProbeAnnotation retrievedAnnotation = getAnnotation(ExpressionProbeAnnotation.class,
                retrievedDetails);
        assertEquals(originalAnnotation.getGene().getFullName(), retrievedAnnotation.getGene().getFullName());
        assertEquals(originalAnnotation.getGene().getSymbol(), retrievedAnnotation.getGene().getSymbol());
        assertThat(retrievedAnnotation.getGene().getAccessionNumbers(getUniqueStringValue()), is(originalAnnotation
                .getGene().getAccessionNumbers(getUniqueStringValue())));
    }

    private void compareMiRNAProbeAnnotations(ArrayDesignDetails originalDetails, ArrayDesignDetails retrievedDetails) {

        final MiRNAProbeAnnotation originalAnnotation = getAnnotation(MiRNAProbeAnnotation.class, originalDetails);
        final MiRNAProbeAnnotation retrievedAnnotation = getAnnotation(MiRNAProbeAnnotation.class, retrievedDetails);
        assertThat(retrievedAnnotation.getAccessionNumbers(getUniqueStringValue()),
                is(originalAnnotation.getAccessionNumbers(getUniqueStringValue())));
    }

    @SuppressWarnings("unchecked")
    private <T> T getAnnotation(Class<T> classtype, ArrayDesignDetails arrayDesignDetails) {
        for (final PhysicalProbe probe : arrayDesignDetails.getProbes()) {
            final AbstractProbeAnnotation annotation = probe.getAnnotation();
            if (classtype.isInstance(annotation)) {
                return (T) annotation;
            }
        }
        return null;
    }

    @Override
    protected void setNullableValuesToNull(ArrayDesign arrayDesign) {
        arrayDesign.setNumberOfFeatures(null);
        arrayDesign.setPolymerType(null);
        arrayDesign.setPrinting(null);
        arrayDesign.setSubstrateType(null);
        arrayDesign.setSurfaceType(null);
    }

    @Override
    protected ArrayDesign createTestObject() {
        return new ArrayDesign();
    }

}
