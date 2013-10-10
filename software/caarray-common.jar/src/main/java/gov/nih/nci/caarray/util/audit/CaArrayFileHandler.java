//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

/**
 * @author wcheng
 *
 */
public class CaArrayFileHandler extends AbstractAuditEntityHandler<CaArrayFile> {

    /**
     * @param processor audit log processor
     */
    public CaArrayFileHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAddOrUpdate(AuditLogRecord record, CaArrayFile file, String property, 
            String columnName, Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if ("status".equals(property)
                && FileStatus.valueOf((String) newVal) == FileStatus.SUPPLEMENTAL) {
            getProcessor().addExperimentDetail(record, columnName, file.getProject());
            getProcessor().addDetail(record, columnName, " - Supplemental File " + file.getName() + " added",
                    oldVal, newVal);
            getProcessor().addProjectSecurity(record, file.getProject(), READ_PRIV_ID);
            addedEntry = true;
        }
        return addedEntry;
    }
}
