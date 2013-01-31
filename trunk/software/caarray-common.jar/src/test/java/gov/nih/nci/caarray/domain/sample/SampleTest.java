//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Tests for the Sample class.
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class SampleTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testMerge() throws Exception {
        Project p = new Project();
        Experiment e = new Experiment();
        p.setExperiment(e);
        Method setter = Experiment.class.getDeclaredMethod("setProject", Project.class);
        setter.setAccessible(true);
        setter.invoke(e, p);

        CollaboratorGroup cg1 = new CollaboratorGroup();
        cg1.setId(1L);
        p.addGroupProfile(cg1);

        CollaboratorGroup cg2 = new CollaboratorGroup();
        cg2.setId(2L);
        p.addGroupProfile(cg2);

        CollaboratorGroup cg3 = new CollaboratorGroup();
        cg3.setId(3L);
        p.addGroupProfile(cg3);

        CollaboratorGroup cg4 = new CollaboratorGroup();
        cg4.setId(4L);
        p.addGroupProfile(cg4);

        AccessProfile ap1 = null;
        AccessProfile ap2 = null;
        AccessProfile ap3 = null;
        AccessProfile ap4 = null;
        for (AccessProfile ap : p.getAllAccessProfiles()) {
            if (ap.getGroup() != null && ap.getGroup().equals(cg1)) {
                ap1 = ap;
            } else if (ap.getGroup() != null && ap.getGroup().equals(cg2)) {
                ap2 = ap;
            } else if (ap.getGroup() != null && ap.getGroup().equals(cg3)) {
                ap3 = ap;
            } else if (ap.getGroup() != null && ap.getGroup().equals(cg4)) {
                ap4 = ap;
            }
        }

        ap1.setId(1L);
        ap2.setId(2L);
        ap3.setId(3L);
        ap4.setId(4L);

        Source source1 = new Source();
        source1.setId(1L);
        source1.setName("source1");
        source1.setExperiment(e);
        e.getSources().add(source1);

        Source source2 = new Source();
        source1.setId(2L);
        source1.setName("source2");
        source2.setExperiment(e);
        e.getSources().add(source2);

        Extract extract1 = new Extract();
        extract1.setId(1L);
        extract1.setName("extract1");
        extract1.setExperiment(e);
        e.getExtracts().add(extract1);

        Extract extract2 = new Extract();
        extract2.setId(2L);
        extract2.setName("extract2");
        extract2.setExperiment(e);
        e.getExtracts().add(extract2);

        Sample sample1 = new Sample();
        sample1.setId(1L);
        sample1.setName("sample1");
        sample1.getSources().add(source1);
        source1.getSamples().add(sample1);
        sample1.getExtracts().add(extract1);
        sample1.setExperiment(e);
        e.getSamples().add(sample1);
        ap1.getSampleSecurityLevels().put(sample1, SampleSecurityLevel.READ_WRITE);
        ap2.getSampleSecurityLevels().put(sample1, SampleSecurityLevel.READ_WRITE);
        ap3.getSampleSecurityLevels().put(sample1, SampleSecurityLevel.NONE);
        assertNull(p.getGroupProfiles().get(cg4).getSampleSecurityLevels().get(sample1));

        Sample sample2 = new Sample();
        sample2.setId(2L);
        sample2.setName("sample2");
        sample2.getSources().add(source2);
        source2.getSamples().add(sample2);
        sample2.getExtracts().add(extract2);
        e.getSamples().add(sample2);
        sample2.setExperiment(e);
        ap1.getSampleSecurityLevels().put(sample2, SampleSecurityLevel.READ);
        ap3.getSampleSecurityLevels().put(sample2, SampleSecurityLevel.READ);
        ap4.getSampleSecurityLevels().put(sample2, SampleSecurityLevel.NONE);

        sample1.merge(sample2);

        assertEquals(1, source1.getSamples().size());
        assertEquals(1, source2.getSamples().size());
        assertEquals(2, sample1.getSources().size());
        assertEquals(2, sample1.getExtracts().size());
        assertEquals(SampleSecurityLevel.READ, p.getGroupProfiles().get(cg1).getSampleSecurityLevels().get(sample1));
        assertEquals(SampleSecurityLevel.READ_WRITE, p.getGroupProfiles().get(cg2).getSampleSecurityLevels().get(sample1));
        assertEquals(SampleSecurityLevel.NONE, p.getGroupProfiles().get(cg3).getSampleSecurityLevels().get(sample1));
        assertEquals(SampleSecurityLevel.NONE, p.getGroupProfiles().get(cg4).getSampleSecurityLevels().get(sample1));
    }

}
