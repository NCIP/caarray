//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.hybridization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for Hybridization class
 * @author dkokotov
 */
@SuppressWarnings("PMD")
public class HybridizationTest {
    @Test
    public void testGetCharacteristicsRecursively() {
        TermSource mged = new TermSource();
        mged.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mged.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());
        
        TermSource caarray = new TermSource();
        caarray.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarray.setVersion(ExperimentOntology.CAARRAY.getVersion());

        Category ds = new Category(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName(), mged);
        Category mt = new Category(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName(), mged);
        Category ct = new Category(ExperimentOntologyCategory.CELL_TYPE.getCategoryName(), mged);
        Category id = new Category(ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName(), caarray);
        Category c2 = new Category("Fake category", caarray);

        Term t1 = new Term();
        t1.setValue("Foo");
        t1.setCategory(ds);

        Term t2 = new Term();
        t2.setValue("Foo2");
        t2.setCategory(mt);

        Term t3 = new Term();
        t3.setValue("Foo3");
        t3.setCategory(ct);

        MeasurementCharacteristic mf = new MeasurementCharacteristic();
        mf.setCategory(c2);
        mf.setValue(1.0f);

        Source so1 = new Source();
        so1.setName("Foo");
        so1.setExternalId("X");
        so1.setMaterialType(t1);
        so1.setDiseaseState(t2);

        Sample sa1 = new Sample();
        sa1.setName("Foo");
        sa1.setExternalId("X2");        
        so1.getSamples().add(sa1);
        sa1.getSources().add(so1);
        sa1.setMaterialType(t2);
        sa1.setDiseaseState(t1);

        Sample sa2 = new Sample();
        sa2.setName("Foo");
        sa2.setExternalId("X3");
        sa2.setDiseaseState(t3);

        Extract ex1 = new Extract();
        ex1.setName("Foo");
        ex1.getSamples().add(sa1);
        sa1.getExtracts().add(ex1);
        ex1.getSamples().add(sa2);
        sa2.getExtracts().add(ex1);

        LabeledExtract le1 = new LabeledExtract();
        le1.setName("Foo");        
        le1.getExtracts().add(ex1);
        ex1.getLabeledExtracts().add(le1);

        LabeledExtract le2 = new LabeledExtract();
        le1.setName("Foo2");        

        Hybridization h1 = new Hybridization();
        h1.setName("foo");
        h1.getLabeledExtracts().add(le1);
        le1.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le2);
        le2.getHybridizations().add(h1);
        
        le2.getCharacteristics().add(mf);
        
        Set<AbstractCharacteristic> chars = h1.getCharacteristicsRecursively(mt);
        assertEquals(1, chars.size());
        AbstractCharacteristic char1 = chars.iterator().next();
        assertTrue(char1 instanceof TermBasedCharacteristic);
        assertEquals(mt.getName(), char1.getCategory().getName());
        assertEquals(t2, ((TermBasedCharacteristic) char1).getTerm());

        chars = h1.getCharacteristicsRecursively(ds);
        assertEquals(2, chars.size());
        List<String> termNames = new ArrayList<String>();
        Iterator<AbstractCharacteristic> charIt = chars.iterator();
        char1 = charIt.next();
        assertTrue(char1 instanceof TermBasedCharacteristic);
        assertEquals(ds.getName(), char1.getCategory().getName());
        termNames.add(((TermBasedCharacteristic) char1).getTerm().getValue());
        AbstractCharacteristic char2 = charIt.next();
        assertEquals(ds.getName(), char2.getCategory().getName());
        termNames.add(((TermBasedCharacteristic) char2).getTerm().getValue());
        Collections.sort(termNames);
        assertEquals("Foo", termNames.get(0));
        assertEquals("Foo3", termNames.get(1));
        
        chars = h1.getCharacteristicsRecursively(ct);
        assertEquals(0, chars.size());
        
        chars = h1.getCharacteristicsRecursively(id);
        assertEquals(2, chars.size());
        List<String> values = new ArrayList<String>();
        charIt = chars.iterator();
        char1 = charIt.next();
        assertTrue(char1 instanceof UserDefinedCharacteristic);
        assertEquals(id.getName(), char1.getCategory().getName());
        values.add(((UserDefinedCharacteristic) char1).getValue());
        char2 = charIt.next();
        assertEquals(id.getName(), char2.getCategory().getName());
        values.add(((UserDefinedCharacteristic) char2).getValue());
        Collections.sort(values);
        assertEquals("X2", values.get(0));
        assertEquals("X3", values.get(1));

        chars = h1.getCharacteristicsRecursively(c2);
        assertEquals(1, chars.size());
        assertEquals(mf, chars.iterator().next());
    }
    
    @Test
    public void testPropagateLastModifiedDataTime() {
        Date date = new Date();
        Hybridization h = new Hybridization();
        LabeledExtract le = new LabeledExtract();
        h.getLabeledExtracts().add(le);
        le.getHybridizations().add(h);
        h.propagateLastModifiedDataTime(date);
        assertEquals(date, le.getLastModifiedDataTime());
    }
}
