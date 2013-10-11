//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import java.util.HashSet;
import java.util.Set;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProjectHandler extends AbstractAuditEntityHandler<Project> {

    /**
     * @param processor audit log processor
     */
    public ProjectHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "PMD.ExcessiveParameterList" })
    protected boolean logAddOrUpdate(AuditLogRecord record, Project project, String property, String columnName,
            Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if ("locked".equals(property)) {
            String message = "Experiment " + project.getExperiment().getTitle() + " "
                    + (Boolean.TRUE.equals(newVal) ? "Locked" : "Unlocked")
                    + (record.getType() == AuditType.INSERT ?  " when created" : "");
            getProcessor().addDetail(record, columnName, message, oldVal, newVal);
            getProcessor().addProjectSecurity(record, project, PERMISSIONS_PRIV_ID);
            addedEntry = true;
        } else if ("files".equals(property)) {
            Set<Long> newIds = new HashSet<Long>();
            for (CaArrayFile newFile : (Set<CaArrayFile>) newVal) {
                newIds.add(newFile.getId());
            }
            for (CaArrayFile oldFile : (Set<CaArrayFile>) oldVal) {
                if (!newIds.contains(oldFile.getId()) && oldFile.getParent() == null) {
                    getProcessor().addExperimentDetail(record, columnName, project);
                    getProcessor().addDetail(record, columnName, " - File " + oldFile.getName() + " deleted",
                            oldFile, null);
                    addedEntry = true;
                }
            }
            getProcessor().addProjectSecurity(record, project, READ_PRIV_ID);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
        return addedEntry;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logDelete(AuditLogRecord record, Project entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("experiment".equals(property)) {
            String msg = String.format("Experiment '%s' deleted", entity.getExperiment().getTitle());
            getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
            return true;
        }
        return false;
    }

}
