//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.ParameterValue;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import org.junit.Test;

/**
 * Tests for the Source class.
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class SourceTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testMerge() throws Exception {
        Term cellType = new Term();
        cellType.setValue("cellType1");

        Term diseaseState = new Term();
        diseaseState.setValue("diseaseState");

        Term materialType = new Term();
        materialType.setValue("materialType");

        Organism organism = new Organism();
        organism.setCommonName("organism");

        Term tissueSite = new Term();
        tissueSite.setValue("tissueSite");

        Term anotherTissueSite = new Term();
        anotherTissueSite.setValue("anotherTissueSite");

        Sample sample1 = new Sample();
        sample1.setId(1L);
        sample1.setName("sample1");

        Sample sample2 = new Sample();
        sample2.setId(2L);
        sample2.setName("sample2");

        Sample sample3 = new Sample();
        sample3.setId(3L);
        sample3.setName("sample3");

        Person provider1 = new Person();
        provider1.setId(1L);
        provider1.setFirstName("John");
        provider1.setLastName("Doe");

        Parameter parameter = new Parameter();
        parameter.setName("parameter");

        Protocol protocol = new Protocol();
        protocol.setName("protocol");

        ParameterValue pv1 = new ParameterValue();
        pv1.setParameter(parameter);
        pv1.setValue("1");

        ParameterValue pv2 = new ParameterValue();
        pv2.setParameter(parameter);
        pv2.setValue("2");

        ProtocolApplication pa1 = new ProtocolApplication();
        pa1.setProtocol(protocol);
        pa1.getValues().add(pv1);

        ProtocolApplication pa2 = new ProtocolApplication();
        pa2.setProtocol(protocol);
        pa2.getValues().add(pv2);

        ProtocolApplication pa3 = new ProtocolApplication();
        pa3.setProtocol(protocol);
        pa3.getValues().add(pv1);


        Source baseSource = new Source();
        baseSource.setCellType(cellType);
        baseSource.setDescription("Description");
        baseSource.setDiseaseState(diseaseState);
        baseSource.setMaterialType(materialType);
        baseSource.setName("source");
        baseSource.setOrganism(organism);
        baseSource.setTissueSite(tissueSite);
        baseSource.getSamples().add(sample1);
        baseSource.getSamples().add(sample2);
        baseSource.getProviders().add(provider1);
        baseSource.getProtocolApplications().add(pa1);

        Source otherSource = new Source();
        otherSource.setName("source");
        otherSource.getSamples().add(sample2);
        otherSource.getSamples().add(sample3);
        baseSource.getProtocolApplications().add(pa2);

        otherSource.merge(baseSource);

        assertEquals(cellType, otherSource.getCellType());
        assertEquals("Description", otherSource.getDescription());
        assertEquals(diseaseState, otherSource.getDiseaseState());
        assertEquals(materialType, otherSource.getMaterialType());
        assertEquals("source", otherSource.getName());
        assertEquals(organism, otherSource.getOrganism());
        assertEquals(tissueSite, otherSource.getTissueSite());
        assertEquals(3, otherSource.getSamples().size());
        assertTrue(otherSource.getSamples().contains(sample1));
        assertTrue(otherSource.getSamples().contains(sample2));
        assertTrue(otherSource.getSamples().contains(sample3));
        assertEquals(1, otherSource.getProviders().size());
        assertTrue(otherSource.getProviders().contains(provider1));
        assertEquals(2, otherSource.getProtocolApplications().size());
        checkProtocolApplications(pa1, pa2, otherSource);

        Source anotherSource = new Source();
        anotherSource.setTissueSite(anotherTissueSite);
        anotherSource.getProtocolApplications().add(pa3);

        otherSource.merge(anotherSource);
        assertEquals(tissueSite, otherSource.getTissueSite());
        assertEquals(2, otherSource.getProtocolApplications().size());
        checkProtocolApplications(pa1, pa2, otherSource);
    }

    private void checkProtocolApplications(ProtocolApplication pa1, ProtocolApplication pa2, Source otherSource) {
        ProtocolApplication retrievedPa1 = otherSource.getProtocolApplications().get(0);
        ProtocolApplication retrievedPa2 = otherSource.getProtocolApplications().get(1);
        if (pa1.getProtocol().equals(retrievedPa1.getProtocol())) {
            assertTrue(pa2.getProtocol().equals(retrievedPa2.getProtocol()));
        } else if (pa2.getProtocol().equals(retrievedPa1.getProtocol())) {
            assertTrue(pa1.getProtocol().equals(retrievedPa2.getProtocol()));
        } else {
            fail("Wrong protocol applications found in merged sources");
        }
    }

}
