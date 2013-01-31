//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class ExperimentContactTest {

    @Test
    public void testGetPerson() {
        ExperimentContact ec = new ExperimentContact();

        //set experiment contact with null, Person and Org
        assertNull(ec.getPerson());

        try {
            ec.setContact(new Organization());
            ec.getPerson();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ise) {}

        ec.setContact(new Person());
        assertNotNull(ec.getPerson());
    }

    @Test
    public void testConstructor(){
        Experiment e = new Experiment();
        Person p = new Person();
        Collection<Term> terms = new ArrayList<Term>();
        terms.add(new Term());
        ExperimentContact ec = new ExperimentContact(e,p,terms);
        assertEquals(e,ec.getExperiment());
        assertEquals(p,ec.getPerson());
        assertEquals(1,ec.getRoles().size());

        Term term = new Term();
        term.setValue("test");
        ExperimentContact ec2 = new ExperimentContact(e,p,term);
        assertEquals(e,ec2.getExperiment());
        assertEquals(p,ec2.getPerson());
        assertEquals(1,ec2.getRoles().size());

        assertEquals("test",ec2.getRoleNames());
    }
}
