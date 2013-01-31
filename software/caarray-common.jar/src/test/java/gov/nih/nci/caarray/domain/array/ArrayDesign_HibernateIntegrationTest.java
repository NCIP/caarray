//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import org.junit.Test;

@SuppressWarnings("PMD")
public class ArrayDesign_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest {

    @Test
    @Override
    public void testSave() {
        super.testSave();
    }

    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {
        TermSource ts = new TermSource();
        ts.setName("TS 1");
        Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);
        ArrayDesign arrayDesign = (ArrayDesign) caArrayObject;
        CaArrayFile file1 = new CaArrayFile();
        file1.setName(getUniqueStringValue());
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
        AssayType nextAssayType = getNextValue(AssayType.values(), arrayDesign.getAssayTypeEnum());
        arrayDesign.setAssayType(nextAssayType.getValue());
        arrayDesign.setVersion(getUniqueStringValue());
        arrayDesign.setOrganism(new Organism());
        arrayDesign.getOrganism().setScientificName("Homo sapiens");
        arrayDesign.getOrganism().setTermSource(ts);
        ArrayDesignDetails designDetails = new ArrayDesignDetails();
        arrayDesign.setDesignDetails(designDetails);
        Feature feature = new Feature(designDetails);
        feature.setBlockColumn((short) getUniqueIntValue());
        feature.setBlockRow((short) getUniqueIntValue());
        feature.setColumn((short) getUniqueIntValue());
        feature.setRow((short) getUniqueIntValue());
        designDetails.getFeatures().add(feature);
        ProbeGroup probeGroup = new ProbeGroup(designDetails);
        designDetails.getProbeGroups().add(probeGroup);
        PhysicalProbe physicalProbe = new PhysicalProbe(designDetails, probeGroup);
        designDetails.getProbes().add(physicalProbe);
        ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        Gene gene = new Gene();
        gene.setEnsemblgeneID(getUniqueStringValue());
        gene.setFullName(getUniqueStringValue());
        gene.setSymbol(getUniqueStringValue());
        gene.setEntrezgeneID(getUniqueStringValue());
        gene.setGenbankAccession(getUniqueStringValue());
        gene.setGenbankAccessionVersion(getUniqueStringValue());
        gene.setUnigeneclusterID(getUniqueStringValue());
        annotation.setGene(gene);
        physicalProbe.setAnnotation(annotation);
    }

    @Override
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        ArrayDesign original = (ArrayDesign) caArrayObject;
        ArrayDesign retrieved = (ArrayDesign) retrievedCaArrayObject;
        assertEquals(original.getAssayTypeEnum(), retrieved.getAssayTypeEnum());
        assertEquals(original.getDesignFiles().size(), retrieved.getDesignFiles().size());
        assertEquals(original.getDesignFiles().iterator().next().getName(), retrieved.getDesignFiles().iterator()
                .next().getName());
        assertEquals(original.getName(), retrieved.getName());
        assertEquals(original.getNumberOfFeatures(), retrieved.getNumberOfFeatures());
        assertEquals(original.getPolymerType(), retrieved.getPolymerType());
        assertEquals(original.getPrinting(), retrieved.getPrinting());
        assertEquals(original.getProvider(), retrieved.getProvider());
        assertEquals(original.getSubstrateType(), retrieved.getSubstrateType());
        assertEquals(original.getSurfaceType(), retrieved.getSurfaceType());
        assertEquals(original.getTechnologyType(), retrieved.getTechnologyType());
        assertEquals(original.getAssayType(), retrieved.getAssayType());
        assertEquals(original.getVersion(), retrieved.getVersion());
        assertEquals(original.getDesignDetails(), retrieved.getDesignDetails());
        if (original.getDesignDetails() != null) {
            ArrayDesignDetails originalDetails = original.getDesignDetails();
            ArrayDesignDetails retrievedDetails = retrieved.getDesignDetails();
            assertEquals(originalDetails.getFeatures().size(), retrievedDetails.getFeatures().size());
            assertEquals(originalDetails.getFeatures().iterator().next().toString(),
                    retrievedDetails.getFeatures().iterator().next().toString());
            ExpressionProbeAnnotation originalAnnotation =
                (ExpressionProbeAnnotation) originalDetails.getProbes().iterator().next().getAnnotation();
            ExpressionProbeAnnotation retrievedAnnotation =
                (ExpressionProbeAnnotation) retrievedDetails.getProbes().iterator().next().getAnnotation();
            assertEquals(originalAnnotation.getGene().getFullName(), retrievedAnnotation.getGene().getFullName());
            assertEquals(originalAnnotation.getGene().getSymbol(), retrievedAnnotation.getGene().getSymbol());
            assertEquals(originalAnnotation.getGene().getEnsemblgeneID(), retrievedAnnotation.getGene().getEnsemblgeneID());
            assertEquals(originalAnnotation.getGene().getEntrezgeneID(), retrievedAnnotation.getGene().getEntrezgeneID());
            assertEquals(originalAnnotation.getGene().getGenbankAccession(), retrievedAnnotation.getGene().getGenbankAccession());
            assertEquals(originalAnnotation.getGene().getGenbankAccessionVersion(), retrievedAnnotation.getGene().getGenbankAccessionVersion());
            assertEquals(originalAnnotation.getGene().getUnigeneclusterID(), retrievedAnnotation.getGene().getUnigeneclusterID());
        }
    }

    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        ArrayDesign arrayDesign = (ArrayDesign) caArrayObject;
        arrayDesign.setNumberOfFeatures(null);
        arrayDesign.setPolymerType(null);
        arrayDesign.setPrinting(null);
        arrayDesign.setSubstrateType(null);
        arrayDesign.setSurfaceType(null);
    }

    @Override
    protected AbstractCaArrayObject createTestObject() {
        return new ArrayDesign();
    }

}
