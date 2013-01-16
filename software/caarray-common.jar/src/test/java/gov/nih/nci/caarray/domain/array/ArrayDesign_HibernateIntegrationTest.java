//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
