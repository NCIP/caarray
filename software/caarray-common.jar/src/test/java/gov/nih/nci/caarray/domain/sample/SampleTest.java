package gov.nih.nci.caarray.domain.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import org.junit.Test;

public class SampleTest {

    @Test
    public void testGetTissueSite() {
        Sample sample = new Sample();
        assertNull(sample.getTissueSite());
        Category nonTissueCat = new Category();
        nonTissueCat.setName(ExperimentOntologyCategory.CELL_TYPE.getCategoryName());
        ValueBasedCharacteristic nonTissueChar = new ValueBasedCharacteristic();
        nonTissueChar.setValue("Foo");
        nonTissueChar.setCategory(nonTissueCat);
        sample.getCharacteristics().add(nonTissueChar);
        assertNull(sample.getTissueSite());
        sample.getCharacteristics().clear();
        Category tissueCat = new Category();
        tissueCat.setName(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName());
        TermBasedCharacteristic tissueChar = new TermBasedCharacteristic();
        Term tissueSiteTerm = new Term();
        tissueSiteTerm.setCategory(tissueCat);
        tissueSiteTerm.setValue("Wowee");
        tissueChar.setCategory(tissueCat);
        tissueChar.setTerm(tissueSiteTerm);
        sample.getCharacteristics().add(tissueChar);
        Term retrievedTissueSite = sample.getTissueSite();
        assertNotNull(retrievedTissueSite);
        assertEquals("Wowee", tissueSiteTerm.getValue());
    }
}
