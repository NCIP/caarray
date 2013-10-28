//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.google.common.collect.Sets;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;

/**
 * @author wcheng
 *
 */
public class ExperimentHandlerTest extends AbstractAuditHandlerTest<Experiment> {
    private static final String EXP_NAME = "TestExp";
    private static final String PUBLICATIONS = "publications";

    @Override
    protected void setupHandler() {
        handler = new ExperimentHandler(processor);
    }

    @Override
    protected void setupEntity() {
        entity = new Experiment();
        entity.setTitle(EXP_NAME);
        entity.setProject(new Project());
    }

    @Test
    public void updateExperimentProperties() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "title"));
        assertTrue(logEntity(record, "description"));
        assertTrue(logEntity(record, "assayTypes"));
        assertTrue(logEntity(record, "manufacturer"));
        assertTrue(logEntity(record, "arrayDesigns"));
        assertTrue(logEntity(record, "organism"));
        assertTrue(logEntity(record, "experimentDesignTypes"));
        assertTrue(logEntity(record, "designDescription"));
        assertTrue(logEntity(record, "qualityControlTypes"));
        assertTrue(logEntity(record, "qualityControlDescription"));
        assertTrue(logEntity(record, "replicateTypes"));
        assertTrue(logEntity(record, "replicateDescription"));
        assertFalse(logEntity(record, "invalidField"));
        verify(processor).addExperimentDetail(record, "title", entity.getProject());
        verify(processor).addDetail(record, "description", " - Description updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "assayTypes", " - Assay Types updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "manufacturer", " - Provider updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "arrayDesigns", " - Array Designs updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "organism", " - Organism updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "experimentDesignTypes", " - Experiment Design Types updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "designDescription", " - Experiment Design Description updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "qualityControlTypes", " - Quality Control Types updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "qualityControlDescription", " - Quality Control Description updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "replicateTypes", " - Replicate Types updated", OLD_VALUE, NEW_VALUE);
        verify(processor).addDetail(record, "replicateDescription", " - Replicate Description updated", OLD_VALUE, NEW_VALUE);
    }
    
    @Test
    public void updateExperimentCollections() {
        Publication pub1 = new Publication();
        Publication pub2 = new Publication();
        Publication pub3 = new Publication();
        pub1.setTitle("pub1");
        pub2.setTitle("pub2");
        pub3.setTitle("pub3");
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, PUBLICATIONS, Sets.newHashSet(pub1, pub2), Sets.newHashSet(pub2, pub3)));
        verify(processor).addExperimentDetail(record, PUBLICATIONS, entity.getProject());
        verify(processor).addDetail(record, PUBLICATIONS, " - Publication pub3 added", null, pub3);
        verify(processor).addDetail(record, PUBLICATIONS, " - Publication pub1 deleted", pub1, null);
    }
}
