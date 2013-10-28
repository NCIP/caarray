//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

/**
 * @author wcheng
 *
 */
public class AccessProfileHandlerTest extends AbstractAuditHandlerTest<AccessProfile> {
    private static final String SECURITY_LEVEL = "securityLevelInternal";
    private static final String SAMPLE_SECURITY_LEVELS = "sampleSecurityLevels";

    @Override
    protected void setupHandler() {
        handler = new AccessProfileHandler(processor);
    }

    @Override
    protected void setupEntity() {
        entity = new AccessProfile();
    }

    @Test
    public void addAccessProfile() {
        setupPublicProject();
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, SECURITY_LEVEL, null, SecurityLevel.NO_VISIBILITY));
        verify(processor).addDetail(record, SECURITY_LEVEL,
                "Public Access Profile for experiment TestExp set to NO_VISIBILITY", null,
                SecurityLevel.NO_VISIBILITY);
    }
    
    @Test
    public void updatePublicAccessProfile() {
        setupPublicProject();
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, SECURITY_LEVEL, SecurityLevel.NO_VISIBILITY, SecurityLevel.READ));
        verify(processor).addDetail(record, SECURITY_LEVEL,
                "Public Access Profile for experiment TestExp changed from NO_VISIBILITY to READ",
                SecurityLevel.NO_VISIBILITY, SecurityLevel.READ);
    }
    
    @Test
    public void updateGroupAccessProfile() {
        setupGroupProject();
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, SECURITY_LEVEL, SecurityLevel.NO_VISIBILITY, SecurityLevel.READ));
        verify(processor).addDetail(record, SECURITY_LEVEL,
                "Group Access Profile for experiment TestExp to group TestGroup changed from NO_VISIBILITY to READ",
                SecurityLevel.NO_VISIBILITY, SecurityLevel.READ);
    }
    
    @Test
    public void updateSampleSecurity() {
        setupPublicProject();
        Sample sampleA = new Sample();
        sampleA.setName("SampleA");
        Sample sampleB = new Sample();
        sampleB.setName("SampleB");
        Map<Sample, SampleSecurityLevel> oldVal = new HashMap<Sample, SampleSecurityLevel>();
        oldVal.put(sampleA, SampleSecurityLevel.NONE);
        Map<Sample, SampleSecurityLevel> newVal = new HashMap<Sample, SampleSecurityLevel>();
        newVal.put(sampleA, SampleSecurityLevel.READ);
        newVal.put(sampleB, SampleSecurityLevel.READ_WRITE);
        
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, SAMPLE_SECURITY_LEVELS, oldVal, newVal));
        verify(processor).addDetail(record, SAMPLE_SECURITY_LEVELS,
                "Access to samples on experiment TestExp's Public Access Profile");
        verify(processor).addDetail(record, SAMPLE_SECURITY_LEVELS,
                " - Access to sample SampleA changed from NONE to READ",
                SampleSecurityLevel.NONE, SampleSecurityLevel.READ);
        verify(processor).addDetail(record, SAMPLE_SECURITY_LEVELS,
                " - Access on sample SampleB set to READ_WRITE",
                null, SampleSecurityLevel.READ_WRITE);
    }
    
    private void setupPublicProject() {
        Project project = new Project();
        project.getExperiment().setTitle("TestExp");
        entity.setProjectForPublicProfile(project);
    }
    
    private void setupGroupProject() {
        Group group = new Group();
        group.setGroupName("TestGroup");
        CollaboratorGroup collabGroup = new CollaboratorGroup(group, new User());
        entity.setGroup(collabGroup);
        Project project = new Project();
        project.getExperiment().setTitle("TestExp");
        entity.setProjectForGroupProfile(project);
    }
}
