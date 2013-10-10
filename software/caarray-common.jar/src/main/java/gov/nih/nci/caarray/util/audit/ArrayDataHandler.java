//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import java.util.HashSet;
import java.util.Set;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

/**
 * @author wcheng
 *
 */
public class ArrayDataHandler extends AbstractAuditEntityHandler<AbstractArrayData> {

    /**
     * @param processor audit log processor
     */
    public ArrayDataHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "PMD.ExcessiveParameterList" })
    protected boolean logAddOrUpdate(AuditLogRecord record, AbstractArrayData ad, String property, String columnName,
            Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if ("hybridizations".equals(property)) {
            Set<Hybridization> oldHybs = oldVal == null ? new HashSet<Hybridization>() : (Set<Hybridization>) oldVal;
            Set<Hybridization> newHybs = newVal == null ? new HashSet<Hybridization>() : (Set<Hybridization>) newVal;
            if (newHybs.size() > oldHybs.size()) {
                CaArrayFile file = ad.getDataFile();
                getProcessor().addExperimentDetail(record, columnName, file.getProject());
                getProcessor().addDetail(record, columnName, " - Data File " + file.getName() + " added", null,
                        newVal);
                getProcessor().addProjectSecurity(record, file.getProject(), READ_PRIV_ID);
                addedEntry = true;
            }
        }
        return addedEntry;
    }

}
